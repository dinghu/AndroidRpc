# AndroidRpc
android跨进程调用框架，基于messenger进行进程间通信

示例demo：https://github.com/dinghu/AndroidRpcClientDemo

AndroidRpc 使用方法：

1.添加gradle配置；
	
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  2.添加依赖；
  
  dependencies {
	        implementation 'com.github.dinghu:AndroidRpc:1.0.10'
	}
  
  3.application中初始化向外提供服务接口的App包名；
  
  AndroidRpc.init(getApplication(),"cn.ding.hu.androidipclib");
  
  4.继承AndroidRpcActivty；
  
  5.调用AndroidRpc.connect链接服务；
  
  6.使用AndroidRpc.invoke进行远程rpc，ipc调用；
  例如：
  
AndroidRpc.invoke(MainActivity.this, IRpcService.class, "getCal",
                        new IRpcInvokeListener<Integer>() {
                            @Override
                            public void onResult(Integer s) {
                                Log.i("rpc"," getCal.result:"+ s);
                            }
                        },5,6);
