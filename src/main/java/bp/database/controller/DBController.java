package bp.database.controller;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import static bp.constants.Constants.MONGO_URI;

public class DBController {

    public DBController() {}

    public MongoClient getConnection(){
        if (MONGO_URI.isEmpty()) return null;

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(MONGO_URI))
                .serverApi(serverApi)
                .build();

        return MongoClients.create(settings);

    }

    public void closeConnection(MongoClient client) {
        client.close();
    }

}
