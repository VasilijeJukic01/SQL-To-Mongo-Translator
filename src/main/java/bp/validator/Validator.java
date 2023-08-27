package main.java.bp.validator;

import main.java.bp.observer.Publisher;

public interface Validator<T> extends Publisher {

    boolean validate(T t);

    void generateRules();
}
