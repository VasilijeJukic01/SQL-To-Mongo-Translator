package main.java.bp.adapter;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoQuery {

    private final List<String> documents;
    private final List<String> documentsClauses;

    private List<Document> jsonDocs;

    public MongoQuery() {
        this.documents = new ArrayList<>();
        this.documentsClauses = new ArrayList<>();
        this.jsonDocs = new ArrayList<>();
    }

    public List<String> getDocuments() {
        return documents;
    }

    public List<String> getDocumentsClauses() {
        return documentsClauses;
    }

    public void setJsonDocs(List<Document> jsonDocs) {
        this.jsonDocs = jsonDocs;
    }

    public List<Document> getJsonDocs() {
        return jsonDocs;
    }
}
