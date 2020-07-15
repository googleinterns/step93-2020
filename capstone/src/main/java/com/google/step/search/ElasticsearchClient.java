package com.google.step.search;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.step.data.RestaurantHeader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ElasticsearchClient {
  private static final String elasticsearchHostname = "localhost";
  private static final short port = 9200;
  private final String elasticsearchUriString = "http://localhost:" + port;
  private HttpTransport transport;
  private HttpRequestFactory requestFactory;
  private final String RESTAURANTS = "restaurants";
  String uri = "http://10.128.0.2:9200";

  Gson gson = new Gson();

  ElasticsearchClient(HttpTransport transport) {
    this.transport = transport;
    requestFactory = this.transport.createRequestFactory();
  }

  public ElasticsearchClient() {
    this(new NetHttpTransport());
  }

  public HttpResponse updateRestaurantHeader(RestaurantHeader restaurantHeader) throws IOException {
    GenericUrl requestUrl = new GenericUrl(elasticsearchUriString);
    requestUrl.setPathParts(Arrays.asList("", RESTAURANTS, "_doc", String.valueOf(restaurantHeader.getRestaurantKey())));
    HttpContent putRequestContent = new ByteArrayContent(Json.MEDIA_TYPE,
        gson.toJson(restaurantHeader).getBytes(StandardCharsets.UTF_8));
    HttpRequest request = requestFactory.buildPutRequest(requestUrl, putRequestContent);

    return request.execute();
  }

  public static String getElasticsearchHostname() {
    return elasticsearchHostname;
  }

  public static short getElasticsearchPort() {
    return port;
  }
}
