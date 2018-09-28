package cn.ding.hu.androidipc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by harry.ding on 2018/9/27.
 */

public class AndroidRpcActivty extends AppCompatActivity {


    private static final String BIND_SERVICE_ACTION = "android.intent.action.ICALL_MESSENGER_RPC";

    private static final String BIND_MESSENGER_SERVICE_COMPONENT_NAME_CLS = "cn.ding.hu.androidipc.service.MessengerService";

    //给服务端发送消息的Messenger
    private Messenger mServerMessenger;

    public Messenger getmServerMessenger() {
        return mServerMessenger;
    }

    public Messenger getmClientMessenger() {
        return mClientMessenger;
    }

    protected Object result;

    private IRpcListener iRpcListener;

    private HashMap<String, IRpcInvokeListener> rpcInvokeListenerHashMap = new HashMap<>();

    public void addRpcInvokeListener(String key, IRpcInvokeListener iRpcInvokeListener) {
        rpcInvokeListenerHashMap.put(key, iRpcInvokeListener);
    }


    public void setiRpcListener(IRpcListener iRpcListener) {
        this.iRpcListener = iRpcListener;
    }

    //客户端接受服务端消息的Messnger
    @SuppressLint("HandlerLeak")
    private Messenger mClientMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            if (msg.what == AndroidRpc.MSG_INVOKE_MSG) {
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    try {
                        String resultName = bundle.getString("resultName");
                        String resultData = bundle.getString("resultData");
                        String methodName = bundle.getString("methodName");

                        Class resultClass = Class.forName(resultName);
                        Gson gson = new Gson();

                        if (!TextUtils.isEmpty(resultData)) {
                            result = gson.fromJson(resultData, resultClass);
                        } else {
                            result = null;
                        }

                        IRpcInvokeListener iRpcInvokeListener = rpcInvokeListenerHashMap.get(methodName);
                        if (iRpcInvokeListener != null) {
                            iRpcInvokeListener.onResult(result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServerMessenger = new Messenger(service);
            if (iRpcListener != null) {
                iRpcListener.onConnect();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServerMessenger = null;
        }
    };

    public void startService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(BIND_SERVICE_ACTION);
        serviceIntent.setComponent(new ComponentName(AndroidRpc.mServicePkgName, BIND_MESSENGER_SERVICE_COMPONENT_NAME_CLS));
        startService(serviceIntent);
    }

    public void startAndBindService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(BIND_SERVICE_ACTION);
        serviceIntent.setComponent(new ComponentName(AndroidRpc.mServicePkgName, BIND_MESSENGER_SERVICE_COMPONENT_NAME_CLS));

        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unBindService() {
        unbindService(serviceConnection);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startAndBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindService();
    }
}
