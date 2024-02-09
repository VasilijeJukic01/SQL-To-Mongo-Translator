package bp.observer;

public interface Publisher {

    void addSubscriber(Subscriber s);

    void removeSubscriber(Subscriber s);

    <T> void notifySubscribers(T t);
}
