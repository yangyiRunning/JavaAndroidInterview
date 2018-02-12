## 类加载机制

### 1. 4种Java自带的类加载器：

|**类加载器(ClassLoader)的种类**|**BootstrapClassLoader**|**ExtClassLoader**|**AppClassLoader(System ClassLoader)**|**自定义ClassLoader**|
|:---:|:---:|:---:|:---:|:---:|
|**负责加载的范围**|负责加载$JAVA_HOME/jre/lib/ 目录下的Java核心类，不继承ClassLoader，由JVM内部实现|负责加载$JAVA_HOME/jre/lib/ext目录下的核心拓展类|负责加载开发者在项目中编写的类|负责远程加载如（本地文件／网络下载），自己编写ClassLoader的子类，覆写findClass()方法|

### 2. 双亲委托：

用类加载器加载一个新编写的类时，需要先找到这个类，确定类究竟在哪个路径，从而确定具体要使用哪种类加载器。当查找一个类时，优先遍历最高级别的 Java 核心类，然后再去遍历 Java 核心扩展类，最后再遍历用户自定义类，而且这个遍历过程是一旦找到就立即停止遍历。

![双亲委托](/assets/order.png)

**每次需要加载一个类，先获取一个系统加载器 AppClassLoader 的实例（ClassLoader.getSystemClassLoader()），然后向上级层层请求，由最上级优先去加载，如果上级觉得这些类不属于核心类，就可以下放到各子级负责人去自行加载。**

### 3. 双亲委托机制源码逻辑（loadClass方法）： （JDK1.8为例）

`protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
`

---

**作者：wingjay**
_原文链接：[http://wingjay.com/2017/05/08/java_classloader/](http://wingjay.com/2017/05/08/java_classloader/)_