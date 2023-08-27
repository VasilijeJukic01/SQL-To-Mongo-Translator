package main.java.bp.parser.clauses;

import main.java.bp.parser.KeyWord;
import main.java.bp.parser.conditions.Condition;

import java.util.ArrayList;
import java.util.List;

public class HavingClause extends Clause {

    private final List<String> args;
    private final List<Condition> conditions;
    private final List<String> operators;

    public HavingClause() {
        super(KeyWord.HAVING);
        this.operators = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.args = new ArrayList<>();
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

            if (c.getLeftOperand().equals("")) buildLeftOperand(arg, c);
            else if(c.getOperator().equals("")) c.setOperator(arg);
            else if (c.getRightOperand().equals("")) buildRightOperand(arg, c);
        }

        if (!c.getLeftOperand().equals("")) conditions.add(c);
    }

    private void buildLeftOperand(String arg, Condition c) {
        if(!check(arg).equals("")) {
            if (check(arg).equals("COUNT")) c.setLeftOperand(arg.substring(6, arg.length() - 1));
            else c.setLeftOperand(check(arg)+arg.substring(4, arg.length() - 1));
        }
        else c.setLeftOperand(arg);
    }

    private void buildRightOperand(String arg, Condition c) {
        if(!check(arg).equals("")) {
            if (check(arg).equals("COUNT")) c.setRightOperand(arg.substring(6, arg.length() - 1));
            else c.setRightOperand(check(arg)+arg.substring(4, arg.length() - 1));
        }
        else c.setRightOperand(arg);
    }

    private String check(String arg){
        if(arg.contains("AVG("))
            return "AVG";
        if(arg.contains("MAX("))
            return "MAX";
        if(arg.contains("MIN("))
            return "MIN";
        if (arg.contains("COUNT("))
            return "COUNT";
        return "";
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
}
