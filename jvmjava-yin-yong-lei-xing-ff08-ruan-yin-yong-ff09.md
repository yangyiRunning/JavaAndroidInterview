## 软引用

在许多的开源组件之中，往往会使用软引用作为缓存组件出现，其最大的特点在于:

**内存不足时回收，内存空间充足时不回收。**

如果想要实现软引用，则必须有一个单独的类(java.lang.ref.SoftReference，其是java.lang.ref.Reference的一个子类)来实现控制：

这个类的方法如下：

- 构造：

```
/**
     * Creates a new soft reference that refers to the given object.  The new
     * reference is not registered with any queue.
     *
     * @param referent object the new soft reference will refer to
     */
    public SoftReference(T referent) {
        super(referent);
        this.timestamp = clock;
    } 
```

- 取出数据：

```
/**
     * Returns this reference object's referent.  If this reference object has
     * been cleared, either by the program or by the garbage collector, then
     * this method returns <code>null</code>.
     *
     * @return   The object to which this reference refers, or
     *           <code>null</code> if this reference object has been cleared
     */
    public T get() {
        T o = super.get();
        if (o != null && this.timestamp != clock)
            this.timestamp = clock;
        return o;
    }
```

观察软引用：

示例代码：

```
public class TestDemo3 {

    public static void main(String[] args) {
        Object obj = new Object();
        //软引用
        SoftReference<Object> ref = new SoftReference<>(obj);
        //断开链接
        obj = null;
        Runtime.getRuntime().gc();
        System.out.println(ref.get());
    }
}
```

输出下列结果：

```
java.lang.Object@60e53b93
```