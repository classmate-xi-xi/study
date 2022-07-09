package thread;

import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * @author: ywx
 * @description
 * @Date: 2022/07/04
 */

public class TestThreadLocal implements Runnable {

    private static final ThreadLocal<SimpleDateFormat> f =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd HHmm"));

    /**
     * private static final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>(){
     *     @Override
     *     protected SimpleDateFormat initialValue(){
     *         return new SimpleDateFormat("yyyyMMdd HHmm");
     *     }
     * };
     */
    @Override
    public void run() {
        System.out.println("Thread Name:" + Thread.currentThread().getName() + "default Formatter = " + f.get().toPattern());
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        f.set(new SimpleDateFormat());

        System.out.println("Thread Name= " + Thread.currentThread().getName() + " formatter = " + f.get().toPattern());

    }

    public static void main(String[] args) throws InterruptedException {
        TestThreadLocal threadLocal = new TestThreadLocal();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(threadLocal, "" + i);
            Thread.sleep(1000);
            thread.start();
        }
    }
}
