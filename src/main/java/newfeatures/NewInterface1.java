package newfeatures;

/**
 * @author: ywx
 * @description
 * @Date: 2022/07/04
 */

public interface NewInterface1 {

    static void sm() {
        System.out.println("");
    }

    default void f() {
        System.out.println(2);
    }
    void def();
}
