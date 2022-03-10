package com.countrycounter;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class TestsHelper {
    public static Double getStatisticsByCountry(String country) {
        String baseServiceUrl = "http://localhost:8080/statistics";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseServiceUrl))
                .GET() // GET is default
                .build();

        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            Map map = gson.fromJson(response.body(), Map.class);
            return (Double) map.get(country);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static void sendGetRequest(int requestsNumber) {
        String baseServiceUrl = "http://localhost:8080/statistics";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseServiceUrl))
                .GET() // GET is default
                .build();

        try {
            for (int i = 0; i < requestsNumber; i++) {
                HttpResponse<Void> response = client.send(request,
                        HttpResponse.BodyHandlers.discarding());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void clearMongo() {
        MongoClient mongoClient = MongoClients.create(new ConnectionString("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("CountryCounter");
        MongoCollection<Document> collection = database.getCollection("counters");
        collection.drop();
    }
}
