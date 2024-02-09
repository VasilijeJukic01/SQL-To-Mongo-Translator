package bp.database;

import bp.database.controller.DBController;
import bp.database.data.Row;
import com.mongodb.client.*;
import bp.adapter.MongoQuery;
import org.bson.Document;

import java.util.*;

import static bp.constants.Constants.MONGO_DATABASE;
import static bp.constants.Constants.MONGO_URI;

public class Executor implements Database {

    private MongoClient connection;
    private final DBController controller;
    private MongoQuery mongoQuery;

    public Executor() {
        this.controller = new DBController();
    }

    private String getTargetTable(){
        for (int i = 0; i < mongoQuery.getDocuments().size(); i++)
            if(mongoQuery.getDocumentsClauses().get(i).equalsIgnoreCase("FROM"))
                return mongoQuery.getDocuments().get(i).toLowerCase();
        return "";
    }

    @Override
    public List<Row> getDataFromTable() {
        this.connection = controller.getConnection();

        MongoDatabase database = connection.getDatabase(MONGO_DATABASE);
        MongoCursor<Document> cursor = database.getCollection(getTargetTable()).aggregate(mongoQuery.getJsonDocs()).iterator();
        List<Row> rows = new ArrayList<>();

        while (cursor.hasNext()){
            Row row = new Row();
            row.setName(getTargetTable());

            Document d = cursor.next();

            for (Document.Entry<String, Object> entry : d.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Document) {
                    for (Map.Entry<String, Object> innerEntry : ((Document)value).entrySet()) {
                        key = innerEntry.getKey();
                        if (key.equals("_id")) continue;
                        value = innerEntry.getValue();
                        row.addField(key,value);
                    }
                }
                else row.addField(key,value);
            }
            rows.add(row);

        }

        controller.closeConnection(connection);
        return rows;
    }

    @Override
    public HashMap<String, List<String>> getCollections() {
        if (MONGO_DATABASE.isEmpty() || MONGO_URI.isEmpty()) return null;

        this.connection = controller.getConnection();

        HashMap<String, List<String>> map = new HashMap<>();
        MongoDatabase database = connection.getDatabase(MONGO_DATABASE);
        MongoIterable<String> collectionNames = database.listCollectionNames();

        for (String collectionName : collectionNames) {
            List<String> values = new ArrayList<>();
            MongoCollection<Document> collection = database.getCollection(collectionName);
            for (Document document : collection.find()) {
                for (String key : document.keySet()) {
                    if (!values.contains(key))
                        values.add(key);
                }
            }

            map.put(collectionName, values);
        }

        controller.closeConnection(connection);
        return map;
    }

    public void setMongoQuery(MongoQuery mongoQuery) {
        this.mongoQuery = mongoQuery;
    }
}
