package main.java.bp.core;

import main.java.bp.adapter.Adapter;
import main.java.bp.adapter.MongoAdapter;
import main.java.bp.database.Database;
import main.java.bp.database.Executor;
import main.java.bp.gui.view.DataTableModel;
import main.java.bp.parser.Parser;
import main.java.bp.parser.Query;
import main.java.bp.parser.SQLParser;
import main.java.bp.parser.clauses.Clause;
import main.java.bp.validator.SQLValidator;
import main.java.bp.validator.Validator;

import java.util.HashMap;
import java.util.List;

public class AppFramework {

    private static AppFramework instance;

    protected Gui gui;
    protected Database executor;
    protected Parser<Query<Clause>> parser;
    protected Validator<Query<Clause>> validator;
    protected Adapter adapter;

    protected DataTableModel tableModel;
    protected HashMap<String, List<String>> databaseCollections;

    public void run(){
        this.gui.start();
    }

    private AppFramework() {}

    public static AppFramework getAppFramework(){
        if(instance == null) {
            instance = new AppFramework();
        }
        return instance;
    }

    private void initDatabase() {
        this.parser = new SQLParser();
        this.validator = new SQLValidator();
        this.adapter = new MongoAdapter();
        this.executor = new Executor();
        this.tableModel = new DataTableModel();
    }

    public void init(Gui gui) {
        this.gui = gui;
        initDatabase();
        this.getDatabaseTables();
    }

    public Database getExecutor() {
        return executor;
    }

    public Parser<Query<Clause>> getParser() {
        return parser;
    }

    public Validator<Query<Clause>> getValidator() {
        return validator;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void readDataFromTable(){
        tableModel.setRows(this.executor.getDataFromTable());
    }

    public void getDatabaseTables() {
        this.databaseCollections = AppFramework.getAppFramework().getExecutor().getCollections();
    }

    public DataTableModel getTableModel() {
        return tableModel;
    }

    public HashMap<String, List<String>> getDatabaseCollections() {
        return databaseCollections;
    }
}
