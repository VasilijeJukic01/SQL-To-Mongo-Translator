package main.java.bp.parser.clauses;

import main.java.bp.parser.KeyWord;

import java.util.ArrayList;
import java.util.List;

public class GroupClause extends Clause {

    private final List<String> columns;

    public GroupClause() {
        super(KeyWord.GROUP);
        this.columns = new ArrayList<>();
    }

    public List<String> getColumns() {
        return columns;
    }

    @Override
    public void build() {

    }

}
