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

在Looper的构造方法中MessageQueue初始化：

```
private Looper(boolean quitAllowed) {
        mQueue = new MessageQueue(quitAllowed);
        mThread = Thread.currentThread();
    }
```

```
MessageQueue(boolean quitAllowed) {
        mQuitAllowed = quitAllowed;
        mPtr = nativeInit();
    }
```

**是否允许中途退出及调用native层的方法初始化**

通过Looper.myLooper()从ThreadLocal中拿到Looper

```
public static @Nullable Looper myLooper() {
        return sThreadLocal.get();
    }
```

### 7. MessageQueue的消息入队过程：

```
boolean enqueueMessage(Message msg, long when) {
        if (msg.target == null) { //这里要求消息必须跟 Handler 关联
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (msg.isInUse()) {
            throw new IllegalStateException(msg + " This message is already in use.");
        }

        synchronized (this) {
            if (mQuitting) {  //如果消息队列已经退出，还入队就报错
                IllegalStateException e = new IllegalStateException(
                        msg.target + " sending message to a Handler on a dead thread");
                Log.w(TAG, e.getMessage(), e);
                msg.recycle();
                return false;
            }

            msg.markInUse();  //消息入队后就标记为 在被使用
            msg.when = when;
            Message p = mMessages;
            boolean needWake;
             //添加消息到链表中
            if (p == null || when == 0 || when < p.when) {
            //之前是空链表的时候读取消息会阻塞，新添加消息后唤醒
                // New head, wake up the event queue if blocked.
                msg.next = p;
                mMessages = msg;
                needWake = mBlocked;
            } else {
            //插入消息到队列时，只有在队列头部有个屏障并且当前消息是异步的时才需要唤醒队列
                // Inserted within the middle of the queue.  Usually we don't have to wake
                // up the event queue unless there is a barrier at the head of the queue
                // and the message is the earliest asynchronous message in the queue.
                needWake = mBlocked && p.target == null && msg.isAsynchronous();
                Message prev;
                for (;;) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p; // invariant: p == prev.next
                prev.next = msg;
            }

            // We can assume mPtr != 0 because mQuitting is false.
            if (needWake) {
                nativeWake(mPtr);
            }
        }
        return true;
    }
```
由上面的源码得知，**每次调用 Handler.sendMessage() 时，都必须是 obtain() 或者 new 一个新的 Message 对象，否则就会报This message is already in use.的错误**，也就是说，每一个Message**不能被二次使用**，也不能在消息队列退出后再添加消息。

### 8. MessageQueue的消息出队过程

```
Message next() {
        // Return here if the message loop has already quit and been disposed.
        // This can happen if the application tries to restart a looper after quit
        // which is not supported.
        //如果消息的 looper 退出，就退出这个方法
        final long ptr = mPtr;
        if (ptr == 0) {
            return null;
        }

        int pendingIdleHandlerCount = -1; // -1 only during first iteration
        int nextPollTimeoutMillis = 0;
         //也是一个循环，有合适的消息就返回，没有就阻塞
        for (;;) {
         //如果有需要过段时间再处理的消息，先调用 Binder 的这个方法
            if (nextPollTimeoutMillis != 0) {
                Binder.flushPendingCommands();
            }

            nativePollOnce(ptr, nextPollTimeoutMillis);

            synchronized (this) {
                // Try to retrieve the next message.  Return if found.
                //获取下一个消息
                final long now = SystemClock.uptimeMillis();
                Message prevMsg = null;
                //当前链表的头结点
                Message msg = mMessages;
                //如果消息没有 target，那它就是一个屏障，需要一直往后遍历找到第一个异步的消息
                if (msg != null && msg.target == null) {
                    // Stalled by a barrier.  Find the next asynchronous message in the queue.
                    do {
                        prevMsg = msg;
                        msg = msg.next;
                    } while (msg != null && !msg.isAsynchronous());
                }
                if (msg != null) {
                    if (now < msg.when) {
                     //如果这个消息还没到处理时间，就设置个时间过段时间再处理
                        // Next message is not ready.  Set a timeout to wake up when it is ready.
                        nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                    } else {
                    // 消息是正常的、可以立即处理的
                        // Got a message.
                        //设置不再阻塞
                        mBlocked = false;
                        //取出当前消息，链表头结点后移一位
                        if (prevMsg != null) {
                            prevMsg.next = msg.next;
                        } else {
                            mMessages = msg.next;
                        }
                        msg.next = null;
                        if (DEBUG) Log.v(TAG, "Returning message: " + msg);
                        //标记这个消息在被使用
                        msg.markInUse();
                        return msg;
                    }
                } else {
                    // No more messages.
                    // 消息链表里没有消息了
                    nextPollTimeoutMillis = -1;
                }

                // Process the quit message now that all pending messages have been handled.
                //如果收到退出的消息，并且所有等待处理的消息都处理完时，调用 Native 方法销毁队列
                if (mQuitting) {
                    dispose();
                    return null;
                }

                // If first time idle, then get the number of idlers to run.
                // Idle handles only run if the queue is empty or if the first message
                // in the queue (possibly a barrier) is due to be handled in the future.
                 //有消息等待过段时间执行时，pendingIdleHandlerCount 增加
                if (pendingIdleHandlerCount < 0
                        && (mMessages == null || now < mMessages.when)) {
                    pendingIdleHandlerCount = mIdleHandlers.size();
                }
                if (pendingIdleHandlerCount <= 0) {
                    // No idle handlers to run.  Loop and wait some more.
                    mBlocked = true;
                    continue;
                }

                if (mPendingIdleHandlers == null) {
                    mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount, 4)];
                }
                mPendingIdleHandlers = mIdleHandlers.toArray(mPendingIdleHandlers);
            }

            // Run the idle handlers.
            // We only ever reach this code block during the first iteration.
            for (int i = 0; i < pendingIdleHandlerCount; i++) {
                final IdleHandler idler = mPendingIdleHandlers[i];
                mPendingIdleHandlers[i] = null; // release the reference to the handler

                boolean keep = false;
                try {
                    keep = idler.queueIdle();
                } catch (Throwable t) {
                    Log.wtf(TAG, "IdleHandler threw exception", t);
                }

                if (!keep) {
                    synchronized (this) {
                        mIdleHandlers.remove(idler);
                    }
                }
            }

            // Reset the idle handler count to 0 so we do not run them again.
            pendingIdleHandlerCount = 0;

            // While calling an idle handler, a new message could have been delivered
            // so go back and look again for a pending message without waiting.
            nextPollTimeoutMillis = 0;
        }
    }
```
**MessageQueue中开启了一个死循环，if(链表头不为空且消息对应的target不为空且到达了指定的处理时间)，就返回此消息，否则继续遍历链表的下一个节点**

**如果有阻塞（没有消息了或者只有 Delay 的消息），会把 mBlocked这个变量标记为 true，在下一个 Message 进队时会判断这个message 的位置，如果在队首就会调用 nativeWake() 方法唤醒线程**

### 9. IdleHandler : 线程阻塞时回调的接口

public static interface IdleHandler {

    //当消息队列没有消息时会回调这个方法，阻塞等待有消息进入
    //返回 true 的话表示唤醒阻塞的线程，false 表示移除
    //如果消息队列中有消息等待在将来执行，也会调用这个方法
    boolean queueIdle();
}

MessageQueue 中提供了监听阻塞回调的注册和移除接口：

public void addIdleHandler(@NonNull IdleHandler handler) {
    if (handler == null) {
        throw new NullPointerException("Can't add a null IdleHandler");
    }
    synchronized (this) {
        mIdleHandlers.add(handler);
    }
}

public void removeIdleHandler(@NonNull IdleHandler handler) {
    synchronized (this) {
        mIdleHandlers.remove(handler);
    }
}

当消息队列阻塞时，会回调这些监听阻塞的观察者，告诉他们：我有空了！来找我玩啊！

### 10. 

---

**原作者：shixinzhang**

_原文链接_：

[http://blog.csdn.net/u011240877/article/details/72892321](http://blog.csdn.net/u011240877/article/details/72892321)