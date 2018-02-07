## Android中为什么主线程不会因为Looper.loop()里的死循环卡死？

#### 关键字： 线程与进程、Android跨进程通信、Android消息机制、Linux pipe/epoll机制

划重点：

- 每个App都运行在一个进程中（除非在AndroidManifest.xml中配置Android:process属性，或通过native代码fork进程）
- 进程和线程的本质区别：**是否能够共享数据**，每个进程拥有自己的一整套变量，而线程则共享数据
- 线程的生命周期中有6个状态（新建、可运行、阻塞、等待、计时等待、销毁），**也就是说一个线程的执行，终有结束的一刻，但作为Android应用的主线程，绝不希望看到这一刻的到来**，所以主线程ActivityTread中会有一个死循环负责“撑住”让线程不死
- 真正会卡死主线程的操作是在回调方法onCreate/onStart/onResume等操作时间过长，会导致掉帧，甚至发生ANR，looper.loop本身不会导致应用卡死


_原文链接：[https://www.zhihu.com/question/34652589/answer/90344494](https://www.zhihu.com/question/34652589/answer/90344494)_