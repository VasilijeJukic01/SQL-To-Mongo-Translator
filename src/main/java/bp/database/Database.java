package main.java.bp.database;

import main.java.bp.database.data.Row;

import java.util.HashMap;
import java.util.List;

public interface Database {

    List<Row> getDataFromTable();

    HashMap<String, List<String>> getCollections();
}
