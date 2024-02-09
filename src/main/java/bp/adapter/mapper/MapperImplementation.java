package bp.adapter.mapper;

import bp.adapter.MongoQuery;
import bp.core.AppFramework;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class MapperImplementation implements Mapper {

    private final String matchTemplate = "{\n" + " $match: %mtc" + "\n}";
    private final String lookupTemplate = "{\n" +
            "  $lookup: {\n" +
            "    from: \"%join\",\n" +
            "    localField: \"%using1\",\n" +
            "    foreignField: \"%using2\",\n" +
            "    as: \"%join\"\n" +
            "  }\n" +
            "}";
    private final String unwindTemplate = "{ $unwind: \"$%unw1\" }";
    private final String projectTemplate = "{ $project: %proj" + " }";
    private final String groupTemplate = "{\n $group: {\n" +
            "      _id: {%grby},\n %agr" +
            " } }";
    private final String sortTemplate = "{\n $sort: %srt\n" + "}";

    private String currentMatch;
    private String currentLookup;
    private String currentProject;
    private String currentGroup;
    private String currentUnwind;
    private String currentSort;

    private int lookupCounter = 0, groupCounter = 0, onCounter = 0;


    private List<String> getJoinClause(MongoQuery mongoQuery) {
        List<String> joins = new ArrayList<>();
        for (int i = 0; i < mongoQuery.getDocumentsClauses().size(); i++) {
            if (mongoQuery.getDocumentsClauses().get(i).equalsIgnoreCase("JOIN")) {
                joins.add(mongoQuery.getDocuments().get(i).toLowerCase());
            }
        }
        return joins;
    }

    private String getFromClause(MongoQuery mongoQuery) {
        String from = "";
        for (int i = 0; i < mongoQuery.getDocumentsClauses().size(); i++) {
            if (mongoQuery.getDocumentsClauses().get(i).equalsIgnoreCase("FROM")) {
                from = mongoQuery.getDocuments().get(i);
            }
        }
        return from;
    }

    private void lookupFix(MongoQuery mongoQuery) {
        String from = getFromClause(mongoQuery);
        List<String> joins = getJoinClause(mongoQuery);

        HashMap<String, List<String>> map = AppFramework.getAppFramework().getDatabaseCollections();

        int startFrom = currentLookup.indexOf("from: "), endFrom = currentLookup.indexOf(',');
        String joinTable = currentLookup.substring(startFrom, endFrom-1).substring(7);
        int startLocal = currentLookup.indexOf("localField: "), endLocal = currentLookup.indexOf(',',currentLookup.indexOf(',')+1);
        String localField = currentLookup.substring(startLocal, endLocal-1).substring(13);
        String newLocalField = "";

        for (String key : map.keySet()) {
            if (!key.equalsIgnoreCase(from)) {
                List<String> columns = map.get(key);
                if (columns.contains(localField) && !key.equals(joinTable) && joins.contains(key)) {
                    newLocalField = "\""+key+"."+localField+"\"";
                    break;
                }
            }
        }
        if (!newLocalField.equals(""))
            currentLookup = currentLookup.replace("localField: "+"\""+localField+"\"", "localField: "+newLocalField);
    }

    private void matchFix(MongoQuery mongoQuery) {
        String from = getFromClause(mongoQuery);
        List<String> joins = getJoinClause(mongoQuery);

        HashMap<String, List<String>> map = AppFramework.getAppFramework().getDatabaseCollections();
        List<String> currentCollection = map.get(from.toLowerCase());
        for (String key : map.keySet()) {
            if (key.equalsIgnoreCase(from)) continue;
            List<String> columns = map.get(key);
            for (String column : columns) {
                if (currentMatch.contains(column) && joins.contains(key) && !currentCollection.contains(column)) {
                    currentMatch = currentMatch.replace(column, "\""+key+"."+column+"\"");
                }
            }
        }
    }

    private void groupFix(MongoQuery mongoQuery) {
        String from = getFromClause(mongoQuery);
        List<String> joins = getJoinClause(mongoQuery);

        HashMap<String, List<String>> map = AppFramework.getAppFramework().getDatabaseCollections();
        for (String key : map.keySet()) {
            if (key.equalsIgnoreCase(from)) continue;
            List<String> columns = map.get(key);
            for (String column : columns) {
                if (currentGroup.contains(column) && joins.contains(key)) {
                    currentGroup = currentGroup.replace("\"$"+column+"\"", "\"$"+key+"."+column+"\"");
                }
            }
        }
    }

    @Override
    public void buildJsonFiles(Object o) {
        if (!(o instanceof MongoQuery)) return;

        resetAll();
        MongoQuery q = (MongoQuery) o;
        List<Document> sorts = new ArrayList<>();
        List<Document> template = new ArrayList<>();

        System.out.println("-----------------------------------------------------------------");

        for (int i = 0; i < q.getDocuments().size(); i++) {
            mapData(i, q, sorts, template);

            if (lookupCounter == 2) {
                currentLookup = currentLookup.replaceAll("field:", "Field:");
                lookupFix(q);
                template.add(Document.parse(currentLookup));
                template.add(Document.parse(currentUnwind));
                System.out.println(currentLookup+",");
                System.out.println(currentUnwind+",");
                resetLookup();
            }
            if (groupCounter == 2) {
                groupFix(q);
                System.out.println(currentGroup+",");
                template.add(Document.parse(currentGroup));
                groupCounter = 0;
            }
        }
        if (groupCounter == 1) {
            currentGroup = currentGroup.replace("%grby", "");
            currentGroup = currentGroup.replace(",\n %agr", "");
            groupFix(q);
            System.out.println(currentGroup+",");
            template.add(Document.parse(currentGroup));
        }
        System.out.println(currentProject);
        System.out.println("-----------------------------------------------------------------");
        template.addAll(sorts);
        template.add(Document.parse(currentProject));
        q.setJsonDocs(template);
    }

    private void mapData(int i, MongoQuery q, List<Document> sorts, List<Document> template) {
        switch (q.getDocumentsClauses().get(i)) {
            case "PROJECTION":
                currentProject = currentProject.replace("%proj", q.getDocuments().get(i)).toLowerCase();
                break;
            case "USING":
                currentLookup = currentLookup.replaceAll("%using1", q.getDocuments().get(i)).toLowerCase();
                currentLookup = currentLookup.replaceAll("%using2", q.getDocuments().get(i)).toLowerCase();
                lookupCounter++;
                break;
            case "ON_LEFT":
                onCounter++;
                currentLookup = currentLookup.replaceAll("%using1", q.getDocuments().get(i)).toLowerCase();
                if (onCounter == 2) lookupCounter++;
                break;
            case "ON_RIGHT":
                onCounter++;
                currentLookup = currentLookup.replaceAll("%using2", q.getDocuments().get(i)).toLowerCase();
                if (onCounter == 2) lookupCounter++;
                break;
            case "SORT":
                currentSort = currentSort.replace("%srt", q.getDocuments().get(i)).toLowerCase();
                System.out.println(currentSort+",");
                sorts.add(Document.parse(currentSort));
                break;
            case "JOIN":
                currentLookup = currentLookup.replaceAll("%join", q.getDocuments().get(i)).toLowerCase();
                currentUnwind = currentUnwind.replace("%unw1", q.getDocuments().get(i)).toLowerCase();
                lookupCounter++;
                break;
            case "GROUP":
                currentGroup = currentGroup.replace("%grby", q.getDocuments().get(i)).toLowerCase();
                groupCounter++;
                break;
            case "AGGREGATION":
                currentGroup = currentGroup.replace("%agr", q.getDocuments().get(i)).toLowerCase();
                groupCounter++;
                break;
            case "MATCH":
            case "HAVING":
                currentMatch = currentMatch.replace("%mtc", q.getDocuments().get(i)).toLowerCase().replace("null", "NULL");
                if (currentMatch.contains("$ne: NULL")) currentMatch = currentMatch.replace("NULL", "\"NULL\"");
                matchFix(q);
                System.out.println(currentMatch+",");
                template.add(Document.parse(currentMatch));
                currentMatch = matchTemplate;
                break;
            default: break;
        }
    }

    private void resetAll() {
        lookupCounter = groupCounter = onCounter = 0;
        currentLookup = lookupTemplate;
        currentUnwind = unwindTemplate;
        currentMatch = matchTemplate;
        currentProject = projectTemplate;
        currentGroup = groupTemplate;
        currentSort = sortTemplate;
    }

    private void resetLookup() {
        currentLookup = lookupTemplate;
        currentUnwind = unwindTemplate;
        lookupCounter = 0;
        onCounter = 0;
    }

}
