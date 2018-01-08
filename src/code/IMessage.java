package code;

public interface IMessage<T extends Number> {

    void printMessage(T t);

    /**
     * @param v
     * @param <K>
     * @return
     */
    <K, V extends Number> K getMessage(V v);
}
