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

package com.google.step.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.step.data.Restaurant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for sending/getting restaurants to/from Datastore. */
@WebServlet("/page-view")
public class PageViewServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int restaurantKey = Integer.parseInt(request.getParameter("restaurantKey"));

    // Begin a transaction in case multiple people are clicking at once
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Transaction transaction = datastore.beginTransaction();
    try {
      // getInstance() returns a Calendar object whose fields have been initialized
      // with the current date and time
      Calendar calendar = Calendar.getInstance();
      int year = calendar.get(Calendar.YEAR);
      int week = calendar.get(Calendar.WEEK_OF_YEAR);

      // Create a query filter for the restaurant on this year and week
      CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND,
          Arrays.asList(
              new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, restaurantKey),
              new FilterPredicate("year", Query.FilterOperator.EQUAL, year),
              new FilterPredicate("week", Query.FilterOperator.EQUAL, week)));

      Query query = new Query("PageViews").setFilter(filter);
      List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
      if (results.size() > 0) {
        // Already had view(s) this week: update the existing entity
        Entity existingViews = results.get(0);
        int currViews = ((Long) existingViews.getProperty("count")).intValue();
        existingViews.setProperty("count", currViews + 1);
        datastore.put(transaction, existingViews);
      } else {
        // No views yet this week: create a new entity
        Entity views = new Entity("PageViews");
        views.setProperty("restaurantKey", restaurantKey);
        views.setProperty("year", year);
        views.setProperty("week", week);
        views.setProperty("count", 1);
        datastore.put(transaction, views);
      }
      transaction.commit();
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
    }
  }
}
