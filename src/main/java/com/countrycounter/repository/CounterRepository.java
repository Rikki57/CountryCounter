package com.countrycounter.repository;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CounterRepository implements CounterOutput {
    @Value(value = "${mongodb.connection}")
    private String mongoDBConnectionString;

    @Override
    public Map<String, Long> getValues() {
        Map<String, Long> result = new HashMap<>();
        Block<Document> getDataBlock = document -> result.put(document.get("country", String.class), document.get("value", Long.class));
        MongoClient mongoClient = MongoClients.create(new ConnectionString(mongoDBConnectionString));
        MongoDatabase database = mongoClient.getDatabase("CountryCounter");
        MongoCollection<Document> collection = database.getCollection("counters");
        collection.find().forEach(getDataBlock);
        mongoClient.close();
        return result;
    }
}
