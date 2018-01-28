## 垃圾回收策略配置（设置与观察）

清楚了整个的回收可以使用的回收策略之后如果想对GC进行合理的回收的策略控制，则可以使用如下的几个参数进行合理的配置：

![](/assets/3441517108757_.pic_hd.jpg)

![](/assets/3491517142381_.pic_hd.jpg)

并行操作的时候我们可以设置使用的CPU数量。

举例： 设置完CPU数量之后可以通过` Runtime.getRuntime().availableProcessors() ` 查看。

下面首先来观察默认的GC策略：

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

现在可以发现年轻代并行回收策略，老年代使用的是并行GC策略。