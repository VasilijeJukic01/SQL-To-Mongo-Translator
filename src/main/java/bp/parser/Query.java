package main.java.bp.parser;

import java.util.List;

public interface Query<T> {

    void addClause(T t);

    List<T> getClauses();

}
