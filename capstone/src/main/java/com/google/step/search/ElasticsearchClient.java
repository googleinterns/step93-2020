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
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.step.data.RestaurantHeader;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code ElasticsearchClient} is a class that sends requests to add {@code RestaurantHeaders}
 * to the "restaurants" index of Elasticsearch and queries the "restaurants" index for relevant
 * restaurant headers.
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
   * Given a {@link RestaurantHeader}, sends HTTP request to the search server to add a document
   * that represents a {@link RestaurantHeader} to the "restaurants" index.
   * @param restaurantHeader a {@link RestaurantHeader} that will be updated in the search index
   * @throws IOException when buildPutRequest() or execute() fails
   */
  public void updateRestaurantHeader(RestaurantHeader restaurantHeader) throws IOException {
    String restaurantKey = String.valueOf(restaurantHeader.getRestaurantKey());
    List<String> requestPath = Arrays.asList("", RESTAURANTS, "_doc", restaurantKey);

    String requestBody = gson.toJson(restaurantHeader);

    HttpRequest request = buildElasticsearchHttpRequest("PUT", requestPath, requestBody);
    request.execute();
  }

  /**
   * Queries search server for {@link RestaurantHeader} objects that match {@code query} by either
   * "name" or "cuisine" field, if the query is not empty. If the query is empty, returns a random
   * assortment of restaurant headers.
   * @param query valid query string, or empty string
   * @return list of {@link RestaurantHeader} objects sorted by descending relevance score
   * @throws IOException thrown if request cannot be made or executed properly
   */
  public List<RestaurantHeader> searchRestaurants(String query) throws IOException {
    return query.isEmpty() ? getRandomRestaurants() : queryRestaurantHeaders(query);
  }

  /**
   * Queries search server for {@link RestaurantHeader} fields that match {@code query} by either
   * "name" or "cuisine" field.
   * @param query valid query string
   * @return list of {@link RestaurantHeader} objects sorted by descending relevance score
   * @throws IOException if request cannot be made or executed properly
   */
  private List<RestaurantHeader> queryRestaurantHeaders(String query) throws IOException {
    List<String> requestPath = Arrays.asList("", RESTAURANTS, "_search");

    String requestBody = new JSONObject()
        .put("query", new JSONObject()
            .put("multi_match", new JSONObject()
                .put("query", query)
                .put("fields", new JSONArray(Arrays.asList("name", "cuisine")))))
        .toString();

    HttpRequest request = buildElasticsearchHttpRequest("POST", requestPath, requestBody);
    HttpResponse response = request.execute();

    return convertElasticsearchResponseBodyToHeaders(response);
  }

  /**
   * Queries search server, matching everything possible.
   * @return list of {@link RestaurantHeader} objects sorted arbitrarily
   * @throws IOException if request cannot be made or executed properly
   */
  private List<RestaurantHeader> getRandomRestaurants() throws IOException {
    List<String> requestPath = Arrays.asList("", "_search");

    String requestBody = new JSONObject()
        .put("query", new JSONObject()
            .put("match_all", new JSONObject()))
        .toString();

    HttpRequest request = buildElasticsearchHttpRequest("POST", requestPath, requestBody);
    HttpResponse response = request.execute();

    return convertElasticsearchResponseBodyToHeaders(response);
  }

  /**
   * Given a request method, a URL path, and a request body, builds an HTTP request for the
   * Elasticsearch server.
   * @param requestMethod a string representing an HTTP request method
   * @param urlPath relative path of HTTP request to search server
   * @param requestBody string representing the HTTP request body
   * @return  {@link HttpRequest} object that will connect to the Elasticsearch server
   * @throws IOException thrown if HTTP request cannot be made properly
   */
  private HttpRequest buildElasticsearchHttpRequest(String requestMethod, List<String> urlPath, String requestBody) throws IOException {
    GenericUrl requestUrl = new GenericUrl(elasticsearchUriString);
    requestUrl.setPathParts(urlPath);

    HttpContent requestContent = new ByteArrayContent(Json.MEDIA_TYPE,
        requestBody.getBytes(Charsets.UTF_8));

    return requestFactory.buildRequest(requestMethod, requestUrl, requestContent);
  }

  /**
   * Given an HTTP response from the Elasticsearch server, creates a list of RestaurantHeaders.
   * <p>
   * This is done by extracting JSON objects representing restaurant headers from the Elasticsearch
   * response and converting them into actual {@code RestaurantHeader} objects
   * @param response  an HTTP response coming from an Elasticsearch "search" query
   * @return  a list of {@code RestaurantHeader} objects
   * @throws IOException thrown when {@code response.getContent()} fails
   */
  private List<RestaurantHeader> convertElasticsearchResponseBodyToHeaders(HttpResponse response) throws IOException {
    String responseString = CharStreams.toString(new InputStreamReader(response.getContent()));

    JSONObject responseJson = new JSONObject(responseString);

    List<RestaurantHeader> headers = new ArrayList<>();
    JSONArray elasticsearchMatches = responseJson.getJSONObject("hits").getJSONArray("hits");

    for (int i = 0; i < elasticsearchMatches.length(); i ++) {
      JSONObject matchJson = elasticsearchMatches.getJSONObject(i);
      JSONObject source = matchJson.getJSONObject("_source");
      RestaurantHeader header = gson.fromJson(source.toString(), RestaurantHeader.class);
      headers.add(header);
    }

    return headers;
  }
}
