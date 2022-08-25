package proxy.jdkproxy;

/**
 * @author: ywx
 * @description 代理方法的实现类
 * @Date: 2022/08/26
 */

public class ServiceProxyImpl implements ServiceProxy {
    @Override
    public String send(String msg) {
        System.out.println("send msg:" + msg);
        return msg;
    }
}
