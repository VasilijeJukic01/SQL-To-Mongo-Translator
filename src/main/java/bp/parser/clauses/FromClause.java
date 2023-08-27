package main.java.bp.parser.clauses;

import main.java.bp.parser.KeyWord;

public class FromClause extends Clause {

    private String table;

    public FromClause() {
        super(KeyWord.FROM);
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }

    @Override
    public void build() {

    }

}
