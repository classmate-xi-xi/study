package proxy.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author: ywx
 * @description
 * @Date: 2022/08/26
 */

public class MyInvocationHandler implements InvocationHandler {

    /**
     * 代理类中的对象
     */
    private final Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * 动态代理对象调用原生方法的时候，最终实际上调用到的是 invoke() 方法
     * invoke()方法代替我们去调用了被代理对象的原生方法
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //调用方法前，添加自己的操作
        System.out.println("before method " + method.getName());
        Object result = method.invoke(target, args);
        //调用方法之后，添加自己的操作
        System.out.println("after method " + method.getName());
        return result;
    }
}
