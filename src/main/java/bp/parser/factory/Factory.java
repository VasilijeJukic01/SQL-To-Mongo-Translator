package main.java.bp.parser.factory;


import main.java.bp.parser.clauses.*;

import java.util.HashMap;
import java.util.Map;

public class Factory {

    private static Factory instance;

    private final Map<String, AbstractFactory<Clause>> factories = new HashMap<>();

    private Factory() {}

    private void init() {
        factories.put("SelectClause", new ClauseFactory(SelectClause.class));
        factories.put("FromClause", new ClauseFactory(FromClause.class));
        factories.put("WhereClause", new ClauseFactory(WhereClause.class));
        factories.put("GroupClause", new ClauseFactory(GroupClause.class));
        factories.put("HavingClause", new ClauseFactory(HavingClause.class));
        factories.put("OnClause", new ClauseFactory(OnClause.class));
        factories.put("UsingClause", new ClauseFactory(UsingClause.class));
        factories.put("JoinClause", new ClauseFactory(JoinClause.class));
        factories.put("OrderClause", new ClauseFactory(OrderClause.class));
    }

    public Clause createClause(String key) {
        return factories.get(key).create();
    }

    public static Factory getInstance() {
        if (instance == null) {
            instance = new Factory();
            instance.init();
        }
        return instance;
    }

}
