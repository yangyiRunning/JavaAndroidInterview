## Android中为什么主线程不会因为Looper.loop()里的死循环卡死？

#### 关键字： 线程与进程、Android跨进程通信、Android消息机制、Linux pipe/epoll机制

划重点：

- 每个App都运行在一个进程中（除非在AndroidManifest.xml中配置Android:process属性，或通过native代码fork进程）
- 


_原文链接：[https://www.zhihu.com/question/34652589/answer/90344494](https://www.zhihu.com/question/34652589/answer/90344494)_