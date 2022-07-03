package invoke;

/**
 * @author: ywx
 * @description
 * @Date: 2022/07/03
 */

public class TestInvoke {

    private String value;

    public TestInvoke() {
        value = "test";
    }

    public void publicMethod(String s) {
        System.out.println("I " + s);
    }

    private void privateMethod() {
        System.out.println("value is " + value);
    }

}
