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
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ElasticsearchClient {
  private static final String elasticsearchHostname = "10.128.0.2";
  private static final short elasticsearchPort = 9200;
  private static final String elasticsearchUriString =  elasticsearchHostname + elasticsearchPort;

  private static final String RESTAURANTS = "restaurants";

  private final HttpRequestFactory requestFactory;

  Gson gson = new Gson();

  ElasticsearchClient(HttpTransport transport) {
    requestFactory = transport.createRequestFactory();
  }

  public ElasticsearchClient() {
    this(new NetHttpTransport());
  }

  public int updateRestaurantHeader(RestaurantHeader restaurantHeader) {
    String restaurantKey = String.valueOf(restaurantHeader.getRestaurantKey());

    GenericUrl requestUrl = new GenericUrl(elasticsearchUriString);
    requestUrl.setPathParts(Arrays.asList("", RESTAURANTS, "_doc", restaurantKey));

    String requestBody = gson.toJson(restaurantHeader);
    HttpContent putRequestContent = new ByteArrayContent(Json.MEDIA_TYPE,
        requestBody.getBytes(StandardCharsets.UTF_8));

    int statusCode;
    try {
      HttpRequest request = requestFactory.buildPutRequest(requestUrl, putRequestContent);
      HttpResponse response = request.execute();

      statusCode = response.getStatusCode();
    } catch (IOException e) {
      statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
    }

    return statusCode;
  }

  public static String getElasticsearchHostname() {
    return elasticsearchHostname;
  }

  public static short getElasticsearchPort() {
    return elasticsearchPort;
  }
}
