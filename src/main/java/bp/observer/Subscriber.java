package bp.observer;

public interface Subscriber {

    <T> void update(T t);
}
