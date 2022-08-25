package proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author: ywx
 * @description 自定义MethodInterceptor
 * @Date: 2022/08/26
 */

public class MyMethodInterceptor implements MethodInterceptor {
    /**
     * @param o           代理对象（增强的对象）
     * @param method      被拦截的方法（需要增强的方法）
     * @param objects     方法入参
     * @param methodProxy 用于调用原始方法
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        // 调用方法之前，添加自己的操作
        System.out.println("我在这个方法前干了啥 before method。。。。。 " + method.getName());
        Object obj = methodProxy.invokeSuper(o, objects);
        //调用方法之后，我们同样可以添加自己的操作
        System.out.println("我在这个方法后干了啥 after method...... " + method.getName());
        return obj;
    }
}
