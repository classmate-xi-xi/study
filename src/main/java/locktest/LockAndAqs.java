package locktest;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: ywx
 * @description
 * @Date: 2022/09/01
 */

public class LockAndAqs {

    /**
     * 非公平锁
     */
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        /**
         * 1. lock中很多地方调用AQS里面的模板方法
         * 2. 模板方法是直接可以进行使用，对于AQS里面的模板方法会调用我们复写的方法 可以进行对五个方法复写
         * （tryAcquire tryRelease）这两个方法需要我们进行自定义实现  排他锁
         * isHeldExclusively 看线程的执行拥有锁情况
         * （tryReleaseShared tryAcquireShared）共享锁
         *
         * you --> Lock.lock --> sync.acquire(模板方法) -->  Lock 里面你自己复写的五个方法之一(自己决定复写哪个)
         */
        try {
            /**
             * AQS 方法:
             * 1 acquire 的模板方法
             * 2 tryAcquire 使我们复写的acquire(AQS) 会调用tryAcquire
             * addWaiter 它主要是将我们获取锁失败的线程添加到AQS里面一个FIFo双端队列里面，我们称之为同步队列
             * acquireQueued
             * compareAndSetState
             * getState 我们的lock不推荐在for循环内部，因为每次循环都会进行一个+1的操作，所以我们的lock方法推荐放在for外
             *
             * 公平锁：现ABC三个线程 A获得锁 BC失败 BC会加入到同步队列的尾端
             *       A释放锁 B收到唤醒通知，进行争抢 这时候D进来了 发现B在排队（不严谨，D调用AQS里的has。。。方法）
             *       放弃争抢直接加入同步队列的尾端
             *       公平锁一定公平 因为D线程在过来的时候会在通过一个has。。方法进行判断
             * 非公平锁：现ABC三个线程 A获得锁 BC失败 BC会加入到同步队列的尾端
             *         A释放锁 B收到唤醒通知，进行争抢 这时候D进来了
             *         直接与B进行争抢 如果D失败加入同步队列尾端
             *         如果D成功，对B线程不公平（D插队）
             *         C不会争抢锁 因为只有同步队列的前置节点通知后继节点才会唤醒，C只能被B唤醒
             *         唤醒的前提是B线程获取了锁并且B线程释放锁之后才会通知C线程来进行竞争锁
             * 公平锁和非公平锁都是使用的同一个同步队列的实现，不要觉得队列都是按照顺序走的就是公平的
             * 公平与非公平是发生在争抢锁的时刻。
             */
            lock.lock();
//            final void lock() {
//                // cas 比较交换获取锁
//                if (compareAndSetState(0, 1))
//                    // 设置当前线程为持有锁的线程
//                    setExclusiveOwnerThread(Thread.currentThread());
//                else
//                    acquire(1);
//            }
//            public final void acquire(int arg) {
//                if (!tryAcquire(arg) &&
//                        //addWaiter() 将线程加入同步队列
//                        acquireQueued(addWaiter(AbstractQueuedSynchronizer.Node.EXCLUSIVE), arg))
//                    selfInterrupt();
//            }

            /**
             * AQS 方法: 尝试加锁 成功就成功 失败就失败
             * lock 必须要进行加锁，不成功便一直进行等待，进入我们的同步队列
             * tryLock 只尝试一次
             * 如果你的线程不希望被阻塞，可以将tryLock和while循环结合
             * 如果tryLock失败那就借助while循环再次进行尝试加锁 （其实就是一个while+tryLock的CAS加锁操作）
             *
             */
            lock.tryLock();
            /**
             * AQS 方法: 可以超时获取锁
             */
            public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
                return sync.tryAcquireNanos(1, unit.toNanos(timeout));
            }
            //tryAcquireNanos
            public final boolean tryAcquireNanos(int arg, long nanosTimeout)
            throws InterruptedException {
                if (Thread.interrupted())
                    throw new InterruptedException();
                return tryAcquire(arg) ||
                        doAcquireNanos(arg, nanosTimeout);
            }
            //tryAcquire
            protected final boolean tryAcquire(int acquires) {
                return nonfairTryAcquire(acquires);
            }
            //nonfairTryAcquire
            final boolean nonfairTryAcquire(int acquires) {
                final Thread current = Thread.currentThread();
                int c = getState();
                if (c == 0) {
                    if (compareAndSetState(0, acquires)) {
                        setExclusiveOwnerThread(current);
                        return true;
                    }
                }
                // 可重入 判断当前线程是否是获取锁的线程
                else if (current == getExclusiveOwnerThread()) {
                    int nextc = c + acquires;
                    // int的上限 2的31次方
                    if (nextc < 0) // overflow
                        throw new Error("Maximum lock count exceeded");
                    setState(nextc);
                    return true;
                }
                return false;
            }
            //shouldParkAfterFailedAcquire
            private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
                int ws = pred.waitStatus;
                if (ws == Node.SIGNAL)
                    return true;
                if (ws > 0) {
                    do {
                        node.prev = pred = pred.prev;
                    } while (pred.waitStatus > 0);
                    pred.next = node;
                } else {
                    compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
                }
                return false;
            }
            lock.tryLock(0, null);
            /**
             * AQS 方法: 可中断获取锁
             */
            lock.lockInterruptibly();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // (非公平锁) tryRelease 只有持有锁的线程才能进行解锁release操作
            lock.unlock();
        }

    }
}
