package bp.parser.clauses;

import bp.parser.KeyWord;

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
