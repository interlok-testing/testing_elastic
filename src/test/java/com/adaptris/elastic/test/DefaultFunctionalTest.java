package com.adaptris.elastic.test;

import com.adaptris.testing.DockerComposeFunctionalTest;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.File;
import java.net.InetSocketAddress;
import java.time.Duration;

import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.*;

public class DefaultFunctionalTest extends DockerComposeFunctionalTest {
    protected static String INTERLOK_SERVICE_NAME = "interlok-1";
    protected static String ELASTICSEARCH_SERVICE_NAME = "elasticsearch-1";
    protected static int INTERLOK_PORT = 8081;
    protected static int ELASTICSEARCH_PORT = 9200;
    protected static WaitStrategy defaultWaitStrategy = Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30));

    protected ComposeContainer setupContainers() {
        return new ComposeContainer(new File("docker-compose.yaml"))
                .withExposedService(INTERLOK_SERVICE_NAME, INTERLOK_PORT, defaultWaitStrategy)
                .withExposedService(ELASTICSEARCH_SERVICE_NAME, ELASTICSEARCH_PORT, defaultWaitStrategy);
    }

    protected String getElasticSearchEndpoint(String path) {
        InetSocketAddress address = getHostAddressForService(ELASTICSEARCH_SERVICE_NAME, ELASTICSEARCH_PORT);
        if (!path.startsWith("/")) path = "/" + path;
        return "http://" + address.getHostString() + ":" + address.getPort() + path;
    }

    protected String getInterlokApiEndpoint(String path) {
        InetSocketAddress address = getHostAddressForService(INTERLOK_SERVICE_NAME, INTERLOK_PORT);
        if (!path.startsWith("/")) path = "/" + path;
        return "http://" + address.getHostString() + ":" + address.getPort() + path;}



    @Test
    public void test_startup() throws Exception{
        // index json document
        when().get(getInterlokApiEndpoint("/elastic/simple/json?message=test"))
                .then().statusCode(200);
        Thread.sleep(1000);
        var jsonResponse = when().get(getElasticSearchEndpoint("/simple-json/_search"));
        jsonResponse.then().statusCode(200).body("hits.total.value", equalTo(1));

        String jsonDocId = jsonResponse.body().path("hits.hits[0]._id");

        // index csv document
        when().get(getInterlokApiEndpoint("/elastic/simple/csv?message=test"))
                .then().statusCode(200);
        Thread.sleep(1000);
        when().get(getElasticSearchEndpoint("/simple-csv/_search"))
                .then().statusCode(200).body("hits.total.value", equalTo(1));

        // index csv document with geo point
        final float lat = 33.87f;
        final float lon = 151.21f;
        when().get(getInterlokApiEndpoint(String.format("/elastic/simple/csv-geo?lat=%f&lon=%f", lat, lon)))
                .then().statusCode(200);
        Thread.sleep(1000);
        when().get(getElasticSearchEndpoint("/simple-csv-geo/_search"))
                .then().statusCode(200).body("hits.total.value", equalTo(1))
                .and().body("hits.hits[0]._source.geopoint.lat", equalTo(lat))
                .and().body("hits.hits[0]._source.geopoint.lon", equalTo(lon));

        // index bulk csv
        when().get(getInterlokApiEndpoint("/elastic/simple/csv-bulk"))
                .then().statusCode(200);
        Thread.sleep(1000);
        when().get(getElasticSearchEndpoint("/simple-csv/_search"))
                .then().statusCode(200).body("hits.total.value", equalTo(1 + 5));

        // delete document
        when().delete(getInterlokApiEndpoint("/elastic/sdk?index=simple-csv-geo&id=csvgeo-1"))
                .then().statusCode(200);
        Thread.sleep(1000);
        when().get(getElasticSearchEndpoint("/simple-csv-geo/_search"))
                .then().statusCode(200).body("hits.total.value", equalTo(0));

        // update document
        final String newMessage = "this is a new message";
        given().when().put(getInterlokApiEndpoint(String.format("/elastic/sdk?index=simple-json&message=%s&id=%s", newMessage, jsonDocId)))
                .then().statusCode(200);
        Thread.sleep(1000);
        when().get(getElasticSearchEndpoint("/simple-json/_search"))
                .then().statusCode(200).body("hits.total.value", equalTo(1))
                .body("hits.hits[0]._source.Message", equalTo(newMessage));

    }


}
