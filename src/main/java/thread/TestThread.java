package thread;

/**
 * @author: ywx
 * @description test死锁
 * @Date: 2022/07/03
 */

public class TestThread {
    private static Object object1 = new Object();
    private static Object object2 = new Object();

    /**
     * 死锁产生条件
     * 1.互斥条件 该时段内只有一个线程占用资源 2.请求与保持条件 一个线程因为请求资源而阻塞时，对自己已有的资源不进行释放
     * 3.不剥夺条件 线程在已获得资源的情况下未使用完之前将不能被其他的线程所强行剥夺，只有自己使用完毕之后才进行释放资源
     * 4.循环等待条件 若干的线程之前形成一种链式循环首尾相链接相互请求对方所占用的资源进而在等待的关系。
     */
    public static void main(String[] args) {

        new Thread(() -> {
            synchronized (object1) {
                System.out.println(Thread.currentThread()+"get obj1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread()+"waiting get resource2");
                synchronized (object2) {
                    System.out.println(Thread.currentThread()+"get obj2");
                }
            }
        },"线程1").start();

        new Thread(() -> {
            synchronized (object2) {
                System.out.println(Thread.currentThread()+"get obj2");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "waiting get resource1");
                synchronized (object1) {
                    System.out.println(Thread.currentThread()+"get obj1");
                }
            }
        },"线程2").start();
    }
}

/**
 * 如何解决死锁问题
 * 1. 破坏请求与保持条件 一次性申请所有的资源
 * 2. 破坏不剥夺条件 占用部分资源的进程进一步申请其他资源时，如果申请不到，就将其所占用的资源主动释放
 * 3. 破坏循环等待条件 按顺序申请资源来预防，释放资源则反序释放。破坏循环等待条件
 * 解决死锁
 * new Thread(() -> {
 *             synchronized (object1) {
 *                 System.out.println(Thread.currentThread()+"get obj1");
 *                 try {
 *                     Thread.sleep(1000);
 *                 } catch (InterruptedException e) {
 *                     e.printStackTrace();
 *                 }
 *                 System.out.println(Thread.currentThread() + "waiting get resource2");
 *                 synchronized (object2) {
 *                     System.out.println(Thread.currentThread()+"get obj2");
 *                 }
 *             }
 *         },"线程2").start();
 */
