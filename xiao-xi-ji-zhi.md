## Android消息机制

- 消息机制可以说是Android中最核心的技术点之一，可以说没有它，整个Android应用层将无从谈起。

- 涉及到的类：
 1. Message
 2. MessageQueue
 3. Handler
 4. Looper
 
- 总体上对消息机制最最基础的感性认识：
 1. Handler 给 MessageQueue 添加消息
 2. 然后 Looper 无限循环读取消息
 3. 再调用 Handler 处理消息 
 
### Message
 
包含任意类型的对象和描述信息，可以被发送给 Handler。

类中关键要素：

- 标识干什么的 what

```
 //用来标识一个消息，接收消息方可以根据它知道这个消息是做什么的
 public int what;
```

- 两个简易的整型数据存储对象 arg1 arg2

```
//如果你的消息要传递的数据是整型的，可以直接使用 arg1 和 arg2，而不需要使用构造一个 Bundle
public int arg1;
public int arg2;
```

- 存储复杂点的对象 Bundle

```
//很关键的数据部分
/*package*/Bundle data;

```

- 跨进程通信绑定数据的 object

```
//一个任意类型的对象，在使用 Messenger 跨进程传递消息时，通常使用它传递给接收者
//在其他场景下我们一般使用 setData() 方法
public Object obj;
```

- 与之关联的 Handler

```
//发送和处理消息关联的 Handler
/*package*/Handler target;
```

- 还有一些标志位和消息池

### Message.obtain()

```
public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }
```

---

**原作者：shixinzhang**

_原文链接_：

[http://blog.csdn.net/u011240877/article/details/72892321](http://blog.csdn.net/u011240877/article/details/72892321)