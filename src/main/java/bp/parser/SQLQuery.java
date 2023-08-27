package main.java.bp.parser;

import main.java.bp.parser.clauses.Clause;

import java.util.ArrayList;
import java.util.List;

public class SQLQuery implements Query<Clause> {

    private final List<Clause> clauses;

    public SQLQuery() {
        this.clauses = new ArrayList<>();
    }

    @Override
    public void addClause(Clause clause) {
        this.clauses.add(clause);
    }

    @Override
    public List<Clause> getClauses() {
        return clauses;
    }
}
