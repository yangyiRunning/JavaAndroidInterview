- [HashMap介绍](https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653191907&idx=1&sn=876860c5a9a6710ead5dd8de37403ffc&chksm=8c990c39bbee852f71c9dfc587fd70d10b0eab1cca17123c0a68bf1e16d46d71717712b91509&scene=21#wechat_redirect)
  1. Java中的数据结构散列表，结构为数组里面套一个链表（每一个数组元素的内容都为一个链表的头指针）
  2. 插入时采用头插法，因为HashMap的设计者认为后插入的数据被查询的可能性更大
  3. HashMap中hash函数的设计，不同于直接取模运算，而是采用位运算的方式，效果同样为取模，但是性能更好
  4. HashMap的默认长度为16，设置HashMap的长度时一般设置为2的幂次，因为如果设置为10的话有些index会高频亮相，但是有些index却取不到，违反了hash函数要均匀分布的设计初衷
  5. HashMap为保存键值对的数据结构，每一个键值对也叫做Entry
  
- [高并发下的HashMap](https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653192000&idx=1&sn=118cee6d1c67e7b8e4f762af3e61643e&chksm=8c990d9abbee848c739aeaf25893ae4382eca90642f65fc9b8eb76d58d6e7adebe65da03f80d&scene=21#wechat_redirect)
  1. 