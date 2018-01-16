### [B树](https://mp.weixin.qq.com/s/rDCEFzoKHIjyHfI_bsz5Rw)

1. 磁盘IO的次数由树的高度绝对，树越矮，磁盘IO的次数越少
2. 从算法逻辑的角度看，二叉查找树的查找速度和比较次数都是最小的，但是由于磁盘IO读取次数直接由树的深度决定的现实问题，所以在真正应用于数据库中的索引时，并不能使用BST的数据结构
3. B树在查询中的比较次数其实不比BST少，尤其是当单一节点中的元素数量很多时，但相比磁盘IO操作，内存中的比较还是快很多的，内存比较的耗时很短
4. B树主要应用在文件系统和部分数据库的索引（MongoDB）

### [B+树](https://mp.weixin.qq.com/s/rDCEFzoKHIjyHfI_bsz5Rw)
