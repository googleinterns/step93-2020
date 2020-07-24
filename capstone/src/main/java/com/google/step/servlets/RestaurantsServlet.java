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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.step.clients.RestaurantClient;
import com.google.step.data.Restaurant;
import com.google.step.data.RestaurantHeader;
import com.google.step.search.ElasticsearchClient;
import com.google.step.search.RestaurantHeaderSearchClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for getting restaurants from Datastore. */
@WebServlet("/restaurants")
public class RestaurantsServlet extends HttpServlet {
  private final RestaurantHeaderSearchClient searchClient;

  public RestaurantsServlet() {
    this(new ElasticsearchClient("localhost", (short)9200));
  }

  RestaurantsServlet(RestaurantHeaderSearchClient searchClient) {
    this.searchClient = searchClient;
  }

  /**
   * Returns a list of RestaurantHeaders, with a snapshot of restaurant details.
   * @param response A list of restaurant details, in the following json format:
    {
      "restaurantHeaders" : [
        {
          "name": <String>,
          "cuisine": <List<String>>,
          "message": <String, details status of restaurant based on score>,
        },
        ...
      ]
    }
   * @param request Specifies relevant search params. //TODO: update exact search param format
   * @throws //TODO: add any error codes thrown once search is implemented.
   */
  // TODO: Change this to be connected to search functionality.
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    List<RestaurantHeader> restaurantHeaders = searchClient.getRandomRestaurants();

    // Format restaurant List to JSON for return
    JSONObject responseJson = new JSONObject()
        .put("restaurants", new JSONArray(restaurantHeaders));

    // Send the JSON as the response
    response.setContentType(Json.MEDIA_TYPE);

    response.getWriter().println(responseJson);
  }
}
