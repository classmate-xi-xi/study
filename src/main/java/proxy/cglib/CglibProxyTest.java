package proxy.cglib;

/**
 * @author: ywx
 * @description Test Cglib Proxy
 * @Date: 2022/08/26
 */

public class CglibProxyTest {
    public static void main(String[] args) {
        ProxyService service = (ProxyService) CglibProxyFactory.getProxy(ProxyService.class);
        service.send("1111111");
    }
}
