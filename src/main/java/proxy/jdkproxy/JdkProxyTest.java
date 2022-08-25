package proxy.jdkproxy;

/**
 * @author: ywx
 * @description
 * @Date: 2022/08/26
 */

public class JdkProxyTest {
    public static void main(String[] args) {
        //实例化需要被代理的对象 代理的目标对象 new ServiceProxyImpl()
        ServiceProxy serviceProxy = (ServiceProxy) JdkProxyFactory.getProxy(new ServiceProxyImpl());
        serviceProxy.send("111111");
    }
}
