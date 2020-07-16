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
import java.util.*;

/**
 * This client is in charge of interacting with the servlet and the metrics entities.
 */
public class MetricsClient {

    /**
     * Empty constructor since we don't need any variables, we only use this class for the methods.
     */
    public MetricsClient() {}

    DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();

    /**
     * Gets the current week's page views by specifying a restaurantKey.
     * @param restaurantKey restaurant key of the restaurant we want to get views.
     * @param restaurantName restaurant name in case no entity exists for this week's page views.
     * @return returns a Map<String, Object> of the form:
     *          {
     *              "year": <String, the year number>,
     *              "week": <String, this week's number in relation to the year.>,
     *              "count": <String, amount of page views the restaurant had.>
     *          }
     */
    public Map<String, Object> getCurrentPageViews(String restaurantName, String restaurantKey) {
        Map<String, Object> resultMap = new HashMap<>();

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

        // If there are no entities of that restaurant, add one with a count of 0.
        if (results == null) {
            Transaction transaction = dataStore.beginTransaction();
            try {
                Entity entity = new Entity("PageViews");
                entity.setProperty("restaurantKey", restaurantKey);
                entity.setProperty("restaurantName", restaurantName);
                entity.setProperty("year", year);
                entity.setProperty("week", week);
                entity.setProperty("count", 0);
                dataStore.put(transaction, entity);

                // Set the count for the return map as 0, since the entity doesn't exist.
                resultMap.put("count", 0);
                transaction.commit();
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }
        } else {
            // If the results didn't come back null, give the count.
            resultMap.put("count", results.getProperty("count"));
        }

        resultMap.put("year", year);
        resultMap.put("week", week);

        return resultMap;
    }

    /**
     * Gets the weekly pageViews for the entire year of a specific restaurant.
     * @param year we are looking for
     * @param restaurantKey of restaurant we want the data.
     * @return list of maps in the following way: List<Map<String, Object>>
     *          [
     *                 {
     *                      "year": <String, the year number>,
     *                      "week": <String, this week's number in relation to the year.>,
     *                      "count": <String, amount of page views the restaurant had.>
     *                  },
     *                  ...,
     *          ]
     */
    public List<Map<String, Object>> getYearRestaurantPageViews(int year, String restaurantKey) {

        CompositeFilter compositeFilter = new CompositeFilter(Query.CompositeFilterOperator.AND,
                Arrays.asList(
                        new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, restaurantKey),
                        new FilterPredicate("year", Query.FilterOperator.EQUAL, year)
                ));
        Query query = new Query("PageViews").setFilter(compositeFilter).addSort("week", Query.SortDirection.ASCENDING);
        PreparedQuery preparedQuery = dataStore.prepare(query);
        List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withDefaults());

        List<Map<String, Object>> resultList = new ArrayList<>();

        Map<String, Object> map;
        for (Entity entity : results) {
            map = new HashMap<>();
            map.put("week", entity.getProperty("week").toString());
            map.put("count", Integer.parseInt(entity.getProperty("count").toString()));
            resultList.add(map);
        }

        return resultList;
    }

    /**
     * Gets all pageViews for the entire system.
     * @return returns a Map<String, Object> in the following format:
     *          {
     *              "firstDate":
     *                  {
     *                      "week": <int>
     *                      "year": <int>
     *                  },
     *              "clickData":
     *                  [
     *                      {
     *                          "restaurantName": <String>
     *                          "week": <int>
     *                          "year": <int>
     *                          "numClicks": <int>
     *                      },
     *                      ...
     *                  ]
     *          }
     *
     *          "clickData" represents all the click data that we have.
     *          "firstDate" represent the first time data was collected in our system.
     */
    public Map<String, Object> getAllPageViews() {
        Query query = new Query("PageViews");
        PreparedQuery preparedQuery = dataStore.prepare(query);
        List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withDefaults());

        Map<String, Object> resultMap = new HashMap<>();

        Map<String, Integer> firstPageView = new HashMap<>();
        firstPageView.put("year", 0);
        firstPageView.put("week", 0);

        List<Map<String, Object>> clickData = populateClickData(results, firstPageView);

        resultMap.put("firstDate", firstPageView);
        resultMap.put("clickData", clickData);

        return resultMap;
    }

    /**
     * Parses the array of entities and populates a list of maps that contain the required information.
     * @param entityList list of entities that we need to be turned into a map: List<Entity>
     * @param firstPageView map of the earliest date in one of the entities: Map<String, Integer>
     * @return returns a list of maps representative of the click data:
     *              "clickData":
     *                  [
     *                      {
     *                          "restaurantName": <String>
     *                          "week": <int>
     *                          "year": <int>
     *                          "numClicks": <int>
     *                      },
     *                      ...
     *                  ]
     */
    protected List<Map<String, Object>> populateClickData(List<Entity> entityList, Map<String, Integer> firstPageView) {
        Integer entityYear;
        Integer entityWeek;
        Map<String, Object> tempClickData;
        List<Map<String, Object>> clickData = new ArrayList<>();

        for (Entity entity : entityList) {
            entityYear = Integer.parseInt(entity.getProperty("year").toString());
            entityWeek = Integer.parseInt(entity.getProperty("week").toString());

            // Check for an update on the earliest date.
            firstPageView = checkEarlierDate(firstPageView, entityWeek, entityYear);

            tempClickData = new HashMap<>();
            tempClickData.put("restaurantName", entity.getProperty("restaurantName").toString());
            tempClickData.put("week", entityWeek);
            tempClickData.put("year", entityYear);
            tempClickData.put("numClicks", entity.getProperty("count"));

            clickData.add(tempClickData);
        }
        return clickData;
    }

    /**
     * Method to obtain the earliest instance of a click in all our entities. This method compares
     * the current earliest date with the current entity being compared.
     * @param currentDates current earliest date of a click: Map<String, Integer>
     * @param week  to compare with the earliest week from the current entity being compared.
     * @param year to compare with the earliest year from the current entity being compared.
     * @return returns the same map as current dates but updated after the comparison.
     *          {
     *              "week": <int>,
     *              "year": <int>
     *          }
     */
    protected Map<String, Integer> checkEarlierDate(Map<String, Integer> currentDates, Integer week, Integer year) {
        if (currentDates.get("year") == 0 && currentDates.get("week") == 0) {
            // If the year and week are 0.
            currentDates.put("year", year);
            currentDates.put("week", week);
        } else if (currentDates.get("year") > year) {
            // If the year in the map is bigger than the one in the entity.
            currentDates.put("year", year);
            currentDates.put("week", week);
        } else if (currentDates.get("year") == year && currentDates.get("week") > week) {
            // If the year is the same but the week of the map is bigger than the week of the entity.
            currentDates.put("week", week);
        }
        return currentDates;
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
