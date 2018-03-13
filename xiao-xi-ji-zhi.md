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
 
### 1. Message：包含任意类型的对象和描述信息，可以被发送给 Handler

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

### 2. 创建一个Message对象的正确方式：从回收池中获取消息

1. Message.obtain()
2. Handler.obtainMessage()

### 3. Message.obtain()：从回收池中获取消息本质上最终调用了如下方法

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
**给消息池加锁保证线程安全，if(消息列表为空) new一个消息链表; 否则从回收池中返回链表头对应的数据; 相应链表缩短1位**

### 4. 消息回收再利用：Message.recycleUnchecked()，在MessageQueue和Looper中都会被调用

```
void recycleUnchecked() {
        // Mark the message as in use while it remains in the recycled object pool.
        // Clear out all other details.
        flags = FLAG_IN_USE;
        what = 0;
        arg1 = 0;
        arg2 = 0;
        obj = null;
        replyTo = null;
        sendingUid = -1;
        when = 0;
        target = null;
        callback = null;
        data = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }
```

**设置个标记位防止被重复入队，将数据清空，给消息池加锁，if(没达到池容量) 拼入链表尾且长度+1**

### 5. 回收消息方法调用时机：MessageQueue.removeMessages和Looper.loop()

```
void removeMessages(Handler h, int what, Object object) {
        if (h == null) {
            return;
        }

        synchronized (this) {
            Message p = mMessages;

            // Remove all messages at front.
            while (p != null && p.target == h && p.what == what
                   && (object == null || p.obj == object)) {
                Message n = p.next;
                mMessages = n;
                p.recycleUnchecked();
                p = n;
            }

            // Remove all messages after front.
            while (p != null) {
                Message n = p.next;
                if (n != null) {
                    if (n.target == h && n.what == what
                        && (object == null || n.obj == object)) {
                        Message nn = n.next;
                        n.recycleUnchecked();
                        p.next = nn;
                        continue;
                    }
                }
                p = n;
            }
        }
    }
```

```
public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        final MessageQueue queue = me.mQueue;

        // Make sure the identity of this thread is that of the local process,
        // and keep track of what that identity token actually is.
        Binder.clearCallingIdentity();
        final long ident = Binder.clearCallingIdentity();

        for (;;) {
            Message msg = queue.next(); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }

            // This must be in a local variable, in case a UI event sets the logger
            final Printer logging = me.mLogging;
            if (logging != null) {
                logging.println(">>>>> Dispatching to " + msg.target + " " +
                        msg.callback + ": " + msg.what);
            }

            final long traceTag = me.mTraceTag;
            if (traceTag != 0 && Trace.isTagEnabled(traceTag)) {
                Trace.traceBegin(traceTag, msg.target.getTraceName(msg));
            }
            try {
                msg.target.dispatchMessage(msg);
            } finally {
                if (traceTag != 0) {
                    Trace.traceEnd(traceTag);
                }
            }

            if (logging != null) {
                logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
            }

            // Make sure that during the course of dispatching the
            // identity of the thread wasn't corrupted.
            final long newIdent = Binder.clearCallingIdentity();
            if (ident != newIdent) {
                Log.wtf(TAG, "Thread identity changed from 0x"
                        + Long.toHexString(ident) + " to 0x"
                        + Long.toHexString(newIdent) + " while dispatching to "
                        + msg.target.getClass().getName() + " "
                        + msg.callback + " what=" + msg.what);
            }

            msg.recycleUnchecked();
        }
    }
```

**在移除消息的过程中，并不是真正的“移除”，而是将消息存放在回收消息链表中，同理，在消息获取时，也不是真正的新建，而是从回收消息链表中拿取先前存进去的消息**

**在MessageQueue出队时，和Looper处理时，分别会将消息存放进回收消息链表；在Message.obtain()时从回收链表中获取一个的旧的消息**

### 6. MessageQueue:  管理着一个 Message 的列表，Handlers 为它添加消息，Looper 从中取消息


```
private static final String TAG = "MessageQueue";
    private static final boolean DEBUG = false;

    // True if the message queue can be quit.
    private final boolean mQuitAllowed;

    @SuppressWarnings("unused")
    private long mPtr; // used by native code

    Message mMessages;
    private final ArrayList<IdleHandler> mIdleHandlers = new ArrayList<IdleHandler>();
    private SparseArray<FileDescriptorRecord> mFileDescriptorRecords;
    private IdleHandler[] mPendingIdleHandlers;
    private boolean mQuitting;

    // Indicates whether next() is blocked waiting in pollOnce() with a non-zero timeout.
    private boolean mBlocked;

    // The next barrier token.
    // Barriers are indicated by messages with a null target whose arg1 field carries the token.
    private int mNextBarrierToken;
```

MessageQueue持有一个message链表的头节点



---

**原作者：shixinzhang**

_原文链接_：

[http://blog.csdn.net/u011240877/article/details/72892321](http://blog.csdn.net/u011240877/article/details/72892321)