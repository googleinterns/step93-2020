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
import com.google.step.data.RestaurantHeader;
import com.google.step.search.ElasticsearchClient;
import com.google.step.search.RestaurantHeaderSearchClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for getting restaurants from the search index */
@WebServlet("/search/restaurants")
public class RestaurantsServlet extends HttpServlet {
  private final RestaurantHeaderSearchClient searchClient;

  public RestaurantsServlet() {
    this(new ElasticsearchClient("localhost", (short)9200));
  }

  RestaurantsServlet(RestaurantHeaderSearchClient searchClient) {
    this.searchClient = searchClient;
  }

  /**
   * Query the search server for a specific subset of matching restaurants
   * @param response  a servlet response with a JSON object holding restaurant details in the
   *                 response body. The restaurant details can be found in the "restaurants" field
   * @param request servlet request with "query" parameter set to search query
   * @throws IOException thrown if writing the response fails
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String queryString = request.getParameter("query").trim();
    List<RestaurantHeader> searchResults = searchClient.searchRestaurants(queryString);

    JSONObject responseJson = new JSONObject()
        .put("restaurants", new JSONArray(searchResults));

    response.setContentType(Json.MEDIA_TYPE);
    response.getWriter().println(responseJson);
  }
}
