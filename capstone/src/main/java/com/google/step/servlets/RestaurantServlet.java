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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.step.data.Restaurant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for sending/getting a restaurant to/from Datastore. */
@WebServlet("/restaurant")
public class RestaurantServlet extends HttpServlet {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Returns a single Restaurant based on restaurantKey.
   * @param response All details for the requested restaurant, in the following json format:
    {
      "restaurant" : {
        "restaurantKey": <long>,
        "name": <String>,
        "location": <GeoPt>,
        "story": <String>,
        "cuisine": <List<String>>,
        "phone": <String>,
        "website": <String>,
        "status": <String>
      }
    }
   * @param request Specifies restaurant key, in the following format:
     /restaurant?restaurantKey=<int>
   * @throws SC_NOT_FOUND if no restaurant with the requested key is found in Datastore.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Long restaurantKeyParam = Long.parseLong(request.getParameter("restaurantKey"));
    Query query = new Query("RestaurantInfo")
                      .setFilter(new Query.FilterPredicate(
                          "restaurantKey", Query.FilterOperator.EQUAL, restaurantKeyParam));
    PreparedQuery results = datastore.prepare(query);
    Entity resultEntity = results.asSingleEntity();
    if (resultEntity == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // Restaurant object to hold all info
    Restaurant restaurant = Restaurant.fromEntity(resultEntity);

    // Format restaurant List to JSON for return
    Gson gson = new Gson();
    String json = gson.toJson(restaurant);

    // Send the JSON as the response
    response.setContentType("application/json;");

    JsonObject ret = new JsonObject();
    ret.addProperty("restaurant", json);
    response.getWriter().println(ret);
  }
}
