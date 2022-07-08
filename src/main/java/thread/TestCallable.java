package thread;

import javafx.concurrent.Task;

import java.util.concurrent.*;

/**
 * @author: ywx
 * @description
 * @Date: 2022/07/08
 */

public class TestCallable implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        return null;
    }

    public static void main(String[] args) {
        Thread testThread = new Thread(() -> {
            System.out.println("testThread当前线程组名字：" +
                    Thread.currentThread().getThreadGroup().getName());
            System.out.println("testThread线程名字：" +
                    Thread.currentThread().getName());
        });
        testThread.start();
        System.out.println("执⾏main⽅法线程名字：" + Thread.currentThread().getName());
    }
}
