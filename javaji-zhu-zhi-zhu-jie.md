## 注解

- Annotation的3个生命周期:

 1. RetentionPolicy.SOURCE  // 只在代码编辑期生效
 2. RetentionPolicy.CLASS  // 在编译期生效，默认值
 3. RetentionPolicy.RUNTIME // 在代码运行时生效
 
- Annotation用来指定的对象（方法、类、变量、参数）:

```
@Annotation 
public void getName() {}
@Annotation 
String name;
public void setName(@Annotation String name) {}

``` 



---

**原作者：wingjay**

_原文链接: [http://wingjay.com/2017/05/03/Java-%E6%8A%80%E6%9C%AF%E4%B9%8B%E6%B3%A8%E8%A7%A3-Annotation/](http://wingjay.com/2017/05/03/Java-%E6%8A%80%E6%9C%AF%E4%B9%8B%E6%B3%A8%E8%A7%A3-Annotation/)_

