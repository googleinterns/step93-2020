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

package com.google.step.clients;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.step.data.RestaurantPageViews;
import com.google.step.data.WeeklyPageView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This client is in charge of interacting with the metrics entities in DataStore.
 */
public class MetricsClient {
  /**
   * Empty constructor since we don't need any variables, we only use this class for the methods.
   */
  public MetricsClient() {}

  private static DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Gets the current week's page views for one restaurant by specifying a restaurantKey.
   * @param restaurantKey restaurant key of the restaurant we want to get views.
   * @return returns an instance of a WeeklyPageView. If the entity doesn't exist yet,
   *         it returns an instance of WeeklyPageView with all variables set to 0
   */
  public WeeklyPageView getCurrentPageViews(String restaurantKey) {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int week = calendar.get(Calendar.WEEK_OF_YEAR);

    CompositeFilter compositeFilter = new CompositeFilter(Query.CompositeFilterOperator.AND,
        Arrays.asList(
            new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, restaurantKey),
            new FilterPredicate("year", Query.FilterOperator.EQUAL, year),
            new FilterPredicate("week", Query.FilterOperator.EQUAL, week)));

    Query query = new Query("PageViews").setFilter(compositeFilter);
    PreparedQuery preparedQuery = dataStore.prepare(query);
    Entity results = preparedQuery.asSingleEntity();

    // If there are no entities of that restaurant, return a page view with zeroes in all fields
    if (results == null) {
      return new WeeklyPageView(0, 0, 0);
    }

    return new WeeklyPageView(
        week, year, Integer.parseInt(results.getProperty("count").toString()));
  }

  /**
   * Gets the weekly pageViews for the entire year of a specific restaurant.
   * @param year we are looking for
   * @param restaurantKey of restaurant we want the data.
   * @return List<WeeklyPageViews> sorted by week in ascending order.
   */
  public List<WeeklyPageView> getYearRestaurantPageViews(int year, String restaurantKey) {
    CompositeFilter compositeFilter = new CompositeFilter(Query.CompositeFilterOperator.AND,
        Arrays.asList(
            new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, restaurantKey),
            new FilterPredicate("year", Query.FilterOperator.EQUAL, year)));
    Query query = new Query("PageViews")
                      .setFilter(compositeFilter)
                      .addSort("week", Query.SortDirection.ASCENDING);
    PreparedQuery preparedQuery = dataStore.prepare(query);
    List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withDefaults());

    List<WeeklyPageView> pageViewsList = new ArrayList<>();

    WeeklyPageView pageView;
    for (Entity entity : results) {
      pageViewsList.add(new WeeklyPageView(Integer.parseInt(entity.getProperty("week").toString()),
          year, Integer.parseInt(entity.getProperty("count").toString())));
    }

    return pageViewsList;
  }

  /**
   * Private class to better organize the necessary information about a restaurant.
   */
  private static class RestaurantPageViewInformation {
    private final String name;
    private List<WeeklyPageView> weeklyPageViewList;

    public RestaurantPageViewInformation(String name, List<WeeklyPageView> weeklyPageViewList) {
      this.name = name;
      this.weeklyPageViewList = weeklyPageViewList;
    }

    public String getName() {
      return name;
    }

    public List<WeeklyPageView> getWeeklyPageViewList() {
      return weeklyPageViewList;
    }
  }

  /**
   * Gets all pageViews for the entire system.
   * @return returns List<RestaurantPageViews> with the PageView list inside each instance
   *         of RestaurantPageViews sorted by week and year in ascending order.
   */
  public List<RestaurantPageViews> getAllPageViews() {
    // Query with double sort so the first element of every restaurant should be
    // the earliest week and year.
    Query query = new Query("PageViews")
                      .addSort("year", Query.SortDirection.ASCENDING)
                      .addSort("week", Query.SortDirection.ASCENDING);
    PreparedQuery preparedQuery = dataStore.prepare(query);
    List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withDefaults());

    // Map to maintain a reference to all the information needed about a restaurant
    // and make searching for them easy.
    Map<String, RestaurantPageViewInformation> restaurantIdMap = new HashMap<>();

    for (Entity entity : results) {
      String name = entity.getProperty("restaurantName").toString();
      String id = entity.getProperty("restaurantKey").toString();
      int year = Integer.parseInt(entity.getProperty("year").toString());
      int week = Integer.parseInt(entity.getProperty("week").toString());
      int count = Integer.parseInt(entity.getProperty("count").toString());
      if (restaurantIdMap.containsKey(id)) {
        restaurantIdMap.get(id).getWeeklyPageViewList().add(new WeeklyPageView(week, year, count));
      } else {
        List<WeeklyPageView> currRestaurantList = new ArrayList<>();
        currRestaurantList.add(new WeeklyPageView(week, year, count));
        restaurantIdMap.put(id, new RestaurantPageViewInformation(name, currRestaurantList));
      }
    }

    // Build the return list
    List<RestaurantPageViews> restaurantPageViewsList = new ArrayList<>();
    for (Map.Entry<String, RestaurantPageViewInformation> entry : restaurantIdMap.entrySet()) {
      restaurantPageViewsList.add(new RestaurantPageViews(
          entry.getValue().getName(), entry.getKey(), entry.getValue().getWeeklyPageViewList()));
    }

    return restaurantPageViewsList;
  }

  /**
   * Adds an entity for the current week for that restaurant or updates an existing one.
   * @param restaurantKey key of the restaurant we want to update or create.
   * @return boolean that expresses true if the transaction was completed successfully or false if
   *     otherwise.
   */
  public boolean putPageView(String restaurantKey) {
    Transaction transaction = dataStore.beginTransaction();

    try {
      Calendar calendar = Calendar.getInstance();
      int year = calendar.get(Calendar.YEAR);
      int week = calendar.get(Calendar.WEEK_OF_YEAR);

      CompositeFilter compositeFilter = new CompositeFilter(Query.CompositeFilterOperator.AND,
          Arrays.asList(
              new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, restaurantKey),
              new FilterPredicate("year", Query.FilterOperator.EQUAL, year),
              new FilterPredicate("week", Query.FilterOperator.EQUAL, week)));

      Query query = new Query("PageViews").setFilter(compositeFilter);
      PreparedQuery results = dataStore.prepare(query);
      Entity resultsEntity = results.asSingleEntity();
      if (resultsEntity == null) {
        // Query for the name
        Query nameQuery =
            new Query("RestaurantInfo")
                .setFilter(
                    new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL,
                        KeyFactory.createKey("RestaurantInfo", Long.parseLong(restaurantKey))));
        PreparedQuery nameResult = dataStore.prepare(nameQuery);
        Entity restaurant = nameResult.asSingleEntity();
        String restaurantName = restaurant.getProperty("name").toString();

        // Create entity
        Entity entity = new Entity("PageViews");
        entity.setProperty("restaurantKey", restaurantKey);
        entity.setProperty("restaurantName", restaurantName);
        entity.setProperty("year", year);
        entity.setProperty("week", week);
        entity.setProperty("count", 1);
        dataStore.put(transaction, entity);
      } else {
        // This week already exists, so update it
        int currentViews = Integer.parseInt(resultsEntity.getProperty("count").toString());
        resultsEntity.setProperty("count", currentViews + 1);
        dataStore.put(transaction, resultsEntity);
      }
      transaction.commit();
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
        return false;
      }
      return true;
    }
  }
}
