package bp.validator.rules;

import bp.parser.Query;
import bp.parser.clauses.Clause;
import bp.parser.clauses.OrderClause;

import java.util.List;

public class OrderRule extends Rule {

    public OrderRule(String name) {
        super(name);
    }

    @Override
    public boolean check(Query<Clause> query) {

        for (Clause clause : query.getClauses()) {
            if (clause instanceof OrderClause) {
                OrderClause orderClause = (OrderClause) clause;
                List<Integer> indicators = orderClause.getIndicators();

                if (orderClause.getColumns().size() != indicators.size()) {
                    super.message = "ORDER BY requires an indicator ASC/DESC for each column!";
                    return false;
                }

                for (int indicator : indicators) {
                    if (indicator != 1 && indicator != -1) {
                        super.message = "Invalid indicator in ORDER BY, valid indicators are ASC/DESC!";
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
