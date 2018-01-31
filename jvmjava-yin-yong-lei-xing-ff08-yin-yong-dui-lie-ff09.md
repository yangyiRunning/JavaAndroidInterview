## 引用队列

所谓的引用队列指的就是保存那些准备被回收的对象。很多的时候所有的对象的回收扫描，都是从根对象开始的。那么对于整个GC而言，如果要想确定哪些对象可以被回收，我们就必须确定好引用的强度，这个也就是所谓的引用路径的设置。

![](/assets/3781517409661_.pic_hd.jpg)

如果现在要找到对象5，那么很明显1找到5属于“强+软”，那么2找到5属于“强+弱”。软引用要比弱引用保存的强。所以这个时候实际上对于对象的引用而言，如果要进行引用的关联判断，那么久必须找到强关联，那么为了避免非强关联或者强引用对象所带来的内存问题，所以提供有一个叫做引用队列的概念，如果在创建软引用或者弱引用类型的时候使用了引用队列的方式，则这个对象被税收之后会自动保存在引用队列之中。

使用引用队列：

示例代码如下：

```
public class TestDemo5 {

    public static void main(String[] args) {
        Object obj = new Object();
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        WeakReference<Object> ref = new WeakReference<Object>(obj, queue);
        System.out.println(queue.poll());
    }
}
```

