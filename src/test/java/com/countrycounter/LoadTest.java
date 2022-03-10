package com.countrycounter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoadTest {
    private static final Integer REQUESTS_PER_THREAD = 100;
    private static final Integer THREADS = 10;
    private static final Integer ITERATIONS = 30;
    private static final Integer TIMEOUT_MS = 1000;

    @Test
    @Timeout(30)
    void testPerformanceWrite() {
        TestsHelper.clearMongo();
        for (int i = 0; i < ITERATIONS; i++) {

            Runnable runnableTask = () -> {
                sendPostRequests(REQUESTS_PER_THREAD, "RU");
            };
            ExecutorService executorService =
                    new ThreadPoolExecutor(10, 100, 20L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>());
            for (int j = 0; j < THREADS; j++) {
                executorService.execute(runnableTask);
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(50000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //Wait until background processes and synchronizations will terminate
            Thread.sleep(20_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(30_000.0, TestsHelper.getStatisticsByCountry("RU"));
    }


    @Test
    void testPrecision() {
        TestsHelper.clearMongo();
        for (int i = 0; i < 2; i++) {

            Runnable runnableTask = () -> {
                sendPostRequests(2, "RU");
            };
            Runnable runnableTask2 = () -> {
                sendPostRequests(2, "GB");
            };
            ExecutorService executorService =
                    new ThreadPoolExecutor(10, 100, 20L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>());
            for (int j = 0; j < 3; j++) {
                executorService.execute(runnableTask);
            }
            for (int j = 0; j < 3; j++) {
                executorService.execute(runnableTask2);
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //Wait until background processes and synchronizations will terminate
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(12, TestsHelper.getStatisticsByCountry("RU"));
        assertEquals(12, TestsHelper.getStatisticsByCountry("RU"));
    }

    @Test
    @Timeout(30)
    void testPerformanceRead() {
        Runnable runnableTask = () -> {
            sendGetRequests(REQUESTS_PER_THREAD, "RU");
        };
        ExecutorService executorService =
                new ThreadPoolExecutor(10, 100, 20L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i < THREADS; i++) {
            executorService.execute(runnableTask);
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(50_000L, TestsHelper.getStatisticsByCountry("RU"));
    }

    void sendGetRequests(int requestsNumber, String value) {
        String baseServiceUrl = "http://localhost:8080/statistics";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseServiceUrl))
                .GET() // GET is default
                .build();

        try {
            for (int i = 0; i < requestsNumber; i++) {
                client.send(request,
                        HttpResponse.BodyHandlers.discarding());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void sendPostRequests(int requestsNumber, String value) {
        String baseServiceUrl = "http://localhost:8080/countryCounter";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseServiceUrl))
                .POST(HttpRequest.BodyPublishers.ofString(value)) // GET is default
                .build();

        try {
            for (int i = 0; i < requestsNumber; i++) {
                client.send(request,
                        HttpResponse.BodyHandlers.discarding());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
