package invoke;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: ywx
 * @description 反射test
 * @Date: 2022/07/03
 */

public class InvokeMain {

    public static void main(String[] args)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Class<?> testClass = Class.forName("invoke.TestInvoke");
        TestInvoke testInvoke = (TestInvoke) testClass.newInstance();
        Method[] methods = testClass.getMethods();
        for (Method m : methods
        ) {
            System.out.println(m.getName());
        }
        Method pm = testClass.getDeclaredMethod("publicMethod", String.class);
        pm.invoke(testInvoke, "1111");

        Field fields = testClass.getDeclaredField("value");

        fields.setAccessible(true);

        fields.set(testInvoke, "2222");

        Method pme = testClass.getDeclaredMethod("privateMethod");
        pme.setAccessible(true);
        pme.invoke(testInvoke);
    }
}
