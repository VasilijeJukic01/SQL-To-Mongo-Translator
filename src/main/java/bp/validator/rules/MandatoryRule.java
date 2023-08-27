package main.java.bp.validator.rules;

import main.java.bp.parser.Query;
import main.java.bp.parser.clauses.Clause;
import main.java.bp.parser.clauses.FromClause;
import main.java.bp.parser.clauses.SelectClause;

public class MandatoryRule extends Rule {

    public MandatoryRule(String name) {
        super(name);
    }

    private void prepareMessage(boolean select, boolean from) {
        if (!select && !from) super.message = "SELECT and FROM don't exist in query!";
        else if (select && !from) super.message = "FROM doesn't exists in query!";
        else super.message = "SELECT doesn't exists in query!";
    }

    @Override
    public boolean check(Query<Clause> query) {
        if(query == null){
            super.message = "Query is empty";
            return false;
        }
        boolean select = false;
        boolean from = false;

        for (Clause c : query.getClauses()) {
            if(c instanceof SelectClause) select = true;
            else if(c instanceof FromClause) from = true;

            if (select && from) return true;
        }

        prepareMessage(select, from);
        return false;
    }
}
