## 弱引用

弱引用本质的含义指的是只要一进行GC处理，那么所引用的对象将会被立即回收。弱引用需要使用的是Map接口的子类：

- java.util.WeakHashMap

观察弱引用：

```
public class TestDemo4 {

    public static void main(String[] args) {
        Map<String, String> map = new WeakHashMap<>();
        String key = new String("hello");
        String value = new String("world");
        map.put(key, value);
        System.out.println(map);
        key = null;
        System.out.println(map);
        Runtime.getRuntime().gc();
        System.out.println(map);
    }
}
```

