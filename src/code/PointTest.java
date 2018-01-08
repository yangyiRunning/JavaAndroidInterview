package code;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangyi 2018年1月8日15:01:37
 */
public class PointTest {

    public static void main(String[] args) {
        Point<Integer> point = new Point<>();
        point.setX(1);
        System.out.println(point.getX());

        point.setY(2);
        System.out.println(point.getY());

        List<Object> list = new ArrayList<>();
        list.add(77);
        point.setList(list);

        for (Object o : list) {
            System.out.println(o);
        }
    }
}
