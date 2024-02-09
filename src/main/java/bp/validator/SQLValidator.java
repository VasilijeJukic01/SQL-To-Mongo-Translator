package bp.validator;

import bp.observer.Subscriber;
import bp.parser.Query;
import bp.parser.clauses.Clause;
import bp.validator.rules.*;

import java.util.ArrayList;
import java.util.List;

public class SQLValidator implements Validator<Query<Clause>> {

    private final List<Rule> rules;
    private List<Subscriber> subscribers;

    public SQLValidator() {
        this.rules = new ArrayList<>();
        this.subscribers = new ArrayList<>();
        generateRules();
    }

    @Override
    public boolean validate(Query<Clause> query) {
        if (query == null) return false;
        for (Rule rule : rules) {
            if (!rule.check(query)) {
                notifySubscribers(rule.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public void generateRules() {
        this.rules.add(new MandatoryRule("Mandatory"));
        this.rules.add(new GroupRule("Group"));
        this.rules.add(new WhereRule("Where"));
        this.rules.add(new JoinRule("Join"));
        this.rules.add(new OrderRule("Order"));
        this.rules.add(new CollectionRule("Collection"));
    }

    @Override
    public void addSubscriber(Subscriber s) {
        if(s == null) return;
        if(this.subscribers == null) this.subscribers = new ArrayList<>();
        if(this.subscribers.contains(s)) return;
        this.subscribers.add(s);
    }

    @Override
    public void removeSubscriber(Subscriber s) {
        if(s == null ||  this.subscribers == null || !this.subscribers.contains(s)) return;
        this.subscribers.remove(s);
    }

    @Override
    public <T> void notifySubscribers(T t) {
        if(t == null || this.subscribers == null || this.subscribers.isEmpty()) return;
        subscribers.forEach(s -> s.update(t));
    }
}
