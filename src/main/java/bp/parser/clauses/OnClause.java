package bp.parser.clauses;

import bp.parser.KeyWord;
import bp.parser.conditions.Condition;

import java.util.ArrayList;
import java.util.List;

public class OnClause extends Clause {

    private final List<String> args;
    private Condition condition;

    public OnClause() {
        super(KeyWord.ON);
        this.args = new ArrayList<>();
    }

    @Override
    public void build() {
        this.condition = new Condition();
        for (String arg : args) {
            if (condition.getLeftOperand().isEmpty()) {
                condition.setLeftOperand(arg);
            }
            else if (condition.getOperator().isEmpty()) {
                condition.setOperator(arg);
            }
            else if (condition.getRightOperand().isEmpty()) {
                condition.setRightOperand(arg);
            }
        }
    }

    public List<String> getArgs() {
        return args;
    }

    public Condition getCondition() {
        return condition;
    }
}
