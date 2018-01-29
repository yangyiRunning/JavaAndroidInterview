## 强引用

强引用是JVM默认的支持模式，即：在引用的期间内，如果该堆内存被制定的栈内存有联系，那么该对象就无法对被GC所回收，而一旦出现了内存空间不足，就会出现OOM错误信息。

观察范例:

```
public class TestDemo2 {

    public static void main(String[] args) {
        //强引用，默认的支持
        Object obj = new Object();
        //引用传递
        Object ref = obj;
        //截止到此处 ↑ 为止，一个空间对象被两个栈内存所指向

        //断开了一个连接
        obj = null;
        //截止到此处 ↑ 为止，但是new Object()对象仍然被其他对象引用着，引用它的对象为ref

        Runtime.getRuntime().gc();

        //输出ref看看引用new Object()这个对象的对象还在不在
        System.out.println(ref);
    }
}
```

如果此时堆内存有一个栈内存指向，那么该对象将无法被GC回收。强引用是我们一直在使用的模式，并且也是以后开发之中主要的使用模式，正因为强引用具备这样的内存分配异常问题，所以，尽量少实例化对象。