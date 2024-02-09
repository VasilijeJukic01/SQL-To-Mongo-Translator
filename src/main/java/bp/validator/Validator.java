package bp.validator;

import bp.observer.Publisher;

public interface Validator<T> extends Publisher {

    boolean validate(T t);

    void generateRules();
}
