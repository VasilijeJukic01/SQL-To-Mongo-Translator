package main.java.bp.parser.factory;

import main.java.bp.parser.clauses.Clause;

public class ClauseFactory implements AbstractFactory<Clause> {

    private final Class<? extends Clause> clauseClass;

    public ClauseFactory(Class<? extends Clause> clauseClass) {
        this.clauseClass = clauseClass;
    }

    @Override
    public Clause create() {
        try {
            return clauseClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
