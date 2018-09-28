package cn.ding.hu.androidipclib;

import android.util.Log;

import cn.ding.hu.androidipclib.server.MainActivity;

/**
 * Created by harry.ding on 2018/9/27.
 */

public class IRpcServiceImpl implements IRpcService {
    @Override
    public String getName() {
        Log.i("rpc", "rpc call getName");
        return "dinghu-rpc-lixi";
    }

    @Override
    public int getCal(int a, int b) {
        return a + b + MainActivity.t;
    }

    @Override
    public User getUser() {
        return new User("dinghu",20,"1.73cm");
    }
}
