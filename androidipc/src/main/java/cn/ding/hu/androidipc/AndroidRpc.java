package cn.ding.hu.androidipc;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;

import java.lang.reflect.Method;
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
    public static String mServicePkgName;

    public static void init(@NonNull final Application app, String servicePkgName) {
        if (sApplication == null) {
            sApplication = app;
            mServicePkgName = servicePkgName;
        }
    }

    public static void startService(AndroidRpcActivty activity) {
        activity.startAndBindService();
    }

    public static void connect(AndroidRpcActivty activity, IRpcListener rpcListener) {
        activity.startAndBindService();
        activity.setiRpcListener(rpcListener);
    }

    public static void register(Class serviceInterface, Class serviceInterfaceImpl) {
        rpcMap.put(serviceInterface.getName(), serviceInterfaceImpl);
    }


    public static Class getService(String serviceName) {
        return rpcMap.get(serviceName);
    }
//
//    public static class AndroidRpcInvocationHandler implements InvocationHandler {
//        private AndroidRpcActivty androidRpcActivty;
//        private Class<?> cls;
//
//        public AndroidRpcInvocationHandler(Class<?> cls, AndroidRpcActivty androidRpcActivty) {
//            this.androidRpcActivty = androidRpcActivty;
//            this.cls = cls;
//        }
//
//        @Override
//        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            System.out.println("开始事务111");
//
//            Messenger messenger = androidRpcActivty.getmServerMessenger();
//            Message message = Message.obtain();
//            message.what = MSG_INVOKE_MSG;
//            message.replyTo = androidRpcActivty.getmClientMessenger();
//            Bundle bundle = new Bundle();
//            bundle.putString("serviceName", cls.getName());
//            bundle.putString("methodName", method.getName());
//            bundle.putSerializable("parameterTypes", method.getParameterTypes());
//            bundle.putSerializable("arguments", args);
//            message.setData(bundle);
//            messenger.send(message);
//            //阻塞线程等待返回
//
//            //发送消息
//            System.out.println("提交事务111");
//            return null;
//        }
//    }


    public static <T> void invoke(final AndroidRpcActivty activity,Class rpcServiceCls, String methodName, IRpcInvokeListener<T> invokeListener, Object... args) {

        try {
            Method[] methods = rpcServiceCls.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(methodName)) {
                    Method method = methods[i];
                    activity.addRpcInvokeListener(method.getName(),invokeListener);
                    Messenger messenger = activity.getmServerMessenger();
                    Message message = Message.obtain();
                    message.what = MSG_INVOKE_MSG;
                    message.replyTo = activity.getmClientMessenger();
                    Bundle bundle = new Bundle();
                    bundle.putString("serviceName", rpcServiceCls.getName());
                    bundle.putString("methodName", method.getName());
                    bundle.putSerializable("parameterTypes", method.getParameterTypes());
                    bundle.putSerializable("arguments", args);
                    message.setData(bundle);
                    messenger.send(message);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static Object getRpcService(final Class<?> cls, final AndroidRpcActivty activity) {
//        return Proxy.newProxyInstance(
//                cls.getClassLoader(),
//                new Class[]{cls},
//                new AndroidRpcInvocationHandler(cls, activity)
//        );
//    }
}

