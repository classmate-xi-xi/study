package rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * @author: ywx
 * @description
 * @Date: 2022/08/14
 */

public class RpcClient<T> {
    public static <T> T getRemoteProxyObj(final Class<T> service, final InetSocketAddress addr) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = null;
                ObjectInputStream inputStream = null;
                ObjectOutputStream outputStream = null;
                try {
                    socket = new Socket();
                    socket.connect(addr);
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeUTF(service.getSimpleName());
                    outputStream.writeUTF(method.getName());

                    System.out.println(service.getSimpleName());
                    System.out.println(method.getName());

                    outputStream.writeObject(method.getParameterTypes());
                    outputStream.writeObject(args);
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    return inputStream.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    outputStream.close();
                    inputStream.close();
                    socket.close();
                }
                return null;
            }
        });
    }

    public static void main(String[] args) {
        Tinterface tinterface = RpcClient.getRemoteProxyObj(Tinterface.class, new InetSocketAddress("localhost", 10000));
        System.out.println(tinterface.send("rpc 测试用例"));
        System.out.println(tinterface.sends(""));
    }
}
