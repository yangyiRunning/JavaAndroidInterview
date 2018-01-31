## 幽灵引用（虚引用）

永远取得不了的数据就叫做幽灵应用。


观察幽灵引用：

示例代码如下：

```
public class TestDemo6 {

    public static void main(String[] args) throws Exception {
        Object obj = new Object();
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        //幽灵引用上在构造时必须使用引用队列
        PhantomReference<Object> ref = new PhantomReference<>(obj, queue);
        System.gc();
        System.out.println(ref.get());
        System.out.println(queue.poll());
    }
}
```

