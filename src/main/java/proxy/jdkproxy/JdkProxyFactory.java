package proxy.jdkproxy;

import java.lang.reflect.Proxy;

/**
 * @author: ywx
 * @description 获取代理对象的工厂类
 * @Date: 2022/08/26
 */

public class JdkProxyFactory {
    /**
     *
     * @param target 代理的目标对象的实例化对象
     * @return
     */
    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                // 目标类的类加载
                target.getClass().getClassLoader(),
                // 代理需要实现的接口，可指定多个
                target.getClass().getInterfaces(),
                // 代理对象对应的自定义 InvocationHandler
                new MyInvocationHandler(target)
        );
    }
}
