package cn.ding.hu.androidipclib;

import android.util.Log;

/**
 * Created by harry.ding on 2018/9/27.
 */

public class IRpcServiceImpl implements IRpcService {
    @Override
    public String getName() {
        Log.i("rpc","rpc call getName");
        return "dinghu-rpc";
    }
}
