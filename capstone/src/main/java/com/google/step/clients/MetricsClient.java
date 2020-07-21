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

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.step.data.Restaurant;
import com.google.step.data.RestaurantPageViews;
import com.google.step.data.WeeklyPageView;

import java.util.*;

/**
 * This client is in charge of interacting with metricsServlet and the metrics entities.
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
     * @param restaurantName restaurant name in case no entity exists for this week's page views.
     * @return returns an instance of a WeeklyPageView. If the entity doesn't exist yet,
     *         it returns an instance of WeeklyPageView with all variables set to 0
     */
    public WeeklyPageView getCurrentPageViews(String restaurantName, String restaurantKey) {
        WeeklyPageView weeklyPageView;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        CompositeFilter compositeFilter = new CompositeFilter(Query.CompositeFilterOperator.AND,
                Arrays.asList(
                        new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, restaurantKey),
                        new FilterPredicate("year", Query.FilterOperator.EQUAL, year),
                        new FilterPredicate("week", Query.FilterOperator.EQUAL, week)
                ));

        Query query = new Query("PageViews").setFilter(compositeFilter);
        PreparedQuery preparedQuery = dataStore.prepare(query);
        Entity results = preparedQuery.asSingleEntity();

        // If there are no entities of that restaurant, return a page view with zeroes in all fields
        if (results == null) {
            return new WeeklyPageView(0, 0, 0);
        }

        weeklyPageView = new WeeklyPageView(week, year, Integer.parseInt(results.getProperty("count").toString()));

        return weeklyPageView;
    }

    /**
     * Gets the weekly pageViews for the entire year of a specific restaurant.
     * @param year we are looking for
     * @param restaurantKey of restaurant we want the data.
     * @return List<WeeklyPageViews> sorted by week
     */
    public List<WeeklyPageView> getYearRestaurantPageViews(int year, String restaurantKey) {

        CompositeFilter compositeFilter = new CompositeFilter(Query.CompositeFilterOperator.AND,
                Arrays.asList(
                        new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, restaurantKey),
                        new FilterPredicate("year", Query.FilterOperator.EQUAL, year)
                ));
        Query query = new Query("PageViews").setFilter(compositeFilter).addSort("week", Query.SortDirection.ASCENDING);
        PreparedQuery preparedQuery = dataStore.prepare(query);
        List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withDefaults());

        List<WeeklyPageView> pageViewsList = new ArrayList<>();

        WeeklyPageView pageView;
        for (Entity entity : results) {
            pageView = new WeeklyPageView(Integer.parseInt(entity.getProperty("week").toString()),
                    year,
                    Integer.parseInt(entity.getProperty("count").toString()));
            pageViewsList.add(pageView);
        }

        return pageViewsList;
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
                .addSort("week", Query.SortDirection.ASCENDING)
                .addSort("year", Query.SortDirection.ASCENDING);
        PreparedQuery preparedQuery = dataStore.prepare(query);
        List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withDefaults());

        // Map to maintain a reference to each restaurant and make searching for them easy.
        // The map and the list share the reference to each restaurantPageViews.
        Map<String, RestaurantPageViews> restaurantNameMap = new HashMap<>();
        List<RestaurantPageViews> restaurantPageViewsList = new ArrayList<>();
        RestaurantPageViews currRestaurant;

        for (Entity entity : results) {
            String name = entity.getProperty("restaurantName").toString();
            int year = Integer.parseInt(entity.getProperty("year").toString());
            int week = Integer.parseInt(entity.getProperty("week").toString());
            int count = Integer.parseInt(entity.getProperty("count").toString());
            if (restaurantNameMap.containsKey(name)) {
                currRestaurant = restaurantNameMap.get(name);
                currRestaurant.getPageViews().add(new WeeklyPageView(week, year, count));
            } else {
                currRestaurant = new RestaurantPageViews(name,
                        new ArrayList<>());
                currRestaurant.getPageViews().add(new WeeklyPageView(week, year, count));
                restaurantNameMap.put(name, currRestaurant);
                restaurantPageViewsList.add(currRestaurant);
            }
        }

        return restaurantPageViewsList;
    }

    /**
     * Adds an entity for the current week for that restaurant or updates an existing one.
     * @param restaurantName name of the restaurant to input into dataStore.
     * @param restaurantKey key of the restaurant we want to update or create.
     * @return boolean that expresses true if the transaction was completed successfully or false if otherwise.
     */
    public boolean putPageView(String restaurantName, String restaurantKey) {
        Transaction transaction = dataStore.beginTransaction();

        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);

            CompositeFilter compositeFilter = new CompositeFilter(Query.CompositeFilterOperator.AND,
                    Arrays.asList(
                            new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, restaurantKey),
                            new FilterPredicate("year", Query.FilterOperator.EQUAL, year),
                            new FilterPredicate("week", Query.FilterOperator.EQUAL, week)
                    ));

            Query query = new Query("PageViews").setFilter(compositeFilter);
            PreparedQuery results = dataStore.prepare(query);
            Entity resultsEntity = results.asSingleEntity();
            if (resultsEntity == null) {
                //Create entity
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
