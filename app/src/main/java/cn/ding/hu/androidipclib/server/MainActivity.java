package cn.ding.hu.androidipclib.server;

import android.os.Bundle;

import cn.ding.hu.androidipc.AndroidRpc;
import cn.ding.hu.androidipc.AndroidRpcActivty;
import cn.ding.hu.androidipclib.IRpcService;
import cn.ding.hu.androidipclib.IRpcServiceImpl;
import cn.ding.hu.androidipclib.R;

public class MainActivity extends AndroidRpcActivty {

    public static  int t = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidRpc.init(getApplication(),"cn.ding.hu.androidipclib");
        AndroidRpc.register(IRpcService.class,IRpcServiceImpl.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidRpc.startService(this);
    }
}
