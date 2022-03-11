For starting this application you need:

1. Kafka topic. It should be pointed in application.properties (Kafka section)
2. MongoDB instance. You should point connection string, database and collection name in application.properties

Application starts under spring boot (SwaggerSpringDemoApplication class). Swagger page is available
on http://localhost:8080/swagger-ui.html (by default). There you can see API specification and can run both types of
requests (post for adding items and get for getting statistics)

After application start integration tests in LoadTest.java are available.

