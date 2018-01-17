### [跳跃表](http://mp.weixin.qq.com/s/COBdoHWDhlw4rmG_fGFhSA)

||**跳跃表**|
| :---: | :---: |
|**定义**|基于有序链表的拓展，**多层金字塔式的链表结构**（注意与树做出区分）|
|**思想**|**索引思想**，因为链表的顺序查找太慢，所以利用索引的思想提取出链表中的部分关键节点，就好比一本书当中的目录，书中关键的信息都在目录中|
|**提取索引的方式**|**层层索引**，在原链表的基础上提取出来一半数据作为一级索引，在一级索引的基础上提取出来一半数据作为二级索引，以此类推……提取到2个时为止，因为2个再提取就是1个，而1个没有比较价值，故不需要再提取|
|**比较**|比较时不再依次顺序比较链表中的每个节点，只需要和链表中提取出来的每个关键节点进行比较，确定了新节点在关键节点中的位置，就可以回到原链表定位到原链表中的位置，比较时针对每一层的关键节点（也就是索引）进行逐层比较|
|**插入**|通过对索引进行的层层比较，找到最终的原链表中的插入位置，确定插入位置后将新节点插入至此位置，**当大量的节点插入至此链表时，节点的“升迁”和“降级”采用抛硬币的方法**，分别都有50%的概率进行**“逐层升迁”**，**直到没有概率继续升迁为止**|
|**插入操作复杂度**|1. 新节点和各层索引节点逐一比较，确定原链表的插入位置。O（logN） 2. 把索引插入到原链表。O（1） 3. 利用抛硬币的随机方式，决定新节点是否提升为上一级索引。结果为“正”则提升并继续抛硬币，结果为“负”则停止。O（logN） **跳跃表插入操作的时间复杂度是O（logN），而这种数据结构所占空间是2N，既空间复杂度是 O（N）**|
|**删除操作复杂度**| 1. 自上而下，查找第一次出现节点的索引，并逐层找到每一层对应的节点。O（logN） 2. 删除每一层查找到的节点，如果该层只剩下1个节点，删除整个一层（原链表除外）。O（logN） **跳跃表删除操作的时间复杂度是O（logN）**|
|**平衡性**|相对于B树，跳跃表**维持结构平衡的成本更低**|


**层层索引示意图:**

![层层索引示意图](http://mmbiz.qpic.cn/mmbiz_jpg/NtO5sialJZGo9orh8G0mUYvictrpL6OCwfv9q24hhXCO2AKkh7D502TeFfRb1hR0fE01VsGxrZKYvmmL0Wud4cjw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1)

**升迁和降级的索引示意图:**

1. step 1：

![升迁和降级的索引示意图](http://mmbiz.qpic.cn/mmbiz_jpg/NtO5sialJZGo9orh8G0mUYvictrpL6OCwf9zekAX4esYgYibMFTzqvlIOgIex98YkZRuBYAfgZG0Q1qyqNu8p0icWQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1)

2. step 2：

![升迁和降级的索引示意图](http://mmbiz.qpic.cn/mmbiz_jpg/NtO5sialJZGo9orh8G0mUYvictrpL6OCwf2F7zDyiaejrsVtd0OQWeiaDnmSRGwZy6XU5z6dic6k4QA4LANpBMWeicFA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1)

3. step 3：

![升迁和降级的索引示意图](http://mmbiz.qpic.cn/mmbiz_jpg/NtO5sialJZGo9orh8G0mUYvictrpL6OCwfzh7ltXXV7WeIWpIcibdaYgfricOg3ibh6O40BjvwyFpWefX90VVOHLmibQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1)

**删除节点示意图**

1. step 1:

![删除节点示意图](http://mmbiz.qpic.cn/mmbiz_jpg/NtO5sialJZGo9orh8G0mUYvictrpL6OCwfgg9AxvKxRdcicQ0VA3efzEGK6cHGiayYzbOzULbv2eo9S0sMZPEtOudw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1)

2. step 2:

![删除节点示意图](http://mmbiz.qpic.cn/mmbiz_jpg/NtO5sialJZGo9orh8G0mUYvictrpL6OCwfXfNSWIcepFbJt97FiaEf4qicaSbVqD4iaCVz7smiatYf7DCWVNj7SdyzIw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1)