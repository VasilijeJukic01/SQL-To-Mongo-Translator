package main.java.bp.adapter.builder;

import main.java.bp.parser.conditions.Condition;

public class ConditionBuilder implements Builder<String, Condition> {

    public ConditionBuilder() {}

    @Override
    public String build(Condition c) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ").append(c.getLeftOperand()).append(":");

        switch (c.getOperator()) {
            case "=":
                sb.append(c.getRightOperand());
                break;
            case "<=":
                sb.append("{ $lte: ").append(c.getRightOperand()).append(" }");
                break;
            case ">=":
                sb.append("{ $gte: ").append(c.getRightOperand()).append(" }");
                break;
            case "<":
                sb.append("{ $lt: ").append(c.getRightOperand()).append(" }");
                break;
            case ">":
                sb.append("{ $gt: ").append(c.getRightOperand()).append(" }");
                break;
            case "BETWEEN":
                sb.append("{ $gte: ").append(c.getRightOperand()).append(", $lte: %placeholder }");
                break;
            case "IN":
                sb.append("{ $in: ").append(c.getRightOperand()).append(" }");
                break;
            case "IS":
                buildIsCondition(sb, c);
                break;
            case "LIKE":
                buildLikeCondition(sb, c);
                break;
        }

        sb.append(" }");
        return sb.toString();
    }

    private void buildIsCondition(StringBuilder sb, Condition c) {
        if (c.getRightOperand().contains("NOT NULL"))
            sb.append("{ $ne: null }");
        else if (c.getRightOperand().contains("NULL"))
            sb.append(" \"null\"");
    }

    private void buildLikeCondition(StringBuilder sb, Condition c) {
        String formatted = c.getRightOperand().replaceAll("'", "/").replaceAll("_", " ");
        int len = formatted.length();
        // Check -> 'TEXT%'
        if (len > 2 && formatted.charAt(len - 2) == '%' && formatted.charAt(1) != '%') {
            formatted = formatted.replaceFirst("/", "/^").replaceAll("%", "");
        }
        // Check -> '%TEXT'
        else if (len > 2 && formatted.charAt(len - 2) != '%' && formatted.charAt(1) == '%') {
            formatted = (formatted.substring(0, len - 1) + '$' + formatted.substring(len) + "/").replaceAll("%", "");
        }
        // Check -> '%TEXT%'
        else if (formatted.contains("%")) formatted = formatted.replaceAll("%", "");

        sb.append(formatted).append("i");
    }

}
