## 垃圾回收策略配置（设置与观察）

清楚了整个的回收可以使用的回收策略之后如果想对GC进行合理的回收的策略控制，则可以使用如下的几个参数进行合理的配置：

![](/assets/3441517108757_.pic_hd.jpg)

![](/assets/3491517142381_.pic_hd.jpg)

并行操作的时候我们可以设置使用的CPU数量。

举例： 设置完CPU数量之后可以通过` Runtime.getRuntime().availableProcessors() ` 查看。

#### **下面首先来观察默认的GC策略：**

编写测试程序：(创建TestDemo类)

```

public class TestDemo {
 public static void main(String[]      args) {
        String str = "www.google.com";
        while (true) {
            str += str + str;
            str.intern();
        }
    }
 } 
 
 
```

具体的操作步骤：

1. 创建TestDemo类（代码如上图所示）

2. 用javac TestDemo.java 命令编译该类

3. 用``` java -Xmx10m -Xms10m -XX:+PrintGCDetails TestDemo执行该类 ```

会在控制台输出下列结果：

![](/assets/3501517143334_.pic_hd.jpg)

其中该行的信息：

![](/assets/3511517143587_.pic_hd.jpg)

现在可以发现年轻代使用的是并行回收策略，老年代使用的是并行GC策略。

#### **使用串行回收策略设置：**

执行下面命令行语句：

``` java -Xmx10m -Xms10m -XX:+UseSerialGC -XX:+PrintGCDetails TestDemo ```

会输出下列信息：

![](/assets/3521517144108_.pic_hd.jpg)

注意信息中的这条记录：

![](/assets/3531517144264_.pic_hd.jpg)

**DefNew：** 年轻代默认的GC

**Tenured:** 老年代GC

年轻代和老年代都是在单线程下执行。

#### **使用并行GC策略：**

执行下列命令行语句：

``` java -Xmx10m -Xms10m -XX:+UseParallelGC -XX:+PrintGCDetails TestDemo ```

会输出以下信息：

![](/assets/3541517144539_.pic_hd.jpg)

注意输出信息中的这条记录：

![](/assets/3551517144628_.pic_hd.jpg)

**PSYoungGen:**  并行GC

**ParOldGen:** 并行GC

从这个输出的信息可以看出，这个的结果和默认的结果是一致的，也就是说在多CPU的环境下默认启动的是并行GC。系统可以自动的帮我们选择GC回收策略。

现在可以发现年轻代并行回收策略，老年代使用的是并行GC策略。