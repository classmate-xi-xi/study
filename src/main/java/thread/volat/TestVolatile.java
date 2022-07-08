package thread.volat;

/**
 * @author: ywx
 * @description
 * @Date: 2022/07/08
 */

public class TestVolatile {
    public static int a = 0;
    public static boolean flag = false;

    public static void main(String[] args) {
        while (a < 1000) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    a += 1;
                    flag = true;
                }
            }, "a").start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (flag) {
                        a += 1;
                        System.out.println(flag);
                    }
                }
            }, "b").start();
        }
    }

}
