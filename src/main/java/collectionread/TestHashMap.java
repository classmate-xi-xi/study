package collectionread;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

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

    public static void main(String[] args) {
        //一个hashMap如果有1000个元素
        //我们可以通过put方法的返回值,进行一个是否有数值覆盖的判断
        //如果有的业务逻辑从上层业务需求上,就可以肯定key绝对唯一
        //这个时候我们通过hashMap进行数据维护添加的话,代码可以需要通过这个put的特性唠保证我们的业务key确实是唯一的
        //如果发现我们put 的方法的返回值有值的话,说明业务key不唯一,可以直接抛出异常
        //通过代码进行业务的二次校验
    }

}
