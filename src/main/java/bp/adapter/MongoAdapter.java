package bp.adapter;

import bp.adapter.mapper.Mapper;
import bp.adapter.mapper.MapperImplementation;
import bp.parser.Query;
import bp.parser.SQLQuery;
import bp.parser.clauses.Clause;

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
