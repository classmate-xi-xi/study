package rpc;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author: ywx
 * @description
 * @Date: 2022/08/14
 */

public class RpcServer {
    private static LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(30);
    private static ThreadPoolExecutor executorService =
            new ThreadPoolExecutor(
                    8,
                    8,
                    1000L,
                    TimeUnit.SECONDS,
                    queue);

    private static final ConcurrentHashMap<String, Class> SERVICE_REGISTER = new ConcurrentHashMap<>();

    public void register(Class service, Class impl) {
        SERVICE_REGISTER.put(service.getSimpleName(), impl);
    }

    public void start(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(port));
            System.out.println("start........" + SERVICE_REGISTER);
            while (true) {
                executorService.execute(new Task(serverSocket.accept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Task implements Runnable {
        Socket client = null;

        public Task(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;

            try {
                inputStream = new ObjectInputStream(client.getInputStream());
                String serviceName = inputStream.readUTF();
                String methodName = inputStream.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) inputStream.readObject();
                Object[] arguments = (Object[]) inputStream.readObject();
                Class serviceClass = SERVICE_REGISTER.get(serviceName);
                if (serviceClass == null) {
                    throw new ClassNotFoundException(serviceName + "NotFound!");
                }
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                Object result = method.invoke(serviceClass.newInstance(), arguments);

                outputStream = new ObjectOutputStream(client.getOutputStream());
                outputStream.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (outputStream != null || inputStream != null || client != null) {
                    try {
                        Objects.requireNonNull(outputStream).close();
                        Objects.requireNonNull(inputStream).close();
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        RpcServer server = new RpcServer();
        server.register(Tinterface.class, TinterfaceImpl.class);
        server.start(10000);
    }
}
