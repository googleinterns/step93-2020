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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilter;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.step.data.RestaurantPageViews;
import com.google.step.data.WeeklyPageView;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;

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
        //              WeeklyPageView with the following data:.
        //                  (
        //                      "week": 2,
        //                      "year": 2015,
        //                      "count": 100
        //                  ),
        //              WeeklyPageView with the following data.
        //                  (
        //                      "week": 3,
        //                      "year": 2015,
        //                      "count": 200
        //                  )
        //          ]
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

        // Different restaurant same year
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

        List<WeeklyPageView> yearPageViews = metricsClient.getYearRestaurantPageViews(2015, "1");
        Assert.assertEquals("Week #", 2, yearPageViews.get(0).getWeek());
        Assert.assertEquals("Week 2 count", 100, yearPageViews.get(0).getCount());

        Assert.assertEquals("Week #", 3, yearPageViews.get(1).getWeek());
        Assert.assertEquals("Week 3 count", 200, yearPageViews.get(1).getCount());
    }

    @Test
    public void testGetCurrentPageViews() {
        // Gets the pageViews for a specific restaurant this week
        // Result: WeeklyPageView with the variables set to:
        //      year: current year,
        //      week: current week relative to the year (e.g. week 47)
        //      count: 100

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

        WeeklyPageView resultPageView = metricsClient.getCurrentPageViews("1");
        Assert.assertEquals("Year", year, resultPageView.getYear());
        Assert.assertEquals("Week", week, resultPageView.getWeek());
        Assert.assertEquals("Count", 100, resultPageView.getCount());
    }

    @Test
    public void testGetCurrentPageViewsWithoutExistingEntity() {
        // Tests if the method correctly created an entity and puts into dataStore
        // if no entity exists for this week currently.
        // Result: WeeklyPageView with all variables set to 0.

        // Using calendar for this test is necessary since the method in the
        // metricsClient uses the calendar as well to get the current date.
        WeeklyPageView resultPageView = metricsClient.getCurrentPageViews("1");
        Assert.assertEquals("Week", 0, resultPageView.getWeek());
        Assert.assertEquals("Year", 0, resultPageView.getYear());
        Assert.assertEquals("Count", 0, resultPageView.getCount());
    }

    @Test
    public void testGetAllPageViews() {
        // Tests the getAllPageViews method by adding several restaurant page view entities.
        // Result: [
        //              RestaurantPageView(
        //                  name: "Subway"
        //                  pageViews: [
        //                      WeeklyPageView(
        //                          week: 11
        //                          year: 2015
        //                          count: 1000
        //                      )
        //                 ]
        //              ),
        //              RestaurantPageView(
        //                  name: "Marlows"
        //                  pageViews: [
        //                      WeeklyPageView(
        //                          week: 13
        //                          year: 2014
        //                          count: 200
        //                      ),
        //                      WeeklyPageView(
        //                          week: 13
        //                          year: 2015
        //                          count: 100
        //                      ),
        //                      WeeklyPageView(
        //                          week: 15
        //                          year: 2015
        //                          count: 50
        //                      ),
        //                      WeeklyPageView(
        //                          week: 20
        //                          year: 2016
        //                          count: 300
        //                      ),
        //                      WeeklyPageView(
        //                          week: 8
        //                          year: 2017
        //                          count: 5
        //                      )
        //                 ]
        //              ),
        //         ]
        // In the result, subway goes first in the return list because it has the earlier week from the entire dataStore.
        // But the other within each RestaurantPageView is by most recent date.

        Entity entity1 = new Entity("PageViews");
        entity1.setProperty("restaurantKey", "1");
        entity1.setProperty("restaurantName", "Marlows");
        entity1.setProperty("year", 2015);
        entity1.setProperty("week", 13);
        entity1.setProperty("count", 100);
        WeeklyPageView pageView1 = new WeeklyPageView(
                Integer.parseInt(entity1.getProperty("week").toString()),
                Integer.parseInt(entity1.getProperty("year").toString()),
                Integer.parseInt(entity1.getProperty("count").toString())
        );


        Entity entity2 = new Entity("PageViews");
        entity2.setProperty("restaurantKey", "1");
        entity2.setProperty("restaurantName", "Marlows");
        entity2.setProperty("year", 2015);
        entity2.setProperty("week", 15);
        entity2.setProperty("count", 50);
        WeeklyPageView pageView2 = new WeeklyPageView(
                Integer.parseInt(entity2.getProperty("week").toString()),
                Integer.parseInt(entity2.getProperty("year").toString()),
                Integer.parseInt(entity2.getProperty("count").toString())
        );

        Entity entity3 = new Entity("PageViews");
        entity3.setProperty("restaurantKey", "1");
        entity3.setProperty("restaurantName", "Marlows");
        entity3.setProperty("year", 2014);
        entity3.setProperty("week", 13);
        entity3.setProperty("count", 200);
        WeeklyPageView pageView3 = new WeeklyPageView(
                Integer.parseInt(entity3.getProperty("week").toString()),
                Integer.parseInt(entity3.getProperty("year").toString()),
                Integer.parseInt(entity3.getProperty("count").toString())
        );

        Entity entity4 = new Entity("PageViews");
        entity4.setProperty("restaurantKey", "1");
        entity4.setProperty("restaurantName", "Marlows");
        entity4.setProperty("year", 2016);
        entity4.setProperty("week", 20);
        entity4.setProperty("count", 300);
        WeeklyPageView pageView4 = new WeeklyPageView(
                Integer.parseInt(entity4.getProperty("week").toString()),
                Integer.parseInt(entity4.getProperty("year").toString()),
                Integer.parseInt(entity4.getProperty("count").toString())
        );

        Entity entity6 = new Entity("PageViews");
        entity6.setProperty("restaurantKey", "1");
        entity6.setProperty("restaurantName", "Marlows");
        entity6.setProperty("year", 2017);
        entity6.setProperty("week", 8);
        entity6.setProperty("count", 5);
        WeeklyPageView pageView6 = new WeeklyPageView(
                Integer.parseInt(entity6.getProperty("week").toString()),
                Integer.parseInt(entity6.getProperty("year").toString()),
                Integer.parseInt(entity6.getProperty("count").toString())
        );

        List<WeeklyPageView> pageViewList1 = new ArrayList<>();

        //This is the order in which they should be due to their dates.
        pageViewList1.add(pageView3);
        pageViewList1.add(pageView1);
        pageViewList1.add(pageView2);
        pageViewList1.add(pageView4);
        pageViewList1.add(pageView6);
        RestaurantPageViews restaurantPageView1 = new RestaurantPageViews(
                entity1.getProperty("restaurantName").toString(),
                pageViewList1
        );

        Entity entity5 = new Entity("PageViews");
        entity5.setProperty("restaurantKey", "2");
        entity5.setProperty("restaurantName", "Subway");
        entity5.setProperty("year", 2015);
        entity5.setProperty("week", 11);
        entity5.setProperty("count", 1000);
        WeeklyPageView pageView5 = new WeeklyPageView(
                Integer.parseInt(entity5.getProperty("week").toString()),
                Integer.parseInt(entity5.getProperty("year").toString()),
                Integer.parseInt(entity5.getProperty("count").toString())
        );
        List<WeeklyPageView> pageViewList5 = new ArrayList<>();
        pageViewList5.add(pageView5);
        RestaurantPageViews restaurantPageView2 = new RestaurantPageViews(
                entity5.getProperty("restaurantName").toString(),
                pageViewList5
        );

        List<RestaurantPageViews> expected = new ArrayList<>();
        expected.add(restaurantPageView1);
        expected.add(restaurantPageView2);

        datastoreService.put(entity1);
        datastoreService.put(entity2);
        datastoreService.put(entity3);
        datastoreService.put(entity4);
        datastoreService.put(entity5);
        datastoreService.put(entity6);

        List<RestaurantPageViews> actual = metricsClient.getAllPageViews();

        for (int i = 0; i < actual.size(); i++) {
            Assert.assertEquals("name", expected.get(i).getName(), actual.get(i).getName());
            for (int x = 0; x < actual.get(i).getPageViews().size(); x++) {
                Assert.assertEquals("year", expected.get(i).getPageViews().get(x).getYear(), actual.get(i).getPageViews().get(x).getYear());
                Assert.assertEquals("week", expected.get(i).getPageViews().get(x).getWeek(), actual.get(i).getPageViews().get(x).getWeek());
                Assert.assertEquals("count", expected.get(i).getPageViews().get(x).getCount(), actual.get(i).getPageViews().get(x).getCount());
            }
        }
    }
}
