package bp.validator.rules;

import bp.parser.Query;
import bp.parser.clauses.Clause;
import bp.parser.clauses.JoinClause;
import bp.parser.clauses.OnClause;
import bp.parser.clauses.UsingClause;

public class JoinRule extends Rule {

    public JoinRule(String name) {
        super(name);
    }

    private void prepareMessage(boolean join, boolean using) {
        if (join && !using) super.message = "USING/ON doesn't exists in query, but JOIN has been used!";
        else if (!join && using) super.message = "JOIN doesn't exists in query, but USING has been used!";
    }

    @Override
    public boolean check(Query<Clause> query) {
        boolean join = false;
        boolean using = false;

        for (Clause c : query.getClauses()) {
            if (c instanceof JoinClause) {
                join = true;
            }
            if (c instanceof UsingClause || c instanceof OnClause) using = true;

            if (join && using) return true;
        }

        if (!join && !using) return true;
        prepareMessage(join, using);
        return false;
    }
}
