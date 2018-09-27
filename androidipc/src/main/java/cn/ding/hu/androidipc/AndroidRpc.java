package cn.ding.hu.androidipc;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by harry.ding on 2018/9/26.
 */

public class AndroidRpc {

    public static int MSG_INVOKE_MSG = 1;

    private static HashMap<String, Class> rpcMap = new HashMap();

    private AndroidRpc() {
        throw new UnsupportedOperationException("u can't instantiate AndroidRpc...");
    }

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;
    public static String sServicePkgName;
    public static Object waitObj = new Object();

    public static void init(@NonNull final Application app, String servicePkgName) {
        if (sApplication == null) {
            sApplication = app;
            sServicePkgName = servicePkgName;
        }
    }

    public static void register(Class serviceInterface, Class serviceInterfaceImpl) {
        rpcMap.put(serviceInterface.getName(), serviceInterfaceImpl);
    }


    public static Class getService(String serviceName) {
        return rpcMap.get(serviceName);
    }


    public static Object getRpcService(final Class<?> cls, final AndroidRpcActivty activity) {
        return Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class[]{cls},
                new InvocationHandler() {
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        System.out.println("开始事务111");

                        Messenger messenger = activity.getmServerMessenger();
                        Message message = Message.obtain();
                        message.what = MSG_INVOKE_MSG;
                        message.replyTo = activity.getmClientMessenger();
                        Bundle bundle = new Bundle();
                        bundle.putString("serviceName", cls.getName());
                        bundle.putString("methodName", method.getName());
                        bundle.putSerializable("parameterTypes", method.getParameterTypes());
                        bundle.putSerializable("arguments", args);
                        message.setData(bundle);
                        messenger.send(message);
                        //阻塞线程等待返回

                        //发送消息
                        System.out.println("提交事务111");
                        return activity.result;
                    }
                }
        );
    }
}
