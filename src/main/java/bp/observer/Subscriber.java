package main.java.bp.observer;

public interface Subscriber {

    <T> void update(T t);
}
