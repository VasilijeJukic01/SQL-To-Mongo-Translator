package bp.validator.rules;

import bp.parser.Query;
import bp.parser.clauses.Clause;

public abstract class Rule {

    protected String name;
    protected String message;

    public Rule(String name) {
        this.name = name;
    }

    public abstract boolean check(Query<Clause> query);

    public String getMessage() {
        return message;
    }
}
