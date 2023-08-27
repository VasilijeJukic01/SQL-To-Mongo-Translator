package main.java.bp.validator.rules;

import main.java.bp.parser.Query;
import main.java.bp.parser.clauses.Clause;
import main.java.bp.parser.clauses.WhereClause;
import main.java.bp.parser.conditions.Condition;

public class WhereRule extends Rule {

    public WhereRule(String name) {
        super(name);
    }

    private boolean checkOperand(String operand) {
        return (operand.contains("AVG(") || operand.contains("MAX(") || operand.contains("MIN("));
    }

    @Override
    public boolean check(Query<Clause> query) {
        for (Clause c : query.getClauses()) {
            if (c instanceof WhereClause) {
                for (Condition condition : ((WhereClause) c).getConditions()) {
                    if (checkOperand(condition.getLeftOperand()) || checkOperand(condition.getRightOperand())) {
                        super.message = "WHERE clause contains aggregation functions!";
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }

}
