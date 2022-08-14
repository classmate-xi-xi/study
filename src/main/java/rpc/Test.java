package rpc;

import java.net.InetSocketAddress;

/**
 * @author: ywx
 * @description
 * @Date: 2022/08/14
 */

public class Test {
    public static void main(String[] args) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RpcServer rpcServer = new RpcServer();
//                rpcServer.register(Tinterface.class, TinterfaceImpl.class);
//                rpcServer.start(10000);
//            }
//        }).start();

        Tinterface tinterface = RpcClient.getRemoteProxyObj(Tinterface.class, new InetSocketAddress("localhost", 10000));
        System.out.println(tinterface.send("rpc 测试用例"));
    }
}
