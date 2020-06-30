// Copyright 2019 Google LLC
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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.sps.data.Restaurant;
import com.google.appengine.api.datastore.GeoPt;
import java.lang.Number;
// import com.google.sps.data.Status;
import java.io.IOException;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for getting comments from Datastore. */
@WebServlet("/restaurant")
public class RestaurantServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("RestaurantInfo").addSort("score", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Restaurant> restaurants = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String name = (String) entity.getProperty("name");
      GeoPt location = (GeoPt) entity.getProperty("location");
      String story = (String) entity.getProperty("story");
      List<String> cuisine = (List<String>) entity.getProperty("cuisine");
      String phone = (String) entity.getProperty("phone");
      String website = (String) entity.getProperty("website");
      int status = ((Number)entity.getProperty("status")).intValue();
      double score = (double) entity.getProperty("score");

      // Comment object to hold all info
      Restaurant restaurant = new Restaurant(name, location, story, cuisine, phone, website, status, score);
      restaurants.add(restaurant);
    }

    // Format comments List to JSON for return
    Gson gson = new Gson();
    String json = gson.toJson(restaurants);

    // Send the JSON as the response
    response.setContentType("application/json;");

    // Build JsonObject with comments list and nextPageCursor
    JsonObject ret = new JsonObject();
    ret.addProperty("restaurants", json);

    response.getWriter().println(ret);
  }
}
