package thread;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author: ywx
 * @description
 * @Date: 2022/07/09
 */

public class TestBlockingQueue {
    private int queueSize = 10;
    private ArrayBlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(10);

    class Consumer extends Thread {
        @Override
        public void run() {
            consumer();
        }

        private void consumer() {
            while (true) {
                try {
                    blockingQueue.take();
                    System.out.println("取走一个元素,剩余："+ queueSize);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Produce extends Thread{
        @Override
        public void run() {
            produce();
        }

        private void produce(){
            while (true) {
                try {
                    blockingQueue.put(1);
                    System.out.println("向队列取一个，剩余"+blockingQueue.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public static void main(String[] args) {
        TestBlockingQueue queue = new TestBlockingQueue();
        Produce produce = queue.new Produce();
        Consumer consumer = queue.new Consumer();
        produce.start();
        consumer.start();
    }
}
