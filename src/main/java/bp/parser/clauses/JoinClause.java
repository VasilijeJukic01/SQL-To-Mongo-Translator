package main.java.bp.parser.clauses;

import main.java.bp.parser.KeyWord;

public class JoinClause extends Clause {

    private String joinTable;

    public JoinClause() {
        super(KeyWord.JOIN);
    }

    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    public String getJoinTable() {
        return joinTable;
    }

    @Override
    public void build() {

    }

}
