package bp.gui.controller;

import bp.adapter.Adapter;
import bp.adapter.MongoAdapter;
import bp.adapter.MongoQuery;
import bp.core.AppFramework;
import bp.database.Executor;
import bp.gui.view.MainFrame;
import bp.parser.Query;
import bp.parser.clauses.Clause;
import bp.validator.Validator;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RunController extends AbstractAction {

    public RunController() {
        super.putValue(NAME, "Run");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String query = MainFrame.getInstance().getTextPane().getText();
        Query<Clause> parsedQuery = parseQuery(query);
        if (!validateQuery(parsedQuery)) return;
        executeQuery(parsedQuery);
    }

    private Query<Clause> parseQuery(String query) {
        return AppFramework.getAppFramework().getParser().parse(query);
    }

    private boolean validateQuery(Query<Clause> query) {
        Validator<Query<Clause>> validator = AppFramework.getAppFramework().getValidator();
        return validator.validate(query);
    }

    private void executeQuery(Query<Clause> query) {
        Adapter adapter = AppFramework.getAppFramework().getAdapter();
        MongoAdapter mongoAdapter = (MongoAdapter) adapter;

        mongoAdapter.setQuery(query);
        MongoQuery mongoQuery = (MongoQuery) adapter.getQuery();

        Executor executor = (Executor) AppFramework.getAppFramework().getExecutor();
        executor.setMongoQuery(mongoQuery);

        AppFramework.getAppFramework().readDataFromTable();
    }
}
