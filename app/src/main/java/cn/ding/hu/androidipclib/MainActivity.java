package cn.ding.hu.androidipclib;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.ding.hu.androidipc.AndroidRpc;
import cn.ding.hu.androidipc.AndroidRpcActivty;

public class MainActivity extends AndroidRpcActivty {
    IRpcService rpcService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidRpc.init(getApplication(),"cn.ding.hu.androidipclib");
        AndroidRpc.register(IRpcService.class,IRpcServiceImpl.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rpcService = (IRpcService)AndroidRpc.getRpcService(IRpcService.class,this);
        rpcService.getName();
    }
}
