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
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElasticsearchClientTest {
  private final RestaurantHeader HEADER_1 = new RestaurantHeader(
      12345L,
      "The Goog Noodle",
      new GeoPt(37.4220621f, -122.0862784f),
      Arrays.asList("pizza", "American"));


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

}