## Android消息机制

- 消息机制可以说是Android中最核心的技术点之一，可以说没有它，整个Android应用层将无从谈起。

- 涉及到的类:
 1. Message
 2. MessageQueue
 3. Handler
 4. Looper
 
- 总体上对消息机制最最基础的感性认识;
 1. Handler 给 MessageQueue 添加消息
 2. 然后 Looper 无限循环读取消息
 3. 再调用 Handler 处理消息

---

**原作者：shixinzhang**

_原文链接_：

[http://blog.csdn.net/u011240877/article/details/72892321](http://blog.csdn.net/u011240877/article/details/72892321)