- [一致性哈希](https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653191083&idx=1&sn=c68c8bb7e18c4d46b85666be10e9ef50&chksm=8c990971bbee80675b6cd0ac3c2c17546cd434c3636616e559ca5cf10d1815c3aed24bfd3c83&scene=21#wechat_redirect)
 1. 逻辑存储结构为一个环
 2. 物理存储结构用一个数组即可
 3. 分别对key和node进行hash，使其尽可能均匀的分布在环状的结构中
 4. 在实际的应用场景中，key往往对应分库中的唯一主键，node对应分库本身，可以根据分库的IP地址来生成node对应的hash
 5. 如果不幸节点hash后的分布仍然不均匀，可以采用虚拟节点，利用(IP+后缀)计算**node1-1**,**node1-2**