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

```
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
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
```

1. 检查目标 class 是否曾经加载过，如果加载过则直接返回
2. 如果没加载过，把加载请求传递给 parent 加载器去加载
3. 如果 parent 加载器加载成功，则直接返回
4. 如果 parent 未加载到，则自身调用 findClass() 方法进行寻找，并把寻找结果返回

### 4. 自定义类加载器

```
/**
 * Load class from network
 */
public class NetworkClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = downloadClassData(name); // 从远程下载
        if (classData == null) {
            super.findClass(name); // 未找到，抛异常
        } else {
            return defineClass(name, classData, 0, classData.length); // convert class byte data to Class<?> object
        }
        return null;
    }
    private byte[] downloadClassData(String name) {
        // 从 localhost 下载 .class 文件
        String path = "http://localhost" + File.separatorChar + "java" + File.separatorChar + name.replace('.', File.separatorChar) + ".class"; 
        try {
            URL url = new URL(path);
            InputStream ins = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int bytesNumRead = 0;
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead); // 把下载的二进制数据存入 ByteArrayOutputStream
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getName() {
        System.out.printf("Real NetworkClassLoader\n");
        return "networkClassLoader";
    }
}
```

在需要加载的时机调用下列代码：

```
String className = "classloader.NetworkClass";
NetworkClassLoader networkClassLoader = new NetworkClassLoader();
Class<?> clazz  = networkClassLoader.loadClass(className);
```


---

**作者：wingjay**
_原文链接：[http://wingjay.com/2017/05/08/java_classloader/](http://wingjay.com/2017/05/08/java_classloader/)_