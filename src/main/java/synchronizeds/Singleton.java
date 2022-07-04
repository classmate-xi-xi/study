package synchronizeds;

/**
 * @author: ywx
 * @description 单例--双重检验锁
 * @Date: 2022/07/03
 */

public class Singleton {
    /**
     * volatile 可以禁止JVM的指令重排
     */
    private volatile static Singleton uniqueInstance;

    private Singleton() {
    }

    public static Singleton getUniqueInstance() {
        //先判断对象是否已经实例过，没有实例化过才进入加锁代码
        if (uniqueInstance == null) {
            //类对象加锁
            synchronized (Singleton.class) {
                if (uniqueInstance == null) {
                    /**
                     * 1.为 uniqueInstance 分配内存空间
                     * 2.初始化 uniqueInstance
                     * 3.将 uniqueInstance 指向分配的内存地址
                     */
                    uniqueInstance = new Singleton();
                }
            }
        }
        return uniqueInstance;
    }
}
