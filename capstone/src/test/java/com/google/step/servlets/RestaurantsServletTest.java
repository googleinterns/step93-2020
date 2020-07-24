package com.google.step.servlets;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.step.data.Restaurant;
import com.google.step.data.RestaurantHeader;
import com.google.step.search.RestaurantHeaderSearchClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class RestaurantsServletTest {
  List<RestaurantHeader> allHeaders = Arrays.asList(
      new RestaurantHeader(2345L, "", new GeoPt(22, 34), Arrays.asList("crackers"))
  );

  Gson gson = new Gson();

  @Test
  public void testDoGet() throws IOException {
    RestaurantHeaderSearchClient mockSearchClient = Mockito.mock(RestaurantHeaderSearchClient.class);

    when(mockSearchClient.getRandomRestaurants()).thenReturn(allHeaders);

    RestaurantsServlet restaurantsServlet = new RestaurantsServlet(mockSearchClient);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    restaurantsServlet.doGet(request, response);

    JSONObject responseBody = new JSONObject(response.getContentAsString());
    JSONArray responseHeadersJson = responseBody.getJSONArray("restaurants");

    // Convert response into a list of restaurant headers
    List<RestaurantHeader> headers = new ArrayList<>();
    JSONObject headerJson;
    for(int i = 0; i < responseHeadersJson.length(); i++) {
      headerJson = responseHeadersJson.getJSONObject(i);
      headers.add(gson.fromJson(headerJson.toString(), RestaurantHeader.class));
    }

    assertEquals(allHeaders, headers);
    assertEquals(200, response.getStatus());
  }
}
