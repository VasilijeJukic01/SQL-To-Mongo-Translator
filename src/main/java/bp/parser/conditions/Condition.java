package bp.parser.conditions;

import java.util.Objects;

public class Condition {

    private String leftOperand = "";
    private String operator = "";
    private String rightOperand = "";

    public Condition() {

    }

    public Condition(String leftOperand, String operator, String rightOperand) {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    public boolean isConditionEmpty() {
        return leftOperand.isEmpty() && rightOperand.isEmpty() && operator.isEmpty();
    }

    public String getLeftOperand() {
        return leftOperand;
    }

    public String getOperator() {
        return operator;
    }

    public String getRightOperand() {
        return rightOperand;
    }

    public void setLeftOperand(String leftOperand) {
        this.leftOperand = leftOperand;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setRightOperand(String rightOperand) {
        this.rightOperand = rightOperand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return Objects.equals(leftOperand, condition.leftOperand) && Objects.equals(operator, condition.operator) && Objects.equals(rightOperand, condition.rightOperand);
    }
}
