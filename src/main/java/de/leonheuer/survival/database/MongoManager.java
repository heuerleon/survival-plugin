package de.leonheuer.survival.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.leonheuer.survival.models.Alliance;
import de.leonheuer.survival.models.User;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoManager {

    private final MongoClient client;
    private final MongoDatabase db;
    private final MongoCollection<User> users;
    private final MongoCollection<Alliance> alliances;

    public MongoManager() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(pojoCodecProvider)
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .codecRegistry(pojoCodecRegistry)
                .build();
        client = MongoClients.create(settings);
        db = client.getDatabase("survival");
        users = db.getCollection("users", User.class);
        alliances = db.getCollection("alliances", Alliance.class);
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDb() {
        return db;
    }

    public MongoCollection<User> getUsers() {
        return users;
    }

    public MongoCollection<Alliance> getAlliances() {
        return alliances;
    }

    public void disconnect() {
        client.close();
    }

}
