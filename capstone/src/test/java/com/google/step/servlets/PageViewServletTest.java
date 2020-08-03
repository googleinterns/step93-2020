package com.google.step.servlets;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.appengine.repackaged.com.google.gson.JsonArray;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;
import com.google.step.data.RestaurantPageViews;
import com.google.step.data.WeeklyPageView;
import com.meterware.httpunit.*;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;

/**
 * Test servlet for the PageViewServlet
 */
public class PageViewServletTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testGetCurrentPageViews() throws SAXException, IOException {
        // Get's this week's page views.
        // Result: {
        //              week: this week #,
        //              year: this week # (2020),
        //              count: 200
        //          }

        // Calendar is needed because it is used by the putPageView method in the client.
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        Entity entity1 = new Entity("PageViews");
        entity1.setProperty("restaurantName", "Marlows");
        entity1.setProperty("restaurantKey", "1");
        entity1.setProperty("year", year);
        entity1.setProperty("week", week);
        entity1.setProperty("count", 200);

        Entity entity2 = new Entity("PageViews");
        entity2.setProperty("restaurantName", "Marlows");
        entity2.setProperty("restaurantKey", "1");
        entity2.setProperty("year", year);
        entity2.setProperty("week", 2);
        entity2.setProperty("count", 100);

        datastoreService.put(entity1);
        datastoreService.put(entity2);

        ServletRunner sr = new ServletRunner();
        sr.registerServlet("page-views", PageViewServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // Make the request.
        WebRequest request = new GetMethodWebRequest("http://localhost:8080/page-views");
        request.setParameter("restaurantKey", "1");
        WebResponse response = sc.getResponse(request);

        Assert.assertNotNull("Response is received", response);
        Assert.assertEquals("content type", "application/json", response.getContentType());

        JsonObject jsonObject = turnStringToJson(response.getText());

        Assert.assertEquals("Year", year, Integer.parseInt(jsonObject.get("year").toString()));
        Assert.assertEquals("Week", week, Integer.parseInt(jsonObject.get("week").toString()));
        Assert.assertEquals("Count", "200", jsonObject.get("count").toString());
    }

    @Test
    public void testGetYearPageViews() throws SAXException, IOException {
        // Gets all the pageViews for a specific restaurant for the entire year
        // Result: List<WeeklyPageView> (
        //          [
        //              WeeklyPageView(
        //                  week: 2
        //                  year: this year # (2020)
        //                  count: 200
        //              )
        //              WeeklyPageView(
        //                  week: this week # (bigger than 2 in this case)
        //                  year: this year # (2020)
        //                  count: 100
        //              )
        //          ]

        // Calendar is needed because it is used by the putPageView method in the client.
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        Entity entity1 = new Entity("PageViews");
        entity1.setProperty("restaurantName", "Marlows");
        entity1.setProperty("restaurantKey", "1");
        entity1.setProperty("year", year);
        entity1.setProperty("week", week);
        entity1.setProperty("count", 100);

        Entity entity2 = new Entity("PageViews");
        entity2.setProperty("restaurantName", "Marlows");
        entity2.setProperty("restaurantKey", "1");
        entity2.setProperty("year", year);
        entity2.setProperty("week", 2);
        entity2.setProperty("count", 200);

        // Different year
        Entity entity3 = new Entity("PageViews");
        entity3.setProperty("restaurantName", "Marlows");
        entity3.setProperty("restaurantKey", "1");
        entity3.setProperty("year", 2015);
        entity3.setProperty("week", 3);
        entity3.setProperty("count", 1000);

        datastoreService.put(entity1);
        datastoreService.put(entity2);
        datastoreService.put(entity3);

        List<WeeklyPageView> expected = new ArrayList<>();
        expected.add(new WeeklyPageView(2, year, 200));
        expected.add(new WeeklyPageView(week, year, 100));

        ServletRunner sr = new ServletRunner();
        sr.registerServlet("page-views", PageViewServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // Make the request.
        WebRequest request = new GetMethodWebRequest("http://localhost:8080/page-views");
        request.setParameter("restaurantKey", "1");
        request.setParameter("year", "2020");
        WebResponse response = sc.getResponse(request);

        Assert.assertNotNull("Response is received", response);
        Assert.assertEquals("content type", "application/json", response.getContentType());
        JsonArray actual = turnStringToJsonArray(response.getText());

        // Check every element from the actual against the expected.
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertEquals("Year", expected.get(i).getYear(),
                    Integer.parseInt(actual.get(i).getAsJsonObject().get("year").toString()));
            Assert.assertEquals("Week", expected.get(i).getWeek(),
                    Integer.parseInt(actual.get(i).getAsJsonObject().get("week").toString()));
            Assert.assertEquals("Count", expected.get(i).getCount(),
                    Integer.parseInt(actual.get(i).getAsJsonObject().get("count").toString()));
        }
    }

    @Test
    public void testGetAllPageViews() throws SAXException, IOException {
        // Tests the getAllPageViews.
        // Result: [
        //              RestaurantPageView(
        //                  name: "Marlows"
        //                  pageViews: [
        //                      WeeklyPageView(
        //                          week: 20
        //                          year: 2013
        //                          count: 100
        //                      ),
        //                      WeeklyPageView(
        //                          week: 2
        //                          year: 2014
        //                          count: 200
        //                      )
        //                 ]
        //              ),
        //              RestaurantPageView(
        //                  name: "Subway"
        //                  pageViews: [
        //                      WeeklyPageView(
        //                          week: 3
        //                          year: 2015
        //                          count: 1000
        //                      )
        //                 ]
        //              )
        //         ]

        Entity entity1 = new Entity("PageViews");
        entity1.setProperty("restaurantName", "Marlows");
        entity1.setProperty("restaurantKey", "1");
        entity1.setProperty("year", 2013);
        entity1.setProperty("week", 20);
        entity1.setProperty("count", 100);

        Entity entity2 = new Entity("PageViews");
        entity2.setProperty("restaurantName", "Marlows");
        entity2.setProperty("restaurantKey", "1");
        entity2.setProperty("year", 2014);
        entity2.setProperty("week", 2);
        entity2.setProperty("count", 200);

        // Different restaurant
        Entity entity3 = new Entity("PageViews");
        entity3.setProperty("restaurantName", "Subway");
        entity3.setProperty("restaurantKey", "2");
        entity3.setProperty("year", 2015);
        entity3.setProperty("week", 3);
        entity3.setProperty("count", 1000);

        List<RestaurantPageViews> expected = new ArrayList<>();
        expected.add(new RestaurantPageViews(
                "Marlows",
                "1",
                new ArrayList<WeeklyPageView>(
                        Arrays.asList(
                                new WeeklyPageView(20, 2013, 100),
                                new WeeklyPageView(2, 2014, 200)))));
        expected.add(new RestaurantPageViews(
                "Subway",
                "2",
                new ArrayList<WeeklyPageView>(
                        Arrays.asList(
                                new WeeklyPageView(3, 2015, 1000)))));

        datastoreService.put(entity1);
        datastoreService.put(entity2);
        datastoreService.put(entity3);

        ServletRunner sr = new ServletRunner();
        sr.registerServlet("page-views", PageViewServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // Make the request.
        WebRequest request = new GetMethodWebRequest("http://localhost:8080/page-views");
        WebResponse response = sc.getResponse(request);

        Assert.assertNotNull("Response is received", response);
        Assert.assertEquals("content type", "application/json", response.getContentType());
        JsonArray actual = turnStringToJsonArray(response.getText());

        // Test every element in the actual list
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertEquals("Name", "\"" + expected.get(i).getName() + "\"",
                    actual.get(i).getAsJsonObject().get("name").toString());
            JsonArray currPageViews = actual.get(i).getAsJsonObject().get("pageViews").getAsJsonArray();
            for (int x = 0; x < expected.get(i).getPageViews().size(); x++) {
                Assert.assertEquals("Week", expected.get(i).getPageViews().get(x).getWeek(),
                        Integer.parseInt(currPageViews.get(x).getAsJsonObject().get("week").toString()));
                Assert.assertEquals("Year", expected.get(i).getPageViews().get(x).getYear(),
                        Integer.parseInt(currPageViews.get(x).getAsJsonObject().get("year").toString()));
                Assert.assertEquals("Count", expected.get(i).getPageViews().get(x).getCount(),
                        Integer.parseInt(currPageViews.get(x).getAsJsonObject().get("count").toString()));
            }
        }
    }

    @Test
    public void testGetPageViewsError() throws SAXException, IOException {
        // Tests the doGet method error by not having one of the three combinations
        // needed to make a request.

        ServletRunner sr = new ServletRunner();
        sr.registerServlet("page-views", PageViewServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // Make the request.
        WebRequest request = new GetMethodWebRequest("http://localhost:8080/page-views");
        request.setParameter("year", "1");

        try {
            sc.getResponse(request);
        } catch (HttpException e) {
            Assert.assertEquals("Error code", HttpServletResponse.SC_BAD_REQUEST, e.getResponseCode());
        }
    }

    @Test
    public void testDoPostExistingEntity() throws SAXException, IOException {
        // Tests the doPost when the entity that we are making the request to already exists,
        // thus that entity count should be updated from 199 to 200.

        // Calendar is needed because it is used by the putPageView method in the client.
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        Entity entity = new Entity("PageViews");
        entity.setProperty("restaurantName", "Marlows");
        entity.setProperty("restaurantKey", "1");
        entity.setProperty("year", year);
        entity.setProperty("week", week);
        entity.setProperty("count", 199);
        datastoreService.put(entity);

        ServletRunner sr = new ServletRunner();
        sr.registerServlet("page-views", PageViewServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // Make the request.
        WebRequest request = new PostMethodWebRequest("http://localhost:8080/page-views");
        request.setParameter("restaurantName", "Marlows");
        request.setParameter("restaurantKey", "1");
        sc.getResponse(request);

        // Check if the put worked.
        Query query = new Query("PageViews").setFilter(new Query.FilterPredicate("restaurantKey",
                Query.FilterOperator.EQUAL, "1"));
        PreparedQuery results = datastoreService.prepare(query);
        Entity result = results.asSingleEntity();

        Assert.assertEquals("count", "200", result.getProperty("count").toString());

        // Test again to make sure it supports multiple put requests.
        sc.getResponse(request);
        query = new Query("PageViews").setFilter(new Query.FilterPredicate("restaurantKey",
                Query.FilterOperator.EQUAL, "1"));
        results = datastoreService.prepare(query);
        result = results.asSingleEntity();

        Assert.assertEquals("count", "201", result.getProperty("count").toString());
    }

    @Test
    public void testDoPostCreateEntity() throws SAXException, IOException {
        // Tests the doPost by having to create the entity which we are calling the doPost request for.

        Entity entity = new Entity("RestaurantInfo");
        entity.setProperty("restaurantKey", "1");
        entity.setProperty("name", "Marlows");
        datastoreService.put(entity);

        ServletRunner sr = new ServletRunner();
        sr.registerServlet("page-views", PageViewServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // Make the request.
        WebRequest request = new PostMethodWebRequest("http://localhost:8080/page-views");
        request.setParameter("restaurantKey", "1");
        sc.getResponse(request);

        // Check if the put worked.
        Query query = new Query("PageViews").setFilter(new Query.FilterPredicate("restaurantKey",
                Query.FilterOperator.EQUAL, "1"));
        PreparedQuery results = datastoreService.prepare(query);

        // This would error if there was more than 1 entity.
        Entity result = results.asSingleEntity();

        Assert.assertEquals("Name", entity.getProperty("name").toString(), result.getProperty("restaurantName").toString());
        Assert.assertEquals("count", "1", result.getProperty("count").toString());
    }

    @Test
    public void testDoPostError() throws SAXException, IOException {
        // Tests the doPost by having an inaccurate combination of parameters needed to make the request.

        ServletRunner sr = new ServletRunner();
        sr.registerServlet("page-views", PageViewServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // Make the request.
        WebRequest request = new PostMethodWebRequest("http://localhost:8080/page-views");
        request.setParameter("restaurantName", "Marlows");

        try {
            sc.getResponse(request);
        } catch (HttpException e) {
            Assert.assertEquals("Error code", HttpServletResponse.SC_BAD_REQUEST, e.getResponseCode());
        }
    }

    /**
     * Turns a Json string into a Json object
     * @param json string to be turned into object
     * @return JsonObject with elements of the String
     */
    private static JsonObject turnStringToJson(String json) {
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        return jsonObject;
    }

    /**
     * Turns a Json String into a Json Array
     * @param json string to be turned into array
     * @return JsonArray with elements of the string.
     */
    private static JsonArray turnStringToJsonArray(String json) {
        JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
        return jsonArray;
    }
}
