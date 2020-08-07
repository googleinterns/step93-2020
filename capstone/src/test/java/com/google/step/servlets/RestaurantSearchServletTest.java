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

package com.google.step.servlets;

import com.google.api.client.json.Json;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.step.data.Restaurant;
import com.google.step.data.RestaurantHeader;
import com.google.step.search.RestaurantHeaderSearchClient;
import com.google.step.search.RestaurantQueryParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RestaurantSearchServletTest {
  private final RestaurantHeader HEADER_1 =
      new RestaurantHeader(
          1111L,
          "Restaurant 1",
          new GeoPt(11, 11),
          Arrays.asList("soup"));

  private final RestaurantHeader HEADER_2 =
      new RestaurantHeader(
          2222L,
          "Restaurant 2",
          new GeoPt(22, 22),
          Arrays.asList("barbecue", "burgers"));

  private final RestaurantHeader HEADER_3 =
      new RestaurantHeader(
          3333L,
          "Restaurant 3",
          new GeoPt(33, 33),
          Arrays.asList("noodle", "Thai"));

  private final RestaurantHeader HEADER_4 =
      new RestaurantHeader(
          4444L,
          "Restaurant 4",
          new GeoPt(44, 44),
          Arrays.asList("burgers", "chicken", "sandwich"));

  private final Gson gson = new Gson();

  @Test
  public void testDoGet() throws IOException {
    List<RestaurantHeader> allHeaders = Arrays.asList(
        HEADER_1,
        HEADER_2,
        HEADER_3,
        HEADER_4
    );
    RestaurantHeaderSearchClient mockSearchClient = mock(RestaurantHeaderSearchClient.class);
    when(mockSearchClient.searchRestaurants(new RestaurantQueryParams.Builder().build())).thenReturn(allHeaders);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    RestaurantSearchServlet restaurantSearchServlet = new RestaurantSearchServlet(mockSearchClient);
    restaurantSearchServlet.doGet(request, response);
    assertEquals(response.getContentType(), Json.MEDIA_TYPE);

    JSONObject responseBody = new JSONObject(response.getContentAsString());
    JSONArray responseHeadersJson = responseBody.getJSONArray("restaurants");

    List<RestaurantHeader> returnedHeaders = getRestaurantHeaders(responseHeadersJson);

    assertEquals(allHeaders, returnedHeaders);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testDoGetWithQueryParam() throws IOException {
    List<RestaurantHeader> burgerRestaurants = Arrays.asList(HEADER_2, HEADER_4);

    RestaurantHeaderSearchClient mockSearchClient = mock(RestaurantHeaderSearchClient.class);
    RestaurantQueryParams params = new RestaurantQueryParams.Builder().query("burgers").build();
    when(mockSearchClient.searchRestaurants(params)).thenReturn(burgerRestaurants);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.addParameter("query", "burgers");

    RestaurantSearchServlet restaurantSearchServlet = new RestaurantSearchServlet(mockSearchClient);
    restaurantSearchServlet.doGet(request, response);
    assertEquals(response.getContentType(), Json.MEDIA_TYPE);

    JSONObject responseBody = new JSONObject(response.getContentAsString());
    JSONArray responseHeadersJson = responseBody.getJSONArray("restaurants");

    List<RestaurantHeader> returnedHeaders = getRestaurantHeaders(responseHeadersJson);

    assertEquals(burgerRestaurants, returnedHeaders);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testDoGetUntrimmedQueryString() throws IOException {
    RestaurantHeaderSearchClient mockSearchClient = mock(RestaurantHeaderSearchClient.class);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.addParameter("query", "     \n   burgers  \n\t");

    RestaurantSearchServlet restaurantSearchServlet = new RestaurantSearchServlet(mockSearchClient);
    restaurantSearchServlet.doGet(request, response);

    RestaurantQueryParams params = new RestaurantQueryParams.Builder().query("burgers").build();
    verify(mockSearchClient).searchRestaurants(params);
  }

  List<RestaurantHeader> getRestaurantHeaders(JSONArray headersJson) {
    List<RestaurantHeader> headers = new ArrayList<>();

    JSONObject headerJson;
    for(int i = 0; i < headersJson.length(); i++) {
      headerJson = headersJson.getJSONObject(i);
      headers.add(gson.fromJson(headerJson.toString(), RestaurantHeader.class));
    }

    return headers;
  }
}
