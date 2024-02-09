package bp.parser;

public interface Parser<T> {

    T parse(String query);

}
