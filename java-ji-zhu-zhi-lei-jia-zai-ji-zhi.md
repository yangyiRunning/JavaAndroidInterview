## 类加载机制

### 4种Java自带的类加载器：

|**类加载器(ClassLoader)的种类**|**BootstrapClassLoader**|**ExtClassLoader**|**AppClassLoader(System ClassLoader)**|**自定义ClassLoader**|
|:---:|:---:|:---:|:---:|:---:|
|**负责加载的范围**|负责加载$JAVA_HOME/jre/lib/ 目录下的Java核心类，不继承ClassLoader，由JVM内部实现|负责加载$JAVA_HOME/jre/lib/ext目录下的核心拓展类|负责加载开发者在项目中编写的类|负责远程加载如（本地文件／网络下载），自己编写ClassLoader的子类，覆写findClass()方法|

_原文链接：[http://wingjay.com/2017/05/08/java_classloader/](http://wingjay.com/2017/05/08/java_classloader/)_