package test;

/**
 * @author: ywx
 * @description
 * @Date: 2022/07/03
 */

public class Test {

    public static void main(String[] args) throws ClassNotFoundException {
        Test.class.getClassLoader().loadClass("test.LoadClassTest");
    }
}
