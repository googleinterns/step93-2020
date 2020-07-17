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

import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.step.data.RestaurantHeader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElasticsearchClientTest {
  private final RestaurantHeader HEADER_1 = new RestaurantHeader(
      12345L,
      "The Goog Noodle",
      new GeoPt(37.4220621f, -122.0862784f),
      Arrays.asList("pizza", "American"));

  private final RestaurantHeader HEADER_2 = new RestaurantHeader(
      1111L,
      "The Statue",
      new GeoPt(40.6892494f,-74.0445004f),
      Collections.singletonList("Indian"));


  @Test
  public void testAddRestaurantDocumentToSearchIndex() {
    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        assertTrue(method.equalsIgnoreCase("PUT"));

        URL urlFromString = new URL(url);
        assertEquals(urlFromString.getHost(), ElasticsearchClient.getElasticsearchHostname());
        assertEquals(urlFromString.getPort(), ElasticsearchClient.getElasticsearchPort());
        assertEquals("/restaurants/_doc/12345", urlFromString.getPath());

        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() {
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
            response.setStatusCode(200);

            return response;
          }
        };
      }
    };

    ElasticsearchClient testClient = new ElasticsearchClient(transport);
    int statusCode = testClient.updateRestaurantHeader(HEADER_1);
    assertEquals(200, statusCode);
  }

  @Test
  public void testQueryRestaurants() throws IOException {
    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        assertTrue(method.equalsIgnoreCase("POST"));

        URL urlFromString = new URL(url);
        assertEquals(urlFromString.getHost(), ElasticsearchClient.getElasticsearchHostname());
        assertEquals(urlFromString.getPort(), ElasticsearchClient.getElasticsearchPort());
        assertEquals("/restaurants/_search", urlFromString.getPath());

        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {

            String expectedQueryBody = new JSONObject()
                .put("query", new JSONObject()
                    .put("multi_match", new JSONObject()
                        .put("query", "goog")
                        .put("fields", new JSONArray(Arrays.asList("name", "cuisine")))))
                .toString();

            String queryRequestBody = getContentAsString();

            assertEquals(expectedQueryBody, queryRequestBody);

            String stringContent = new JSONObject()
                .put("hits", new JSONObject()
                    .put("hits", new JSONArray()
                        .put(new JSONObject()
                            .put("_source", new JSONObject(HEADER_1)))))
                .toString();
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
            response.setContent(stringContent);
            return response;
          }
        };
      }
    };

    ElasticsearchClient esClient = new ElasticsearchClient(transport);
    List<RestaurantHeader> queryResult = esClient.queryRestaurantHeaders("goog");
    assertEquals(Collections.singletonList(HEADER_1), queryResult);
  }

  @Test
  public void testQueryRestaurantsDiscover() throws IOException {
    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        assertTrue(method.equalsIgnoreCase("POST"));

        URL urlFromString = new URL(url);
        assertEquals(urlFromString.getHost(), ElasticsearchClient.getElasticsearchHostname());
        assertEquals(urlFromString.getPort(), ElasticsearchClient.getElasticsearchPort());
        assertEquals("/restaurants/_search", urlFromString.getPath());

        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {

            String expectedRequestBody = new JSONObject()
                .put("query", new JSONObject()
                    .put("match_all", new JSONObject()))
                .toString();

            String queryRequestBody = getContentAsString();

            assertEquals(expectedRequestBody, queryRequestBody);

            String stringContent = new JSONObject()
                .put("hits", new JSONObject()
                    .put("hits", new JSONArray()
                        .put(new JSONObject()
                            .put("_source", new JSONObject(HEADER_1)))
                        .put(new JSONObject()
                            .put("_source", new JSONObject(HEADER_2)))))
                .toString();
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
            response.setContent(stringContent);
            return response;
          }
        };
      }
    };

    ElasticsearchClient esClient = new ElasticsearchClient(transport);
    assertEquals(Arrays.asList(HEADER_1, HEADER_1), esClient.getRandomRestaurants());
  }
}
