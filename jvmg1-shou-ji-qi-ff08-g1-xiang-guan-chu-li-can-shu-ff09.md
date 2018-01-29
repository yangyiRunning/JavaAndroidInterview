## G1相关参数处理

清楚了G1的基本运行原理之后，那么下面就需要进行一些G1的配置。**在现在Javan内存之中还没有默认使用G1的情况，所以如果想用G1进行垃圾收集需要进行手工配置。**

![](/assets/3721517237988_.pic_hd.jpg)

使用G1回收器：

1. 准备好范例代码TestDemo.java类：

``` 
public class TestDemo {

    public static void main(String[] args) {
        String str = "www.google.com";
        while (true) {
            str += str + str;
            str.intern();
        }
    }
}

```

2. 然后打开终端键入如下命令行：

``` 
java -Xmx10m -Xms10m -XX:+UseG1GC -XX:+PrintGCDetails TestDemo

```

3. 会输入如下大量的信息：

![](/assets/3731517238485_.pic_hd.jpg)

![](/assets/3741517238519_.pic_hd.jpg)

![](/assets/3751517238552_.pic_hd.jpg)

G1的处理和传统的垃圾收集策略是不同的，关键的因素是G1将所有的内存进行了子区域的划分。