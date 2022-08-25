package proxy.cglib;

/**
 * @author: ywx
 * @description 需要代理的服务
 * @Date: 2022/08/26
 */

public class ProxyService {
    public String send(String message) {
        System.out.println("send message:" + message);
        return message;
    }
}
