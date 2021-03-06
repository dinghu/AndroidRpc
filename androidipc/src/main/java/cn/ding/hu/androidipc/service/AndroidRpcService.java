package cn.ding.hu.androidipc.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Method;

import cn.ding.hu.androidipc.AndroidRpc;

public class AndroidRpcService extends Service {

    @SuppressLint("HandlerLeak")
    private Messenger messenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == AndroidRpc.MSG_INVOKE_MSG) {
                Bundle bundleReq = msg.getData();
                if (bundleReq != null) {
                    try {
                        Class<?>[] parameterTypes = (Class<?>[]) bundleReq.getSerializable("parameterTypes");
                        Object[] arguments = (Object[]) bundleReq.getSerializable("arguments");
                        String serviceName = bundleReq.getString("serviceName");
                        String methodName = bundleReq.getString("methodName");

                        if (TextUtils.isEmpty(methodName) || "toString".equals(methodName)) {
                            return;
                        }

                        Class serviceClass = AndroidRpc.getService(serviceName);
                        if (serviceClass == null) {
                            Log.e("rpc", serviceName + " not found");
                            return;
                        }
                        Method method = serviceClass.getMethod(methodName, parameterTypes);
                        Object result = method.invoke(serviceClass.newInstance(), arguments);

                        //发送返回结果到客户端
                        Messenger clientMessenger = msg.replyTo;
                        Message message = Message.obtain();
                        message.what = AndroidRpc.MSG_INVOKE_MSG;
                        Gson gson = new Gson();
                        Bundle bundleRet = msg.getData();
                        bundleRet.putSerializable("resultType", method.getReturnType());
                        bundleRet.putString("resultData", gson.toJson(result));
                        bundleRet.putString("methodName", methodName);
                        message.setData(bundleRet);
                        clientMessenger.send(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
