package bp.validator.rules;

import bp.parser.Query;
import bp.parser.clauses.Clause;
import bp.parser.clauses.GroupClause;
import bp.parser.clauses.SelectClause;

public class GroupRule extends Rule {

    public GroupRule(String name) {
        super(name);
    }

    private boolean checkAggregations(String column) {
        return (column.contains("AVG") || column.contains("MAX") || column.contains("MIN") || column.contains("COUNT"));
    }

    @Override
    public boolean check(Query<Clause> query) {
        if (query == null) {
            super.message = "Query is empty";
            return true;
        }
        GroupClause groupClause = null;
        for (Clause c : query.getClauses()) {
            if(c instanceof GroupClause){
                groupClause = (GroupClause) c;
            }
        }
        if(groupClause == null) return true;
        for (Clause c : query.getClauses()) {
            if(c instanceof SelectClause){
                for (String column : ((SelectClause) c).getColumns()) {
                    if(!groupClause.getColumns().contains(column) && !checkAggregations(column)) {
                        super.message = "GROUP BY doesn't contain required columns!";
                        return false;
                    }
                }
                super.message = "";
                return true;
            }
        }
        return false;
    }
}
