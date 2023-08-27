package main.java.bp.parser;

import main.java.bp.parser.clauses.*;
import main.java.bp.parser.factory.Factory;

import java.util.ArrayList;
import java.util.List;

public class SQLParser implements Parser<Query<Clause>> {

    private final List<String> keyWords = new ArrayList<>(List.of("SELECT", "FROM", "WHERE", "JOIN", "USING", "ON", "HAVING", "GROUP", "ORDER"));

    @Override
    public Query<Clause> parse(String text) {
        SQLQuery query = new SQLQuery();
        boolean distinctActive = false;
        boolean subQuery = false;
        GroupClause distinctGroup = new GroupClause();

        String[] parts = text.split(" ");

        for (String s : parts) {
            String part = s.toUpperCase();
            if(part.equals("BY")) continue;
            if (!part.contains("[") && !part.contains("]")) {
                part = part.replaceAll(",", "");
            }

            // Keyword
            if (keyWords.contains(part) && !subQuery) {
                try {
                    Clause clause = Factory.getInstance().createClause(part.charAt(0)+part.substring(1).toLowerCase()+"Clause");
                    query.addClause(clause);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Parameters
            else {
                if (query.getClauses().size() == 0) return null;
                Clause lastClause = query.getClauses().get(query.getClauses().size() - 1);
                if (lastClause instanceof SelectClause) {
                    if (part.equals("DISTINCT")) ((SelectClause) lastClause).setDistinct(true);
                    else {
                        ((SelectClause) lastClause).addColumn(part);
                        if (((SelectClause) lastClause).isDistinct()) {
                            distinctActive = true;
                            distinctGroup.getColumns().add(part);
                        }
                    }
                }
                else if (lastClause instanceof FromClause) {
                    ((FromClause) lastClause).setTable(part);
                }
                else if (lastClause instanceof WhereClause) {
                    if (part.contains("{")) {
                        subQuery = true;
                        ((WhereClause) lastClause).setContainSubQuery(true);
                    }
                    else if (part.contains("}")) subQuery = false;
                    ((WhereClause) lastClause).getArgs().add(part);
                }
                else if (lastClause instanceof JoinClause) {
                    ((JoinClause) lastClause).setJoinTable(part);
                }
                else if (lastClause instanceof UsingClause) {
                    ((UsingClause) lastClause).setColumn(part);
                }
                else if (lastClause instanceof OnClause) {
                    ((OnClause) lastClause).getArgs().add(part);
                }
                else if (lastClause instanceof HavingClause) {
                    ((HavingClause) lastClause).getArgs().add(part);
                }
                else if (lastClause instanceof GroupClause) {
                    ((GroupClause) lastClause).getColumns().add(part);
                }
                else if (lastClause instanceof OrderClause) {
                    ((OrderClause) lastClause).getArgs().add(part);
                }
            }

        }

        if (distinctActive) query.addClause(distinctGroup);
        buildClauses(query);
        subQueryFormat(query);
        return query;
    }

    private void subQueryFormat(Query<Clause> q) {
        int index = -1;
        int counter = 0;
        for (Clause clause : q.getClauses()) {
            if (clause instanceof WhereClause) {
                if (((WhereClause) clause).isContainSubQuery()) index = counter;
            }
            counter++;
        }
        if (index != -1) q.getClauses().remove(q.getClauses().get(index));
    }

    private void buildClauses(SQLQuery q) {
        Query<Clause> sub = null;
        for (Clause c : q.getClauses()) {
            if (c instanceof WhereClause) {
                if (((WhereClause) c).isContainSubQuery()) {
                    sub = ((WhereClause) c).buildSubQuery();
                }
                else c.build();
            }
            else c.build();
        }
        if (sub != null) sub.getClauses().forEach(q::addClause);
    }

}
