package bp.core;

import bp.adapter.Adapter;
import bp.adapter.MongoAdapter;
import bp.database.Database;
import bp.database.Executor;
import bp.gui.view.DataTableModel;
import bp.parser.Parser;
import bp.parser.Query;
import bp.parser.SQLParser;
import bp.parser.clauses.Clause;
import bp.validator.SQLValidator;
import bp.validator.Validator;

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
