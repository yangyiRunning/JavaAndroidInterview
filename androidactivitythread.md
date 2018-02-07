## Android中为什么主线程不会因为Looper.loop()里的死循环卡死？

#### 关键字： 线程与进程、Android跨进程通信、Android消息机制、Linux pipe/epoll机制

##### 划重点：

- 每个App都运行在一个进程中（除非在AndroidManifest.xml中配置Android:process属性，或通过native代码fork进程）

- 进程和线程的本质区别：**是否能够共享数据**，每个进程拥有自己的一整套变量，而线程则共享数据

- 线程的生命周期中有6个状态（新建、可运行、阻塞、等待、计时等待、销毁），**也就是说一个线程的执行，终有结束
的一刻，但作为Android应用的主线程，绝不希望看到这一刻的到来**，所以主线程ActivityTread中会有一个死循环负责“撑住”让线程不死

- 真正会卡死主线程的操作是在回调方法onCreate/onStart/onResume等操作时间过长，会导致掉帧，甚至发生ANR，looper.loop()本身不会导致应用卡死

- 在进入死循环之前便创建了新binder线程（ApplicationThread），用来接收AMS发来的事件，再通过Handler将Message发送给主线程

- 主线程大多数时候都是处于休眠状态，并不会消耗大量CPU资源

- Activity的生命周期都是依靠主线程的Looper.loop，当收到不同Message时则采用相应措施：在H.handleMessage(msg)方法中，根据接收到不同的msg，执行相应的生命周期。

- 主线程的message是由App进程中的其他线程通过Handler发送来的

##### 综述一下思路（回溯启动一个App的流程）：

1.  每个App都是Android系统fork出来的一个进程，进程中都运行着一个主线程，此主线程的名叫ActivityThread，此类虽没有直接继承自Thread类，但事实上充当了主线程的职能。

2. 在主线程ActivityThread的main方法中，为了保证主线程间的信息接收，新建了一个Binder线程（ApplicationThread）；为了保证主线程不灭，开启了一个死循环将其“撑住”。

3. “撑住”的主线程枕戈待旦等待着Binder线程的指示。

4. 而Binder线程又翘首期盼着远在另一个进程——系统服务进程的召唤


_原文链接：[https://www.zhihu.com/question/34652589/answer/90344494](https://www.zhihu.com/question/34652589/answer/90344494)_