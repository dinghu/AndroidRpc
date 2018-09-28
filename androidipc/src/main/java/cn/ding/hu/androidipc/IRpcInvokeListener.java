package cn.ding.hu.androidipc;

/**
 * Created by harry.ding on 2018/9/28.
 */

public interface IRpcInvokeListener<T> {
    void onResult(T t);
}
