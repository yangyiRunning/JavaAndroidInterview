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