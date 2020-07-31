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

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.appengine.api.datastore.GeoPt;
import com.google.step.data.RestaurantHeader;
import com.google.step.data.RestaurantScore;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ElasticsearchClientTest {
  private final String elasticsearchHostname = "10.128.0.2";
  private final short elasticsearchPort = 9200;

  private final RestaurantHeader HEADER_1 = new RestaurantHeader(
      12345L,
      "The Goog Noodle",
      new GeoPt(37.4220621f, -122.0862784f),
      Arrays.asList("pizza", "American"),
      Optional.of(0.2));

  private final RestaurantHeader HEADER_2 = new RestaurantHeader(
      1111L,
      "The Statue",
      new GeoPt(40.6892494f,-74.0445004f),
      Collections.singletonList("Indian"),
      Optional.of(0.7));


  @Test
  public void testAddRestaurantDocumentToSearchIndex() throws IOException {
    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        assertTrue(method.equalsIgnoreCase("PUT"));

        URL urlFromString = new URL(url);
        assertEquals(elasticsearchHostname, urlFromString.getHost());
        assertEquals(elasticsearchPort, urlFromString.getPort());
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

    ElasticsearchClient testClient =
        new ElasticsearchClient(transport, elasticsearchHostname, elasticsearchPort);
    testClient.updateRestaurantHeader(HEADER_1);
  }

  @Test
  public void testQueryRestaurants() throws IOException {
    MockHttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        assertTrue(method.equalsIgnoreCase("POST"));

        URL urlFromString = new URL(url);
        assertEquals(elasticsearchHostname, urlFromString.getHost());
        assertEquals(elasticsearchPort, urlFromString.getPort());
        assertEquals("/restaurants/_search", urlFromString.getPath());

        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {

            String expectedQueryBody = new JSONObject()
                .put("query", new  JSONObject()
                    .put("function_score", new JSONObject()
                        .put("query", new JSONObject()
                            .put("multi_match", new JSONObject()
                                .put("query", "goog")
                                .put("fields", new JSONArray(Arrays.asList("name", "cuisine")))))
                        .put("field_value_factor", new JSONObject()
                            .put("field", "metricsScore")
                            .put("factor", 2))))
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

    MockHttpTransport transportSpy = spy(transport);
    ElasticsearchClient esClient =
        new ElasticsearchClient(transportSpy, elasticsearchHostname, elasticsearchPort);

    String expectedUrl =
        new URIBuilder()
            .setScheme("http")
            .setHost(elasticsearchHostname)
            .setPort(elasticsearchPort)
            .setPath("/restaurants/_search")
            .toString();

    List<RestaurantHeader> queryResult = esClient.searchRestaurants("goog");

    verify(transportSpy).buildRequest("POST", expectedUrl);
    assertEquals(Collections.singletonList(HEADER_1), queryResult);
  }

  @Test
  public void testQueryRestaurantsDiscover() throws IOException {
    MockHttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        assertTrue(method.equalsIgnoreCase("POST"));

        URL urlFromString = new URL(url);
        assertEquals(elasticsearchHostname, urlFromString.getHost());
        assertEquals(elasticsearchPort, urlFromString.getPort());
        assertEquals("/restaurants/_search", urlFromString.getPath());

        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {

            String expectedRequestBody = new JSONObject()
                .put("query", new  JSONObject()
                    .put("function_score", new JSONObject()
                        .put("query", new JSONObject()
                            .put("match_all", new JSONObject()))
                        .put("field_value_factor", new JSONObject()
                            .put("field", "metricsScore")
                            .put("factor", 2)))).toString();

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

    MockHttpTransport transportSpy = spy(transport);
    ElasticsearchClient esClient =
        new ElasticsearchClient(transportSpy, elasticsearchHostname, elasticsearchPort);

    String expectedUrl =
        new URIBuilder()
            .setScheme("http")
            .setHost(elasticsearchHostname)
            .setPort(elasticsearchPort)
            .setPath("/restaurants/_search")
            .toString();

    List<RestaurantHeader> searchResults = esClient.searchRestaurants("");

    verify(transportSpy).buildRequest("POST", expectedUrl);
    assertEquals(Arrays.asList(HEADER_1, HEADER_2), searchResults);
  }

  @Test
  public void testUpdateRestaurantHeaderScores() throws IOException{
    List<RestaurantScore> scores = new LinkedList<>();
    scores.add(new RestaurantScore(1111L, 0.1));
    scores.add(new RestaurantScore(2222L, 0.2));
    scores.add(new RestaurantScore(3333L, 0.3));
    scores.add(new RestaurantScore(4444L, 0.4));

    MockHttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) {
        assertTrue(method.equalsIgnoreCase("POST"));

        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {

            String expectedRequestBody =
                new JSONObject().put("update", new JSONObject().put("_id", 1111L)) + "\n" +
                new JSONObject().put("metricsScore", 0.1) + "\n" +
                new JSONObject().put("update", new JSONObject().put("_id", 2222L)) + "\n" +
                new JSONObject().put("metricsScore", 0.2) + "\n" +
                new JSONObject().put("update", new JSONObject().put("_id", 3333L)) + "\n" +
                new JSONObject().put("metricsScore", 0.3) + "\n" +
                new JSONObject().put("update", new JSONObject().put("_id", 4444L)) + "\n" +
                new JSONObject().put("metricsScore", 0.4) + "\n";

            String queryRequestBody = getContentAsString();

            assertEquals(expectedRequestBody.trim(), queryRequestBody.trim());

            return new MockLowLevelHttpResponse();
          }
        };
      }
    };

    MockHttpTransport transportSpy = spy(transport);
    ElasticsearchClient esClient =
        new ElasticsearchClient(transportSpy, elasticsearchHostname, elasticsearchPort);

    String expectedUrl =
        new URIBuilder()
            .setScheme("http")
            .setHost(elasticsearchHostname)
            .setPort(elasticsearchPort)
            .setPath("/restaurants/_bulk")
            .toString();

    esClient.updateRestaurantScores(scores);

    verify(transportSpy).buildRequest("POST", expectedUrl);
  }
}
