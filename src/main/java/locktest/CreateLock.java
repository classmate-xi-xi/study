package locktest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author: ywx
 * @description 自定义锁 不重入锁
 * @Date: 2022/09/01
 */

/**
 * 1.implements lock
 * 2.静态内部类，extend AbstractQueuedSynchronizer
 */
public class CreateLock implements Lock {
    /**
     * 3.重写AQS的方法，达到自定义预期
     */
    private static class Sync extends AbstractQueuedSynchronizer {
        /**
         * 重写排他锁 加锁
         *
         * @param arg
         * @return
         */
        @Override
        protected boolean tryAcquire(int arg) {
            if (!Thread.currentThread().getName().startsWith("s1")) {
                return false;
            }
            // cas 加锁成功 or 失败
            if (compareAndSetState(0, arg)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 重写排他锁 解锁
         *
         * @param arg
         * @return
         */
        @Override
        protected boolean tryRelease(int arg) {
            // 在解锁前判断 是否有锁 如果state为0 抛出异常 不存在锁
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            // 为什么不使用cas进行解锁？
            // 既然能够解锁，说明线程本身就持有该锁，持有锁即拥有线程自己的单线程执行空间
            setState(0);
//            setExclusiveOwnerThread(null);
            return true;
        }

        Condition getCondition() {
            return new ConditionObject();
        }
    }

    /**
     * 创建一个内部类
     */
    private Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.getCondition();
    }
}
