package code;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangyi 2018年1月8日13:57:21
 */
public class Point<T extends Number> {
    private T x;
    private T y;
    private List<? super String> list;
    private List<? extends Number> numbers = new ArrayList<>();

    public List<? super String> getList() {
        return list;
    }

    public void setList(List<? super String> list) {
        this.list = list;
    }

    public T getX() {
        return x;
    }

    public void setX(T x) {
        this.x = x;
    }

    public T getY() {
        return y;
    }

    public void setY(T y) {
        this.y = y;
    }
}
