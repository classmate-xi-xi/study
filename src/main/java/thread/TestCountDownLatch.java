package thread;

import java.util.concurrent.CountDownLatch;

/**
 * @author: ywx
 * @description TestCountDownLatch
 * @Date: 2022/09/16
 */

public class TestCountDownLatch {
    static class PreTaskThread implements Runnable {
        private String task;
        private CountDownLatch countDownLatch;

        public PreTaskThread(String task, CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
            this.task = task;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                System.out.println(task + "---over");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        //主任务
        new Thread(() -> {
            try {
                System.out.println("等待数据加载...");
                System.out.println(String.format("还有%d个前置任务", countDownLatch.getCount()));
                countDownLatch.await();
                System.out.println("数据加载完成，正式开始游戏！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        // 前置任务
        new Thread(new TestCountDownLatch.PreTaskThread("加载⼈物模型", countDownLatch)).start();
        new Thread(new TestCountDownLatch.PreTaskThread("加载地图数据", countDownLatch)).start();
        new Thread(new TestCountDownLatch.PreTaskThread("加载背景⾳乐", countDownLatch)).start();
    }
}
