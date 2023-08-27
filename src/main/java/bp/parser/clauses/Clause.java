package main.java.bp.parser.clauses;

import main.java.bp.parser.KeyWord;

public abstract class Clause {

    public KeyWord keyword;

    public Clause(KeyWord keyword) {
        this.keyword = keyword;
    }

    public abstract void build();

    public KeyWord getKeyword() {
        return keyword;
    }
}
