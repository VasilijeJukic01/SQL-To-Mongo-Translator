package bp.adapter;

import bp.adapter.builder.Builder;
import bp.adapter.builder.ConditionBuilder;
import bp.parser.SQLQuery;
import bp.parser.clauses.*;
import bp.parser.conditions.Condition;

import java.util.ArrayList;
import java.util.List;

public class ParameterConverter {

    private final Builder<String, Condition> conditionBuilder;
    private final ModifyHandler modifyHandler;

    private boolean andFlag, orFlag, distinctFlag;
    private List<String> joins, groupColumns, aggregationColumns;
    private String betweenOperator = "";

    public ParameterConverter() {
        this.conditionBuilder = new ConditionBuilder();
        this.modifyHandler = new ModifyHandler();
    }

    // Parameters Extractor
    public MongoQuery extractParameters(SQLQuery q) {
        MongoQuery mongoQuery = new MongoQuery();
        reset();

        for (Clause c : q.getClauses()) {
            StringBuilder sb = new StringBuilder();
            switch (c.getKeyword()) {
                case SELECT:
                    selectClauseHandle(sb, c, mongoQuery); break;
                case FROM:
                    fromClauseHandle(c, mongoQuery); break;
                case ORDER:
                   orderClauseHandle(sb, c, mongoQuery); break;
                case JOIN:
                    joinClauseHandle(c, mongoQuery); break;
                case USING:
                    usingClauseHandle(c, mongoQuery); break;
                case ON:
                    onClauseHandle(c, mongoQuery); break;
                case GROUP:
                   groupClauseHandle(sb, c, mongoQuery); break;
                case WHERE:
                    whereHandle(sb, c, mongoQuery); break;
                case HAVING:
                    havingHandle(sb, c, mongoQuery); break;
                default: break;
            }
        }
        modifyHandler.sortModifications(mongoQuery, joins);
        modifyHandler.groupModifications(mongoQuery, groupColumns);
        modifyHandler.projectionModification(mongoQuery, joins, groupColumns, aggregationColumns);
        return mongoQuery;
    }

    // Handlers
    private void selectClauseHandle(StringBuilder sb, Clause c, MongoQuery mongoQuery) {
        projectionHandle(sb, c, mongoQuery);
        aggregationHandle(sb, c, mongoQuery);
    }

    private void projectionHandle(StringBuilder sb, Clause c, MongoQuery mongoQuery) {
        sb.append("{");
        for (String column : ((SelectClause)c).getColumns()) {
            if (column.equals("*")) break;
            if (((SelectClause) c).isDistinct()) {
                distinctFlag = true;
                sb.append(column).append(":\"$_id.").append(column).append("\",");
            }
            else sb.append(column).append(":1,");
        }
        sb.append("_id"+":0").append("}");
        mongoQuery.getDocumentsClauses().add("PROJECTION");
        mongoQuery.getDocuments().add(sb.toString());
    }

    private void aggregationHandle(StringBuilder sb, Clause c, MongoQuery mongoQuery) {
        if (((SelectClause)c).getAggregations().size() == 0) return;
        sb.delete(0, sb.length());
        List<String> aggregations = aggregationColumns = ((SelectClause)c).getAggregations();
        List<String> indicators = ((SelectClause)c).getIndicators();

        for (int i = 0; i < aggregations.size(); i++) {
            if (indicators.get(i).equals("COUNT")) {
                sb.append(indicators.get(i)).append(aggregations.get(i)).append(": { $sum: 1 },");
            }
            else sb.append(indicators.get(i)).append(aggregations.get(i)).append(": { $").append(indicators.get(i)).append(": \"$").append(aggregations.get(i)).append("\" },");
        }
        sb.deleteCharAt(sb.length()-1);
        mongoQuery.getDocumentsClauses().add("AGGREGATION");
        mongoQuery.getDocuments().add(sb.toString());
    }

    private void fromClauseHandle(Clause c, MongoQuery mongoQuery) {
        mongoQuery.getDocumentsClauses().add("FROM");
        mongoQuery.getDocuments().add(((FromClause)c).getTable());
    }

    private void joinClauseHandle(Clause c, MongoQuery mongoQuery) {
        mongoQuery.getDocumentsClauses().add("JOIN");
        mongoQuery.getDocuments().add(((JoinClause)c).getJoinTable());
        joins.add(((JoinClause)c).getJoinTable());
    }

    private void usingClauseHandle(Clause c, MongoQuery mongoQuery) {
        mongoQuery.getDocumentsClauses().add("USING");
        mongoQuery.getDocuments().add(((UsingClause)c).getColumn());
    }

    private void onClauseHandle(Clause c, MongoQuery mongoQuery) {
        mongoQuery.getDocumentsClauses().add("ON_LEFT");
        mongoQuery.getDocuments().add(((OnClause)c).getCondition().getLeftOperand());
        mongoQuery.getDocumentsClauses().add("ON_RIGHT");
        mongoQuery.getDocuments().add(((OnClause)c).getCondition().getRightOperand());
    }

    private void orderClauseHandle(StringBuilder sb, Clause c, MongoQuery mongoQuery) {
        sb.append("{");
        for (int i = 0; i < ((OrderClause)c).getColumns().size(); i++) {
            if (distinctFlag) {
                sb.append("\"_id.").append(((OrderClause) c).getColumns().get(i)).append("\":").append(((OrderClause) c).getIndicators().get(i)).append(",");
            }
            else sb.append(((OrderClause)c).getColumns().get(i)).append(":").append(((OrderClause)c).getIndicators().get(i)).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}");
        mongoQuery.getDocumentsClauses().add("SORT");
        mongoQuery.getDocuments().add(sb.toString());
    }

    private void groupClauseHandle(StringBuilder sb, Clause c, MongoQuery mongoQuery) {
        groupColumns = ((GroupClause)c).getColumns();
        for (String column : ((GroupClause)c).getColumns()) {
            sb.append(column).append(": \"$").append(column).append("\",");
        }
        sb.deleteCharAt(sb.length()-1);
        mongoQuery.getDocumentsClauses().add("GROUP");
        mongoQuery.getDocuments().add(sb.toString());
    }

    private void whereHandle(StringBuilder sb, Clause c, MongoQuery mongoQuery) {
        WhereClause whereClause = (WhereClause) c;
        processConditions(sb, whereClause.getConditions(), whereClause.getOperators(), "MATCH", mongoQuery);
    }

    private void havingHandle(StringBuilder sb, Clause c, MongoQuery mongoQuery) {
        HavingClause havingClause = (HavingClause) c;
        processConditions(sb, havingClause.getConditions(), havingClause.getOperators(), "HAVING", mongoQuery);
    }

    private void processConditions(StringBuilder sb, List<Condition> conditions, List<String> operators, String keyword, MongoQuery mongoQuery) {
        // Case I - One condition
        if (conditions.size() == 1) {
            sb.append(conditionBuilder.build(conditions.get(0)));
            mongoQuery.getDocumentsClauses().add(keyword);
            mongoQuery.getDocuments().add(sb.toString());
            return;
        }

        orFlag = operators.contains("OR");
        andFlag = operators.contains("AND");

        if (conditions.size() > 1 && ((!orFlag && andFlag) || (orFlag && !andFlag))) {
            sb.append("{ $").append(operators.get(0)).append(": [ ");
            for (Condition condition : conditions) {
                sb.append(conditionBuilder.build(condition)).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" ] }");
            mongoQuery.getDocumentsClauses().add(keyword);
            mongoQuery.getDocuments().add(sb.toString());
            return;
        }

        // Case II - More conditions
        sb.append("{ $or: [");
        List<Integer> andIndexes = new ArrayList<>();
        List<Integer> skipList = new ArrayList<>();

        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).equalsIgnoreCase("AND")) {
                andIndexes.add(i);
                if (!skipList.contains(i)) skipList.add(i);
                if (!skipList.contains(i + 1)) skipList.add(i + 1);
            }
        }

        for (Integer index : andIndexes) {
            sb.append("{ $and: [");
            for (int i = 0; i < conditions.size(); i++) {
                if (i == index || i == index + 1) {
                    String operator = conditions.get(i).getOperator();
                    if (operator.isEmpty()) betweenOperator = conditions.get(i).getLeftOperand();
                    else sb.append(conditionBuilder.build(conditions.get(i))).append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" ] },");
        }

        for (int i = 0; i < conditions.size(); i++) {
            if (!skipList.contains(i)) {
                String operator = conditions.get(i).getOperator();
                if (operator.isEmpty()) betweenOperator = conditions.get(i).getLeftOperand();
                else sb.append(conditionBuilder.build(conditions.get(i))).append(",");
            }
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("] }");

        mongoQuery.getDocumentsClauses().add(keyword);
        if (sb.toString().contains("%placeholder"))
            mongoQuery.getDocuments().add(sb.toString().replace("%placeholder", betweenOperator));
        else mongoQuery.getDocuments().add(sb.toString());
    }

    private void reset() {
        this.andFlag = this.orFlag = this.distinctFlag = false;
        this.betweenOperator = "";
        this.joins = new ArrayList<>();
        this.groupColumns = new ArrayList<>();
        this.aggregationColumns = new ArrayList<>();
    }

}
