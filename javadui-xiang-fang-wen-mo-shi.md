- Java的引用类型是最为重要的数据处理模型，而整个的引用数据类型的处理之中，会牵扯到：堆内存、栈内存、方法区。下面以一个简单的代码为主：
Object obj = new Object()，实例化了一个Object对象。
  1. Object obj : 描述的是保存z哎栈内存之中，而保存有堆内存的引用，这个数据会保存在本地变量表中；
  2. new Object() : 一个真正的对象，对象保存在堆内存当中，直观的思路下，整个引用的操作：
  3. 新定义的对象的名称保存在本地变量表，而后在这块区域里面需要确定好与之对应的栈内存
  4. 通过变量表中的栈内存地址可以找到堆内存
  5. 利用堆内存的对象进行本地方法的调用
 
- 对于所有引用数据类型的访问存在两种模式：

![](/assets/3021516984164_.pic_hd.jpg)

 但是在Java中它直接利用的是对象保存的模式，而堆内存中不需要再保存句柄，而是直接保存的对象，这样就省略了一步句柄到对象之间的查找，而这个是对象可以直接进行Java方法区的调用
 
![](/assets/3031516984608_.pic_hd.jpg)

- 在实际上有三种JVM
  1. SUN公司最早改良的HotSpot
  2. BEA公司的JRockit
  3. IBM JVM'S
  
- 取得当前JVM的版本号
` java -version
java version "1.8.0_121"
Java(TM) SE Runtime Environment (build 1.8.0_121-b13)
Java **HotSpot**(TM) 64-Bit Server VM (build 25.121-b13, **mixed mode**) `

所谓的混合模式就是指适合于编译和执行。

- 使用纯解释模式启动

` java -Xint -version
java version "1.8.0_121"
Java(TM) SE Runtime Environment (build 1.8.0_121-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.121-b13, **interpreted mode**) `

- 使用纯编译模式启动

` java -Xcomp -version
java version "1.8.0_121"
Java(TM) SE Runtime Environment (build 1.8.0_121-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.121-b13, **compiled mode**) `
实际上现在的JDK设置都开始为服务器而准备的

JVM的启动有两种模式：

1.  ”-server“ ： 服务器模式，占用的内存大、启动速度慢
2. ”-client“ ： 本地单机运行程序模式，启动的速度快

在jre中就可以看是哪种模式 jvm.cfg这个文件中就可以看当前的启动配置，现在Java的默认启动模式就是-server，jre的server文件夹下就保存的server方式启动的启动配置
