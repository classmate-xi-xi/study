package thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: ywx
 * @description
 * @Date: 2022/07/09
 */

public class TestThreadPool {

//    public void execute(Runnable command) {
//        if (command == null)
//            throw new NullPointerException();
//        /*
//         * Proceed in 3 steps:
//         *
//         * 1. If fewer than corePoolSize threads are running, try to
//         * start a new thread with the given command as its first
//         * task.  The call to addWorker atomically checks runState and
//         * workerCount, and so prevents false alarms that would add
//         * threads when it shouldn't, by returning false.
//         *
//         * 2. If a task can be successfully queued, then we still need
//         * to double-check whether we should have added a thread
//         * (because existing ones died since last checking) or that
//         * the pool shut down since entry into this method. So we
//         * recheck state and if necessary roll back the enqueuing if
//         * stopped, or start a new thread if there are none.
//         *
//         * 3. If we cannot queue task, then we try to add a new
//         * thread.  If it fails, we know we are shut down or saturated
//         * and so reject the task.
//         */
//        int c = ctl.get();
//        // 1.当前线程数⼩于corePoolSize,则调⽤addWorker创建核⼼线程执⾏任务
//        if (workerCountOf(c) < corePoolSize) {
//            if (addWorker(command, true))
//                return;
//            c = ctl.get();
//        }
//        //如果不⼩于corePoolSize，则将任务添加到workQueue队列
//        if (isRunning(c) && workQueue.offer(command)) {
//            int recheck = ctl.get();
//            // 如果isRunning返回false(状态检查)，则remove这个任务，然后执⾏拒绝策略。
//            if (!isRunning(recheck) && remove(command))
//                reject(command);
//            else if (workerCountOf(recheck) == 0)
//                //线程池处于running状态，但是没有线程，则创建线程
//                addWorker(null, false);
//
//        } else// 如果放⼊workQueue失败，则创建⾮核⼼线程执⾏任务，
//            // 如果这时创建⾮核⼼线程失败(当前线程总数不⼩于maximumPoolSize时)，就会执⾏拒绝策略
//            if (!addWorker(command, false))
//                reject(command);
//    }
//
//    private boolean addWorker(Runnable firstTask, boolean core) {
//        retry:
//        //判断线程数量是否超出阈值
//        for (; ; ) {
//            int c = ctl.get();
//            int rs = runStateOf(c);
//
//            // Check if queue empty only if necessary.
//            if (rs >= SHUTDOWN &&
//                    !(rs == SHUTDOWN &&
//                            firstTask == null &&
//                            !workQueue.isEmpty()))
//                return false;
//
//            for (; ; ) {
//                int wc = workerCountOf(c);
//                if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize))
//                    return false;
//                if (compareAndIncrementWorkerCount(c))
//                    break retry;
//                c = ctl.get();  // Re-read ctl
//                if (runStateOf(c) != rs)
//                    continue retry;
//                // else CAS failed due to workerCount change; retry inner loop
//            }
//        }
//        //开启线程
//        boolean workerStarted = false;
//        boolean workerAdded = false;
//        ThreadPoolExecutor.Worker w = null;
//        try {
//            w = new ThreadPoolExecutor.Worker(firstTask);
//            final Thread t = w.thread;
//            if (t != null) {
//                //线程池全局锁
//                final ReentrantLock mainLock = this.mainLock;
//                mainLock.lock();
//                try {
//                    // Recheck while holding lock.
//                    // Back out on ThreadFactory failure or if
//                    // shut down before lock acquired.
//                    int rs = runStateOf(ctl.get());
//
//                    if (rs < SHUTDOWN ||
//                            (rs == SHUTDOWN && firstTask == null)) {
//                        if (t.isAlive()) // precheck that t is startable
//                            throw new IllegalThreadStateException();
//                        workers.add(w);
//                        int s = workers.size();
//                        if (s > largestPoolSize)
//                            largestPoolSize = s;
//                        workerAdded = true;
//                    }
//                } finally {
//                    mainLock.unlock();
//                }
//                //启动线程
//                if (workerAdded) {
//                    t.start();
//                    workerStarted = true;
//                }
//            }
//        } finally {
//            if (!workerStarted)
//                addWorkerFailed(w);
//        }
//        return workerStarted;
//    }
//
//    //只要 getTask ⽅法不返回 null ,此线程就不会退出
//    //先去执⾏创建这个worker时就有的任务，当执⾏完这个任务后，
//    // worker的⽣命周期并没有结束，在 while 循环中，
//    // worker会不断地调⽤ getTask ⽅法从阻塞队列中
//    // 获取任务然后调⽤ task.run() 执⾏任务,从⽽达到复⽤线程的⽬的
//    final void runWorker(Worker w) {
//        Thread wt = Thread.currentThread();
//        Runnable task = w.firstTask;
//        w.firstTask = null;
//        //线程启动之后，释放锁
//        w.unlock(); // allow interrupts
//        boolean completedAbruptly = true;
//        try {
//            //Worker执⾏firstTask或从workQueue中获取任务，如果getTask⽅法不返回null
//            while (task != null || (task = getTask()) != null) {
//                //进⾏加锁操作，保证thread不被其他线程中断
//                w.lock();
//                // If pool is stopping, ensure thread is interrupted;
//                // if not, ensure thread is not interrupted.  This
//                // requires a recheck in second case to deal with
//                // shutdownNow race while clearing interrupt
//                //检查线程池状态，倘若线程池处于中断状态，当前线程将中断
//                if ((runStateAtLeast(ctl.get(), STOP)
//                        || (Thread.interrupted()
//                        && runStateAtLeast(ctl.get(), STOP)))
//                        && !wt.isInterrupted())
//                    wt.interrupt();
//                try {
//                    //beforeExecute(wt, task); 啥也没干
//                    beforeExecute(wt, task);
//                    Throwable thrown = null;
//                    try {
//                        //执行task
//                        task.run();
//                    } catch (RuntimeException x) {
//                        thrown = x;
//                        throw x;
//                    } catch (Error x) {
//                        thrown = x;
//                        throw x;
//                    } catch (Throwable x) {
//                        thrown = x;
//                        throw new Error(x);
//                    } finally {
//                        //.
//                        afterExecute(task, thrown);
//                    }
//                } finally {
//                    //结束任务 并释放锁
//                    task = null;
//                    w.completedTasks++;
//                    w.unlock();
//                }
//            }
//            completedAbruptly = false;
//        } finally {
//            processWorkerExit(w, completedAbruptly);
//        }
//    }
//
//
//    private Runnable getTask() {
//        boolean timedOut = false; // Did the last poll() time out?
//
//        for (; ; ) {
//            int c = ctl.get();
//            int rs = runStateOf(c);
//
//            // Check if queue empty only if necessary.
//            //检查线程的状态是否为shutdown stop 和 work队列 为空直接销毁
//            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
//                decrementWorkerCount();
//                return null;
//            }
//
//            int wc = workerCountOf(c);
//
//            // Are workers subject to culling?
//            // allowCoreThreadTimeOut变量默认是false,核⼼线程即使空闲也不会被销毁
//            // 如果为true,核⼼线程在keepAliveTime内仍空闲则会被销毁。
//            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
//
//            // 2.如果运⾏线程数超过了最⼤线程数，但是缓存队列已经空了，这时递减worker数量。
//            // 如果有设置允许线程超时或者线程数量超过了核⼼线程数量，
//            // 并且线程在规定时间内均未poll到任务且队列为空则递减worker数量
//            if ((wc > maximumPoolSize || (timed && timedOut))
//                    && (wc > 1 || workQueue.isEmpty())) {
//                if (compareAndDecrementWorkerCount(c))
//                    return null;
//                continue;
//            }
//            /**
//             * 核⼼线程的会⼀直卡在 workQueue.take ⽅法，被阻塞并挂起，不会占⽤CPU资源，
//             * 直到拿到 Runnable 然后返回
//             * （当然如果allowCoreThreadTimeOut设置为true,那么核⼼线程就会去调⽤poll⽅法，因为poll可能会返回null,
//             * 所以这时候核⼼线程满⾜超时条件也会被销毁）
//             *
//             * ⾮核⼼线程会workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS)，
//             * 如果超时还没有拿到，下⼀次循环判断compareAndDecrementWorkerCount就会返回 null,
//             * Worker对象的run()⽅法循环体的判断为null,任务结束，然后线程被系统回收
//             */
//            try {
//                // 如果timed为true(想想哪些情况下timed为true),则会调⽤workQueue的poll⽅法
//                // 超时时间是keepAliveTime。如果超过keepAliveTime时⻓，
//                // poll返回了null，上边提到的while循序就会退出，线程也就执⾏完了。
//                // 如果timed为false（allowCoreThreadTimeOut为falsefalse
//                // 且wc > corePoolSize为false），则会调⽤workQueue的take⽅法阻塞在当前。
//                // 队列中有任务加⼊时，线程被唤醒，take⽅法返回任务，并执⾏。
//                Runnable r = timed ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) : workQueue.take();
//                if (r != null)
//                    return r;
//                timedOut = true;
//            } catch (InterruptedException retry) {
//                timedOut = false;
//            }
//        }
//    }

    public static void main(String[] args) {
        LinkedBlockingQueue queue = new LinkedBlockingQueue();
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(3, 5, 1000, TimeUnit.SECONDS, queue);
    }
}
