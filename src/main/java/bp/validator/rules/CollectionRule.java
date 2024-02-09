package bp.validator.rules;

import bp.core.AppFramework;
import bp.parser.Query;
import bp.parser.clauses.Clause;
import bp.parser.clauses.FromClause;
import bp.parser.clauses.JoinClause;

import java.util.HashMap;
import java.util.List;

public class CollectionRule extends Rule {

    public CollectionRule(String name) {
        super(name);
    }

    @Override
    public boolean check(Query<Clause> query) {
        HashMap<String, List<String>> collectionsMap = AppFramework.getAppFramework().getDatabaseCollections();

        for (Clause clause : query.getClauses()) {
            String tableName = null;

            if (clause instanceof FromClause)
                tableName = ((FromClause) clause).getTable();
            else if (clause instanceof JoinClause)
                tableName = ((JoinClause) clause).getJoinTable();

            if (tableName != null) {
                String lowercaseTableName = tableName.toLowerCase();
                if (!collectionsMap.containsKey(lowercaseTableName)) {
                    super.message = "Invalid collection: " + tableName;
                    return false;
                }
            }
        }
        return true;
    }


}
