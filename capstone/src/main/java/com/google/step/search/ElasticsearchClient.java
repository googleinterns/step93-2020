// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.step.search;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.step.data.RestaurantHeader;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * The {@code ElasticsearchClient} is a class that sends requests to add {@code RestaurantHeaders}
 * to the "restaurants" index of Elasticsearch.
 */
public class ElasticsearchClient {
  private static final String RESTAURANTS = "restaurants";

  private final String elasticsearchUriString;
  private final HttpRequestFactory requestFactory;
  private final Gson gson = new Gson();

  ElasticsearchClient(HttpTransport transport, String hostname, short port) {
    requestFactory = transport.createRequestFactory();

    URIBuilder uriBuilder = new URIBuilder()
        .setScheme("http")
        .setHost(hostname)
        .setPort(port);
    elasticsearchUriString = uriBuilder.toString();
  }

  public ElasticsearchClient(String hostname, short port) {
    this(new NetHttpTransport(), hostname, port);
  }

  /**
   * Given a {@code RestaurantHeader}, sends HTTP request to Elasticsearch server to add a document
   * to the "restaurants" index representing a {@code restaurantHeader}.
   * @param restaurantHeader a {@code RestaurantHeader}
   * @throws IOException when buildPutRequest() or execute() fails
   */
  public void updateRestaurantHeader(RestaurantHeader restaurantHeader) throws IOException {
    String restaurantKey = String.valueOf(restaurantHeader.getRestaurantKey());

    GenericUrl requestUrl = new GenericUrl(elasticsearchUriString);
    requestUrl.setPathParts(Arrays.asList("", RESTAURANTS, "_doc", restaurantKey));

    String requestBody = gson.toJson(restaurantHeader);
    HttpContent putRequestContent = new ByteArrayContent(Json.MEDIA_TYPE,
        requestBody.getBytes(StandardCharsets.UTF_8));

    requestFactory.buildPutRequest(requestUrl, putRequestContent).execute();
  }
}