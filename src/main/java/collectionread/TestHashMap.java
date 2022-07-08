package collectionread;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @author: ywx
 * @description
 * @Date: 2022/07/06
 */

public class TestHashMap {

    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c;
            Type[] ts, as;
            Type t;//ts 里面的元素
            ParameterizedType p; //ArrayList<String>
            if ((c = x.getClass()) == String.class) // 如果是String类型 直接返回 String自己实现了Comparable且是final
                return c;//return String.class
            if ((ts = c.getGenericInterfaces()) != null) { //【List<object>,RandomAccess,clon,seri】
                for (int i = 0; i < ts.length; ++i) {
                    //List<object>
                    if (((t = ts[i]) instanceof ParameterizedType)
                            // List<Map<String,String>> 说明他不是个Comparable return null
                            && ((p = (ParameterizedType) t).getRawType() == Comparable.class) //List.class
                            && (as = p.getActualTypeArguments()) != null // [Object.class]
                            && as.length == 1 && as[0] == c) // type arg is c 只取一条
                        return c;
                }
            }
        }
        return null;
    }

    static final int tableSizeFor(int cap) {//取出一个数字的最接近的2的n次方(向上取)
        int n = cap - 1;//9
        n |= n >>> 1;    // 1001 | 0100 = 1101
        n |= n >>> 2;   // 1101 | 0011 = 1111
        n |= n >>> 4;//1111
        n |= n >>> 8;//1111
        n |= n >>> 16;//1111
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1; //n + 1 = 1 0000
    }

    final HashMap.Node<K, V> getNode(int hash, Object key) {
        HashMap.Node<K, V>[] tab;
        //链表的头结点,e 临时赋值
        HashMap.Node<K, V> first, e;
        int n;
        K k;

        if ((tab = table) != null && (n = tab.length) > 0 &&
                //(n - 1) & hash
                //(n - 1) & (h = key.hashCode()) ^ (h >>> 16)
                (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && // always check first node
                    ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                if (first instanceof HashMap.TreeNode)
                    return ((HashMap.TreeNode<K, V>) first).getTreeNode(hash, key);
                do {
                    //对象相同hash一定相同
                    if (e.hash == hash && //如果hash相同的两个对象,他们的对象不一定相等;hash是一个int类型 使用== 运算加快
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

    //put方法
    //onlyIfAbsent = false; evict = true
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        HashMap.Node<K, V>[] tab;
        HashMap.Node<K, V> p;
        int n, i;
        tab = table;
        n = tab.length;
        if (tab == null || n == 0)
            //resize 扩容 默认16
            n = (tab = resize()).length;
        //tab[i = (n - 1) & hash] 计算槽位
        if ((p = tab[i = (n - 1) & hash]) == null)
            //没有数据直接添加node
            tab[i] = newNode(hash, key, value, null);
        else {
            HashMap.Node<K, V> e;
            K k;
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if (p instanceof HashMap.TreeNode) //如果是tree 就进行红黑树的转化
                e = ((HashMap.TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
            else {//链表
                for (int binCount = 0; ; ++binCount) {//循环链表
                    e = p.next;
                    if (e == null) {
                        //尾插法
                        p.next = newNode(hash, key, value, null);
                        //大于等于八 TREEIFY_THRESHOLD - 1
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);//链表转红黑树
                        break;
                    }
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    //p = p.next;
                    p = e;
                }
            }
            //覆盖 key相等的时候
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                //onlyIfAbsent 是个false
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                //对hashMap无用 模板方法模式
                afterNodeAccess(e);
                //返回旧值
                return oldValue;
            }
        }
        //操作修改加一
        ++modCount;
        //hashMap是先添加后扩容 ArrayList是先扩容再添加
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }

    //扩容resize
    final HashMap.Node<K, V>[] resize() {
        HashMap.Node<K, V>[] oldTab = table; //假设old为16
        int oldCap = (oldTab == null) ? 0 : oldTab.length;//16
        int oldThr = threshold; //12
        int newCap, newThr = 0;
        //=============================计算==============================================
        if (oldCap > 0) {
            //oldCap 大于等于 max容量
            if (oldCap >= MAXIMUM_CAPACITY) { //放弃扩容 直接干到最大
                threshold = Integer.MAX_VALUE;//threshold 这个值只与扩容有关 它的大小不会限制其他的操作
                return oldTab;
                //(newCap = oldCap << 1) => newCap = (oldCap * 2) 且 oldCap >=16
            } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY
                    && oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold => newThr = oldThr * 2
        } else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float) newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
                    (int) ft : Integer.MAX_VALUE);
        }
        //=============================赋值==============================================
        // 真正的开始给threshold 进行了newThr的赋值
        threshold = newThr;
        @SuppressWarnings({"rawtypes", "unchecked"})
        HashMap.Node<K, V>[] newTab = (HashMap.Node<K, V>[]) new HashMap.Node[newCap];
        // 真正的开始给table 进行了newTab的赋值
        table = newTab;

        //==========================节点迁移(原位置和新位置)=================================================
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                HashMap.Node<K, V> e;
                if ((e = oldTab[j]) != null) {
                    //GC oldTab[j] = null;
                    oldTab[j] = null;
                    //当前不是链节点
                    if (e.next == null)
                        //单个节点 非链表非树
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof HashMap.TreeNode)
                        //如果进行map扩容,我们的树肯定会被拆掉 重新分配节点 会有性能消耗
                        //实际情况下 map很难达到红黑树 hash十分散列,业务代码中不可能也不能够不允许在一个map里放置这么多元素
                        //如果我需要从db里取出 2的30次方数据 需要用hashMap 怎么办?
                        //: clear()方法进行复用 我只需要从db里分批进行获取数据 再进行clear 只扩容第一次map
                        //拆分树
                        ((HashMap.TreeNode<K, V>) e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        //loHead old 链表的头节点 loTail old 链表的尾节点
                        HashMap.Node<K, V> loHead = null, loTail = null;
                        //hiHead 链表的头节点 hiTail 链表的尾节点
                        HashMap.Node<K, V> hiHead = null, hiTail = null;
                        HashMap.Node<K, V> next;

                        //===========创建链表========================
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) { //等于0 位置不变
                                if (loTail == null)
                                    loHead = e;
                                else
                                    //尾插法
                                    loTail.next = e;
                                loTail = e;
                            } else {//不等于0 变化位置 将该元素重新计算位置 放置到new table
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    //尾插法
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        //===========添加链表节点========================
                        if (loTail != null) {//尾部最终节点判断 GC
                            loTail.next = null;
                            newTab[j] = loHead; //放置到新链表new table
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;//放置到新链表new table
                        }
                    }
                }
            }
        }
        return newTab;
    }

    // 转化成红黑树 当链表的链节点达到8 且 底层数组的容量达到64才会转化
    final void treeifyBin(HashMap.Node<K, V>[] tab, int hash) {
        int n, index;
        HashMap.Node<K, V> e;
        //(n = tab.length) < MIN_TREEIFY_CAPACITY 底层table数组的容量达到64才会转化
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            resize();
            //e.class 为 Node.class
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            HashMap.TreeNode<K, V> hd = null, tl = null;
            do {
                // TreeNode是hashMap的子节点 TreeNode二叉树的节点是hashMap链表的一个子类
                // TreeNode是hashMap的子类 TreeNode依然是有next指针指向的
                // TreeNode next指针为了当链表的链节点达到6的时候快速解树 还原到链表结构
                // 为什么不强转为子类?
                // replacementTreeNode() -> return new TreeNode<>(p.hash, p.key, p.value, next);
                HashMap.TreeNode<K, V> p = replacementTreeNode(e, null);
                if (tl == null)//只进入一次 进行一次赋值
                    hd = p;//以hd进行真正的treeify
                else {
                    //节点转化关联
                    p.prev = tl; //tl <- p.prev tl.next -> p
                    tl.next = p;
                }
                //前置节点与后置节点的关联
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                hd.treeify(tab);
        }
    }

    //remove
    public V remove(Object key) {
        HashMap.Node<K, V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
                null : e.value;
    }

    final HashMap.Node<K, V> removeNode(int hash, Object key, Object value,
                                        boolean matchValue, boolean movable) {
        HashMap.Node<K, V>[] tab;
        HashMap.Node<K, V> p;
        int n, index;
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (p = tab[index = (n - 1) & hash]) != null) {
            HashMap.Node<K, V> node = null, e;
            K k;
            V v;
            //==========node赋值====================================================
            // 第一个节点找到了node
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                node = p;
            else if ((e = p.next) != null) {
                //是不是树节点
                if (p instanceof HashMap.TreeNode)
                    node = ((HashMap.TreeNode<K, V>) p).getTreeNode(hash, key);
                //链表
                else {
                    // hashMap数组的槽位上 每次传入的key 参数里的key值 先检查首节点
                    // jvm GC的安全点
                    do {
                        if (e.hash == hash &&
                                ((k = e.key) == key ||
                                        (key != null && key.equals(k)))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
            }
            //===========node删除==============================================
            //node != null       matchValue = false
            if (node != null && (!matchValue || (v = node.value) == value ||
                    (value != null && value.equals(v)))) {
                if (node instanceof HashMap.TreeNode)
                    ((HashMap.TreeNode<K, V>) node).removeTreeNode(this, tab, movable);
                else if (node == p)
                    tab[index] = node.next;
                else
                    p.next = node.next;
                ++modCount;
                --size;
                //linkNode 的模板方法
                afterNodeRemoval(node);
                return node;
            }
        }
        return null;
    }
    //1111
    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new HashMap.KeySet();
            keySet = ks;
        }
        return ks;
    }

    /**
     * jdk 1.8
     * @param key
     * @return
     */
    //hashCode()的高16位异或低16位实现的：(h = k.hashCode()) ^ (h >>> 16)
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * jdk1.7 计算存放在table【】里kv对的index下标
     * @param h
     * @param length
     */
    static int indexFor(int h, int length) {
        return h & (length-1);  //第三步 取模运算 等价于对length取模 h%length
    }

    public static void main(String[] args) {

        //一个hashMap如果有1000个元素
        //我们可以通过put方法的返回值,进行一个是否有数值覆盖的判断
        //如果有的业务逻辑从上层业务需求上,就可以肯定key绝对唯一
        //这个时候我们通过hashMap进行数据维护添加的话,代码可以需要通过这个put的特性唠保证我们的业务key确实是唯一的
        //如果发现我们put 的方法的返回值有值的话,说明业务key不唯一,可以直接抛出异常
        //通过代码进行业务的二次校验


        HashMap hashMap = new HashMap<>();
        Set set = hashMap.keySet();
        //不允许  keySet并没有重写add方法 继承的父类的add 抛出异常
        set.add("");
        //直接删除的是map的节点 removeNode()的方法 移除的是kv键值对
        set.remove("");
        // keySet: set.remove("");
        // keySet 就是hashMap里面的key的映射,其实真正的keySet并不是实际意义的存在
        // keySet里面的元素其实都是对hashMap里面的内存地址的一个指向,
        // 当对hashMap里面的keySet进行删除的时候,实际是删除了这个keySet内存指向的映射,自然keySet

        ArrayList arrayList = new ArrayList();
        //lambada 和函数式
        arrayList.stream().forEach(System.out::println);
    }

}
