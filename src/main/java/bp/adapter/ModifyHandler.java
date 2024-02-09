package bp.adapter;

import bp.core.AppFramework;

import java.util.HashMap;
import java.util.List;

public class ModifyHandler {

    public ModifyHandler() {

    }

    // Projection-Join Modification
    public void projectionModification(MongoQuery mongoQuery, List<String> joins, List<String> groupColumns, List<String> aggrCols) {
        for (int i = 0; i < mongoQuery.getDocuments().size(); i++) {
            if (mongoQuery.getDocumentsClauses().get(i).equals("PROJECTION")) {
                for (String key : joins) {
                    HashMap<String, List<String>> map = AppFramework.getAppFramework().getDatabaseCollections();
                    List<String> columns = map.get(key.toLowerCase());
                    for (String column : columns) {
                        if (mongoQuery.getDocuments().get(i).contains(column.toUpperCase()) && !column.equals("_id") && !groupColumns.contains(column.toUpperCase())) {
                            if (!aggrCols.contains(column.toUpperCase()))
                                mongoQuery.getDocuments().set(i, mongoQuery.getDocuments().get(i).replace(column.toUpperCase(), "\"" + key + "." + column + "\""));
                        }
                    }
                }
            }
        }
    }

    // Group Modification
    public void groupModifications(MongoQuery mongoQuery, List<String> groupColumns) {
        for (int i = 0; i < mongoQuery.getDocuments().size(); i++) {
            if (mongoQuery.getDocumentsClauses().get(i).equals("PROJECTION")) {
                for (String column : groupColumns) {
                    if (mongoQuery.getDocuments().get(i).contains(column.toUpperCase())) {
                        String formatted = column+":\"$_id."+column+"\"";
                        mongoQuery.getDocuments().set(i, mongoQuery.getDocuments().get(i).replace(column.toUpperCase()+":1", formatted));
                    }
                }
            }
        }
    }

    // Sort Modification
    public void sortModifications(MongoQuery mongoQuery, List<String> joins) {
        for (int i = 0; i < mongoQuery.getDocuments().size(); i++) {
            if (mongoQuery.getDocumentsClauses().get(i).equals("SORT")) {
                for (String key : joins) {
                    HashMap<String, List<String>> map = AppFramework.getAppFramework().getDatabaseCollections();
                    List<String> columns = map.get(key.toLowerCase());
                    for (String column : columns) {
                        if (mongoQuery.getDocuments().get(i).contains(column.toUpperCase()) && !column.equals("_id")) {
                            String formatted = "\""+key+"."+column+"\":";
                            mongoQuery.getDocuments().set(i, mongoQuery.getDocuments().get(i).replace(column.toUpperCase()+":", formatted));
                        }
                    }
                }
            }
        }
    }

}
