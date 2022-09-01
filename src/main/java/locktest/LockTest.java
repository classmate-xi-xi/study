package locktest;

/**
 * @author: ywx
 * @description testLock
 *
 * Lock 具有高扩展性，就是因为它是通过实现lock以及内部类继承AQS 即兴的自我代码重写 所有的逻辑都由自己实现。
 *
 * @Date: 2022/09/01
 */

public class LockTest {
    static CreateLock createLock = new CreateLock();

    public static void testLock() {
        createLock.lock();
        try {
            System.out.println("获取到了锁，线程名称为 = " + Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            createLock.unlock();
        }

    }

    public static void main(String[] args) {
        System.out.println(31 >>> 1);
        Thread a = new Thread(() -> {
            testLock();
            while (true) {
            }
        });
        a.setName("s1+a");

        Thread b = new Thread(() -> {
            testLock();
        });
        b.setName("s1");
        a.start();
        b.start();

    }
}
