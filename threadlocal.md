## ThreadLocal

- 在多线程环境下，防止自己的变量被其他线程篡改
- 每个线程中都有一个**ThreadLocalMap**数据结构，当执行set方法时，其值是保存在当前线程的threadLocals变量中，当执行get方法中，是从当前线程的threadLocals变量获取
- **ThreadLocalMap**：本质上是一个数组，元素为一个键值对，ThreadLocal为key，Object为value，采用"**线性检索**的方式寻找数组中的空位"
- Entry中的key，也就是ThreadLocal对象本身保存在弱引用中，而value，也就是Object对象则保存在强引用中，有可能导致，key会被回收而value回收不了，造成内存泄漏
- 避免ThreadLocal内存泄漏的方法：使用完ThreadLocal后显式的调用remove方法

```
ThreadLocal<String> localName = new ThreadLocal();
try {
localName.set("占小狼");
} finally {
localName.remove();
}
```

**原作者：占小狼**

_原文链接：[http://mp.weixin.qq.com/s/1ccG1R3ccP0_5A7A5zCdzQ](http://mp.weixin.qq.com/s/1ccG1R3ccP0_5A7A5zCdzQ)_