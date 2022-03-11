package com.countrycounter.repository;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

@Component
public class CounterKafkaReader {
    private static final Map<String, AtomicLong> tempStorage = new HashMap<>();
    private static final String COUNTRY_FIELD_NAME = "country";
    private static final String VALUE_FIELD_NAME = "value";
    @Value(value = "${mongodb.connection}")
    private String mongoDBConnectionString;
    @Value(value = "${mongodb.database}")
    private String mongoDBDatabaseName;
    @Value(value = "${mongodb.collection}")
    private String mongoDBCollectionName;

    @PostConstruct
    public void scheduledPersistOperation() {
        Timer timer = new Timer();
        TimerTask persistTask = new TimerTask() {
            @Override
            public void run() {
                persistStorage();
            }
        };
        LocalDateTime localDateTime1 = LocalDateTime.now();
        Date date = Date.from(localDateTime1.atZone(ZoneId.systemDefault()).toInstant());
        timer.schedule(persistTask, date, 1000);
    }

    @KafkaListener(topics = "countryCounter", groupId = "main")
    public void listenMessages(String message) {
        synchronized (tempStorage) {
            AtomicLong value = tempStorage.get(message);
            if (value != null) {
                value.incrementAndGet();
            } else {
                tempStorage.put(message, new AtomicLong(1));
            }
        }
    }

    private void persistStorage() {
        MongoClient mongoClient = MongoClients.create(new ConnectionString(mongoDBConnectionString));
        MongoDatabase database = mongoClient.getDatabase(mongoDBDatabaseName);
        MongoCollection<Document> collection = database.getCollection(mongoDBCollectionName);
        synchronized (tempStorage) {
            for (Map.Entry<String, AtomicLong> entry : tempStorage.entrySet()) {
                UpdateResult updateResult = collection.updateOne(eq(COUNTRY_FIELD_NAME, entry.getKey()), inc(VALUE_FIELD_NAME, entry.getValue()));
                if (updateResult.getModifiedCount() == 0) {
                    Document doc = new Document(COUNTRY_FIELD_NAME, entry.getKey())
                            .append(VALUE_FIELD_NAME, entry.getValue());
                    try {
                        collection.insertOne(doc);
                    } catch (Exception e) {
                        collection.updateOne(eq(COUNTRY_FIELD_NAME, entry.getKey()), inc(VALUE_FIELD_NAME, entry.getValue()));
                    }
                }
            }
            tempStorage.clear();
        }
        mongoClient.close();
    }

}
