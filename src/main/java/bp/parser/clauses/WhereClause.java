package bp.parser.clauses;

import bp.core.AppFramework;
import bp.parser.KeyWord;
import bp.parser.Query;
import bp.parser.conditions.Condition;

import java.util.ArrayList;
import java.util.List;

public class WhereClause extends Clause {

    private final List<String> whereOperators = new ArrayList<>(List.of("BETWEEN", "LIKE", "IN", "<", ">", "<=", ">=", "=", "IS"));
    private final List<String> args;

    private final List<Condition> conditions;
    private final List<String> operators;

    private boolean containSubQuery;

    public WhereClause() {
        super(KeyWord.WHERE);
        this.operators = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.args = new ArrayList<>();
    }

    public Query<Clause> buildSubQuery() {
        String subQuery = getSubQueryString();
        return AppFramework.getAppFramework().getParser().parse(subQuery);
    }

    public String getSubQueryString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.size(); i++) {
            sb.append(args.get(i).replaceAll(",", "").replace("{", "").replace("}","")
                    .replace("FROM", "JOIN").replace("SELECT", "USING"));
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public void build() {
        Condition c = new Condition();
        for (String arg : args) {
            if (arg.equals("AND") || arg.equals("OR")) {
                operators.add(arg);
                conditions.add(c);
                c = new Condition();
                continue;
            }
            if (whereOperators.contains(arg)) {
                c.setOperator(arg);
                continue;
            }

            if (c.getLeftOperand().isEmpty() || c.getLeftOperand() == null) c.setLeftOperand(arg);
            else if (c.getRightOperand().isEmpty() || c.getRightOperand() == null) c.setRightOperand(arg);

            if (c.getRightOperand().contains("NOT")) c.setRightOperand(c.getRightOperand().replace("NOT", "NOT "+arg));
        }
        if (!c.getLeftOperand().isEmpty()) conditions.add(c);
    }

    public List<String> getArgs() {
        return args;
    }

    public List<String> getOperators() {
        return operators;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setContainSubQuery(boolean containSubQuery) {
        this.containSubQuery = containSubQuery;
    }

    public boolean isContainSubQuery() {
        return containSubQuery;
    }

}
