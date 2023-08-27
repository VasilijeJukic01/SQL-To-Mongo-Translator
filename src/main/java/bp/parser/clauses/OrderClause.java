package main.java.bp.parser.clauses;

import main.java.bp.parser.KeyWord;

import java.util.ArrayList;
import java.util.List;

public class OrderClause extends Clause {

    private final List<String> args;
    private final List<String> columns;
    private final List<Integer> indicators;


    public OrderClause() {
        super(KeyWord.ORDER);
        this.args = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.indicators = new ArrayList<>();
    }

    @Override
    public void build() {
        for (String arg : args) {
            if (arg.equals("ASC")) {
                indicators.add(1);
                continue;
            }
            if (arg.equals("DESC")) {
                indicators.add(-1);
                continue;
            }
            columns.add(arg);
        }
    }

    public List<String> getArgs() {
        return args;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Integer> getIndicators() {
        return indicators;
    }

}
