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
 * {@link RestaurantHeaderSearchClient} backed by Elasticsearch.
 */
public class ElasticsearchClient implements RestaurantHeaderSearchClient {
  private static final String RESTAURANTS = "restaurants";

  private final String elasticsearchUriString;
  private final HttpRequestFactory requestFactory;
  private final Gson gson = new Gson();

  ElasticsearchClient(HttpTransport transport, String hostname, short port) {
    requestFactory = transport.createRequestFactory();

    URIBuilder uriBuilder = new URIBuilder().setScheme("http").setHost(hostname).setPort(port);
    elasticsearchUriString = uriBuilder.toString();
  }

  public ElasticsearchClient(String hostname, short port) {
    this(new NetHttpTransport(), hostname, port);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateRestaurantHeader(RestaurantHeader restaurantHeader) throws IOException {
    String restaurantKey = String.valueOf(restaurantHeader.getRestaurantKey());
    List<String> requestPath = Arrays.asList("", RESTAURANTS, "_doc", restaurantKey);

    String requestBody = gson.toJson(restaurantHeader);

    HttpRequest request = buildElasticsearchHttpRequest("PUT", requestPath, requestBody);
    request.execute();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<RestaurantHeader> searchRestaurants(String query) throws IOException {
    List<String> requestPath = Arrays.asList("", RESTAURANTS, "_search");

    JSONObject queryRequest = query.isEmpty() ? createMatchAllQuery() : createBasicSearchQuery(query);
    String requestBody = addBoostingToQuery(queryRequest).toString();

    HttpRequest request = buildElasticsearchHttpRequest("POST", requestPath, requestBody);
    HttpResponse response = request.execute();

    return convertElasticsearchResponseBodyToHeaders(response);
  }

  private JSONObject addBoostingToQuery(JSONObject queryJson) {
    return new JSONObject()
        .put("query", new  JSONObject()
            .put("function_score", queryJson
                .put("field_value_factor", new JSONObject()
                    .put("field", "metricsScore")
                    .put("factor", 2))));
  }

  private JSONObject createBasicSearchQuery(String query) {
    List<String> requestPath = Arrays.asList("", RESTAURANTS, "_search");

    return new JSONObject()
        .put("query", new JSONObject()
            .put("multi_match", new JSONObject()
                .put("query", query)
                .put("fields", new JSONArray(Arrays.asList("name", "cuisine")))));
  }

  private JSONObject createMatchAllQuery() {
    return  new JSONObject()
        .put("query", new JSONObject()
            .put("match_all", new JSONObject()));
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

    // Elasticsearch response body stores match information in { hits: { hits : [] } }
    JSONArray elasticsearchMatches = responseJson.getJSONObject("hits").getJSONArray("hits");

    List<RestaurantHeader> headers = new ArrayList<>();
    for (int i = 0; i < elasticsearchMatches.length(); i ++) {
      JSONObject matchJson = elasticsearchMatches.getJSONObject(i);
      JSONObject source = matchJson.getJSONObject("_source");
      RestaurantHeader header = gson.fromJson(source.toString(), RestaurantHeader.class);
      headers.add(header);
    }

    return headers;
  }
}
