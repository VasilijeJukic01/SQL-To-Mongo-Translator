package main.java.bp.parser.clauses;

import main.java.bp.parser.KeyWord;

import java.util.ArrayList;
import java.util.List;

public class SelectClause extends Clause {

    private final List<String> columns;
    private final List<String> aggregations;
    private final List<String> indicators;

    private boolean distinct;

    public SelectClause() {
        super(KeyWord.SELECT);
        this.columns = new ArrayList<>();
        this.aggregations = new ArrayList<>();
        this.indicators = new ArrayList<>();
    }

    @Override
    public void build(){
        List<Integer> idx = new ArrayList<>();
        int cnt = 0;
        for (String column : columns) {
            if(column.contains("AVG(")){
                aggregations.add(column.substring(4,column.length()-1));
                indicators.add("AVG");
                idx.add(cnt);
            }
            else if(column.contains("MIN(")){
                aggregations.add(column.substring(4,column.length()-1));
                indicators.add("MIN");
                idx.add(cnt);
            }
            else if(column.contains("MAX(")){
                aggregations.add(column.substring(4,column.length()-1));
                indicators.add("MAX");
                idx.add(cnt);
            }
            else if (column.contains("COUNT(")){
                aggregations.add(column.substring(6,column.length()-1));
                indicators.add("COUNT");
                idx.add(cnt);
            }
            cnt++;
        }
        for (Integer i : idx) {
            columns.set(i, columns.get(i).replace("(", ""));
            columns.set(i, columns.get(i).replace(")", ""));
        }
    }

    public void addColumn(String column) {
        this.columns.add(column);
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<String> getAggregations() {
        return aggregations;
    }

    public List<String> getIndicators() {
        return indicators;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

}
