package main.java.bp.adapter;

import main.java.bp.adapter.mapper.Mapper;
import main.java.bp.adapter.mapper.MapperImplementation;
import main.java.bp.parser.Query;
import main.java.bp.parser.SQLQuery;
import main.java.bp.parser.clauses.Clause;

public class MongoAdapter implements Adapter {

    private Query<Clause> query;
    private final ParameterConverter parameterConverter;
    private final Mapper mapper;

    public MongoAdapter() {
        this.parameterConverter = new ParameterConverter();
        this.mapper = new MapperImplementation();
    }

    private MongoQuery convertToMongo(Query<Clause> q) {
        MongoQuery mongoQuery = parameterConverter.extractParameters((SQLQuery)q);
        mapper.buildJsonFiles(mongoQuery);
        return mongoQuery;
    }

    public void setQuery(Query<Clause> query) {
        this.query = query;
    }

    @Override
    public Object getQuery() {
        return convertToMongo(query);
    }

}
