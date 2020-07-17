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

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;


import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import java.util.Calendar;

public class MetricsClientTests {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private final MetricsClient metricsClient = new MetricsClient();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    public void checkIfDataStoreWorks() {
        assertEquals(0, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
        datastoreService.put(new Entity("User"));
        datastoreService.put(new Entity("User"));
        assertEquals(2, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
    }

    @Test
    public void testCheckWorks1() {
        checkIfDataStoreWorks();
    }

    @Test
    public void testCheckWorks2() {
        checkIfDataStoreWorks();
    }

    @Test
    public void testPutPageView() {
        // Checks if the putPageView method is correctly putting data into dataStore.
        metricsClient.putPageView("Mc Donald's", "1");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        CompositeFilter compositeFilter = new Query.CompositeFilter(Query.CompositeFilterOperator.AND,
                Arrays.asList(
                        new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, "1"),
                        new FilterPredicate("year", Query.FilterOperator.EQUAL, year),
                        new FilterPredicate("week", Query.FilterOperator.EQUAL, week)
                ));

        Query query = new Query("PageViews").setFilter(compositeFilter);
        PreparedQuery results = datastoreService.prepare(query);
        Entity resultEntity = results.asSingleEntity();
        Assert.assertEquals("Count", (long) 1, resultEntity.getProperty("count"));
    }

    @Test
    public void testSeveralPutPageViews() {
        // Checks if the putPageView method is correctly putting and updating an entity in dataStore.
        metricsClient.putPageView("Mc Donald's", "1");
        metricsClient.putPageView("Mc Donald's", "1");
        metricsClient.putPageView("Mc Donald's", "1");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        CompositeFilter compositeFilter = new Query.CompositeFilter(Query.CompositeFilterOperator.AND,
                Arrays.asList(
                        new FilterPredicate("restaurantKey", Query.FilterOperator.EQUAL, "1"),
                        new FilterPredicate("year", Query.FilterOperator.EQUAL, year),
                        new FilterPredicate("week", Query.FilterOperator.EQUAL, week)
                ));

        Query query = new Query("PageViews").setFilter(compositeFilter);
        PreparedQuery results = datastoreService.prepare(query);
        Entity resultEntity = results.asSingleEntity();
        Assert.assertEquals("Count", (long) 3, resultEntity.getProperty("count"));
    }

    @Test
    public void testGetYearRestaurantPageViews() {
        // Tests if method is correctly grabbing the correct data for a specified year and restaurant.
        // Result: [
        //              {
        //                  "week": 2,
        //                  "count": 100
        //              },
        //              {
        //                  "week": 3,
        //                  "count": 200
        //              }
        //          ]
        // Same restaurant same year
        Entity entity1 = new Entity("PageViews");
        entity1.setProperty("restaurantKey", "1");
        entity1.setProperty("week", 2);
        entity1.setProperty("year", 2015);
        entity1.setProperty("count", 100);

        // Same restaurant same year
        Entity entity2 = new Entity("PageViews");
        entity2.setProperty("restaurantKey", "1");
        entity2.setProperty("week", 3);
        entity2.setProperty("year", 2015);
        entity2.setProperty("count", 200);

        // Same year different restaurant
        Entity entity3 = new Entity("PageViews");
        entity3.setProperty("restaurantKey", "2");
        entity3.setProperty("week", 2);
        entity3.setProperty("year", 2015);
        entity3.setProperty("count", 1000);

        // Same restaurant different year
        Entity entity4 = new Entity("PageViews");
        entity4.setProperty("restaurantKey", "1");
        entity4.setProperty("week", 5);
        entity4.setProperty("year", 2016);
        entity4.setProperty("count", 300);

        datastoreService.put(entity1);
        datastoreService.put(entity2);
        datastoreService.put(entity3);
        datastoreService.put(entity4);

        List<Map<String, Object>> yearPageViews = metricsClient.getYearRestaurantPageViews(2015, "1");
        Assert.assertEquals("Week #", "2", yearPageViews.get(0).get("week").toString());
        Assert.assertEquals("Week 2 count", "100", yearPageViews.get(0).get("count").toString());

        Assert.assertEquals("Week #", "3", yearPageViews.get(1).get("week").toString());
        Assert.assertEquals("Week 3 count", "200", yearPageViews.get(1).get("count").toString());
    }

    @Test
    public void testGetCurrentPageViews() {
        // Gets the pageViews for a specific restaurant this week
        // Result:
        //      {
        //          "year": current year (2020),
        //          "week": current week number,
        //          "count": 100
        //      }

        // Using calendar for this test is necessary since the method in the
        // metricsClient uses the calendar as well to get the current date.
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        Entity entity1 = new Entity("PageViews");
        entity1.setProperty("restaurantKey", "1");
        entity1.setProperty("restaurantName", "Marlows");
        entity1.setProperty("year", year);
        entity1.setProperty("week", week);
        entity1.setProperty("count", 100);

        Entity entity2 = new Entity("PageViews");
        entity2.setProperty("restaurantKey", "1");
        entity2.setProperty("restaurantName", "Marlows");
        entity2.setProperty("year", 2015);
        entity2.setProperty("week", 7);
        entity2.setProperty("count", 200);

        datastoreService.put(entity1);
        datastoreService.put(entity2);

        Map<String, Object> resultMap = metricsClient.getCurrentPageViews("Marlows", "1");
        Assert.assertEquals("Year", year, resultMap.get("year"));
        Assert.assertEquals("Week", week, resultMap.get("week"));
        Assert.assertEquals("Count", "100", resultMap.get("count").toString());
    }
    @Test
    public void testGetCurrentPageViewsWithoutExistingEntity() {
        // Tests if the method correctly created an entity and puts into dataStore
        // if no entity exists for this week currently.
        // Result:
        //      {
        //          "year": current year (2020),
        //          "week": current week number,
        //          "count": 0
        //      }

        // Using calendar for this test is necessary since the method in the
        // metricsClient uses the calendar as well to get the current date.
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        Map<String, Object> resultMap = metricsClient.getCurrentPageViews("Marlows", "1");
        Assert.assertEquals("Year", year, resultMap.get("year"));
        Assert.assertEquals("Week", week, resultMap.get("week"));
        Assert.assertEquals("Count", "0", resultMap.get("count").toString());
        Assert.assertEquals(1, datastoreService.prepare(new Query("PageViews")).countEntities(withLimit(10)));
    }

    @Test
    public void testCheckBeginningDate() {
        // Checks the map is being updated correctly when the year and week have only been initialized.
        // Result: {"year": 1, "week": 1}
        Map<String, Integer> map = new HashMap<>();
        map.put("year", Integer.MAX_VALUE);
        map.put("week", Integer.MAX_VALUE);
        metricsClient.checkEarlierDate(map, 1, 1);
        Assert.assertEquals("Year", "1", map.get("year").toString());
        Assert.assertEquals("Week", "1", map.get("week").toString());
    }

    @Test
    public void testCheckSeveralDates() {
        // Tests for several updates on the method.
        Map<String, Integer> map = new HashMap<>();
        map.put("year", 2009);
        map.put("week", 15);

        // Later year and week.
        // Result:
        //          {
        //              "year": 2009,
        //              "week": 15
        //          }
        metricsClient.checkEarlierDate(map, 16, 2010);
        Assert.assertEquals("Year", "2009", map.get("year").toString());
        Assert.assertEquals("Week", "15", map.get("week").toString());

        // Later year but earlier week.
        // Result:
        //          {
        //              "year": 2009,
        //              "week": 15
        //          }
        metricsClient.checkEarlierDate(map, 9, 2011);
        Assert.assertEquals("Year", "2009", map.get("year").toString());
        Assert.assertEquals("Week", "15", map.get("week").toString());

        // Earlier year but later week.
        // Result:
        //          {
        //              "year": 2008,
        //              "week": 42
        //          }
        metricsClient.checkEarlierDate(map, 42, 2008);
        Assert.assertEquals("Year", "2008", map.get("year").toString());
        Assert.assertEquals("Week", "42", map.get("week").toString());
    }

    @Test
    public void testPopulateClickData() {
        // Tests for the correctly populated array by the method.
        /* Result:
         *          [
         *              {
         *                  "restaurantName": "McDonalds",
         *                  "week": 15,
         *                  "year": 2019,
         *                  "numClicks": 100
         *              },
         *              {
         *                  "restaurantName": "Marlows",
         *                  "week": 15,
         *                  "year": 2019,
         *                  "numClicks": 100
         *              },
         *              {
         *                  "restaurantName": "Marlows",
         *                  "week": 20,
         *                  "year": 2019,
         *                  "numClicks": 100
         *              },
         *          ]
         */
        List<Entity> entityList = new ArrayList<>();
        Map<String, Integer> firstPageView = new HashMap<>();
        firstPageView.put("year", 0);
        firstPageView.put("week", 0);

        Entity entity1 = new Entity("PageViews");
        entity1.setProperty("restaurantKey", "1");
        entity1.setProperty("restaurantName", "McDonalds");
        entity1.setProperty("week", 15);
        entity1.setProperty("year", 2019);
        entity1.setProperty("count", 100);

        Entity entity2 = new Entity("PageViews");
        entity2.setProperty("restaurantKey", "2");
        entity2.setProperty("restaurantName", "Marlows");
        entity2.setProperty("week", 20);
        entity2.setProperty("year", 2019);
        entity2.setProperty("count", 100);

        Entity entity3 = new Entity("PageViews");
        entity3.setProperty("restaurantKey", "2");
        entity3.setProperty("restaurantName", "Marlows");
        entity3.setProperty("week", 15);
        entity3.setProperty("year", 2018);
        entity3.setProperty("count", 200);
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);

        List<Map<String, Object>> actual = metricsClient.populateClickData(entityList, firstPageView);
        List<Map<String, Object>> expected = new ArrayList<>();

        // Populate the expected list.
        Map<String, Object> clickData;
        for (Entity entity : entityList) {
            clickData = new HashMap<>();
            clickData.put("restaurantName", entity.getProperty("restaurantName").toString());
            clickData.put("week", entity.getProperty("week"));
            clickData.put("year", entity.getProperty("year"));
            clickData.put("numClicks", entity.getProperty("count"));
            expected.add(clickData);
        }

        // Check each element in the actual list.
        for (int i = 0; i < expected.size(); i ++) {
            Assert.assertEquals("Restaurant Name", expected.get(i).get("restaurantName"), actual.get(i).get("restaurantName"));
            Assert.assertEquals("Week", expected.get(i).get("week"), actual.get(i).get("week"));
            Assert.assertEquals("Year", expected.get(i).get("year"), actual.get(i).get("year"));
            Assert.assertEquals("Number of Clicks", expected.get(i).get("numClicks"), actual.get(i).get("numClicks"));
        }
    }
}
