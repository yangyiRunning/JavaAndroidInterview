- [HashMap介绍](https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653191907&idx=1&sn=876860c5a9a6710ead5dd8de37403ffc&chksm=8c990c39bbee852f71c9dfc587fd70d10b0eab1cca17123c0a68bf1e16d46d71717712b91509&scene=21#wechat_redirect)
  1. Java中的数据结构散列表，结构为数组里面套一个链表（每一个数组元素的内容都为一个链表的头指针）
  2. 插入时采用头插法，因为HashMap的设计者认为后插入的数据被查询的可能性更大
  3. HashMap中hash函数的设计，不同于直接取模运算，而是采用位运算的方式，效果同样为取模，但是性能更好
  4. HashMap的默认长度为16，设置HashMap的长度时一般设置为2的幂次，因为如果设置为10的话有些index会高频亮相，但是有些index却取不到，违反了hash函数要均匀分布的设计初衷
  5. HashMap为保存键值对的数据结构，每一个键值对也叫做Entry，这些个键值对（Entry）分散存储在一个数组当中，这个数组就是HashMap的主干
  6. HashMap数组的每一个元素不止是一个Entry对象，也是一个链表的头节点
  7. 设想中的index计算方法（取模运算）：index =  HashCode（Key） % Length
  8. 实际中的index计算方法（位运算）：index =  HashCode（Key） &  （Length - 1） （**Hash算法最终得到的index结果，完全取决于Key的Hashcode值的最后几位。**）
  9. HashMap是一个Entry对象的数组。数组中的每一个Entry元素，又是一个链表的头节点
  
- [高并发下的HashMap](https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653192000&idx=1&sn=118cee6d1c67e7b8e4f762af3e61643e&chksm=8c990d9abbee848c739aeaf25893ae4382eca90642f65fc9b8eb76d58d6e7adebe65da03f80d&scene=21#wechat_redirect)
  1. HashMap在插入元素过多的时候需要进行Resize，Resize的条件是**HashMap.Size >= Capacity * LoadFactor**。
  2. HashMap的Resize包含**扩容**和**ReHash**两个步骤，ReHash在并发的情况下可能会形成链表环。
  3. 扩容与否的衡量标准是**主干（即Entry[]）**的长度是否达到给定长度的0.75

- [ConcurrentHashMap](https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653192083&idx=1&sn=5c4becd5724dd72ad489b9ed466329f5&chksm=8c990d49bbee845f69345e4121888ec967df27988bc66afd984a25331d2f6464a61dc0335a54&scene=21#wechat_redirect)
  1. HashMap不是线程安全的。在高并发环境下做插入操作，有可能出现下面的环形链表
  2. 二级哈希表
  3. 主干为Segment[]，Segment本身就相当于一个HashMap对象，同HashMap一样，Segment包含一个HashEntry数组，数组中的每一个HashEntry既是一个键值对，也是一个链表的头节点
  4. **锁分段技术**：每一个Segment就相当于一个高度自治的特区，能够高度自主的进行读写操作