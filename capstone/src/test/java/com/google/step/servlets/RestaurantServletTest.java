package com.google.step.servlets;

import static org.junit.Assert.*;

import com.google.appengine.api.datastore.*;

import com.google.step.clients.RestaurantClient;
import com.meterware.httpunit.*;
import com.meterware.servletunit.ServletRunner;

import com.meterware.servletunit.ServletUnitClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONObject;

public class RestaurantServletTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    /**
     * Test for the doGet method of the RestaurantServlet. First adds
     * a dummy entity to the mock Datastore then calls doGet using a
     * ServletRunner to check the response against the expected format.
     * @throws IOException
     * @throws SAXException if the ServletRunner parser cannot handle the request
     */
    @Test
    public void testDoGet() throws IOException, SAXException {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        // Create dummy RestaurantInfo entity and add it to the mock Datastore
        long restaurantKey = 4;
        String name =  "Wildfire";
        String story = "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.";
        List<String> cuisineList = Arrays.asList("Steakhouse", "American");
        String phone = "8472797900";
        String website = "https://www.wildfirerestaurant.com";
        long score = (long)1.1;
        String status = "STRUGGLING";
        GeoPt location = new GeoPt((float) 42.17844, (float) -87.92843);
        Entity restaurantInfo1 = new Entity("RestaurantInfo");
        restaurantInfo1.setProperty("restaurantKey", restaurantKey);
        restaurantInfo1.setProperty("name", name);
        restaurantInfo1.setProperty("location", location);
        restaurantInfo1.setProperty("story", story);
        restaurantInfo1.setProperty("cuisine", cuisineList);
        restaurantInfo1.setProperty("phone", phone);
        restaurantInfo1.setProperty("website", website);
        restaurantInfo1.setProperty("score", score);
        restaurantInfo1.setProperty("status", status);
        datastoreService.put(restaurantInfo1);

        // Create a client to run the servlet
        ServletRunner sr = new ServletRunner();
        sr.registerServlet("restaurant", RestaurantServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // This can throw the SAXException.
        WebRequest request = new GetMethodWebRequest("http://localhost:8080/restaurant?restaurantKey=4");
        WebResponse response = sc.getResponse(request);
        assertNotNull("No response received", response);
        assertEquals("content type", "application/json", response.getContentType());
        String responseText = response.getText();

        JSONObject restaurantObject = new JSONObject(responseText).getJSONObject("value");

        assertEquals(restaurantKey, ((Integer)restaurantObject.get("restaurantKey")).longValue());
        assertEquals(name, restaurantObject.get("name"));
        assertEquals(location.getLatitude(), ((Double)restaurantObject.getJSONObject("location").get("latitude")).floatValue(), 0);
        assertEquals(location.getLongitude(), ((Double)restaurantObject.getJSONObject("location").get("longitude")).floatValue(), 0);
        assertEquals(story, restaurantObject.get("story"));
        for (int i = 0; i < cuisineList.size(); i++) {
            assertEquals(cuisineList.get(i), restaurantObject.getJSONArray("cuisine").get(i));
        }
        assertEquals(phone, restaurantObject.get("phone"));
        assertEquals(website, restaurantObject.get("website"));
        assertEquals(status, restaurantObject.get("status"));
    }

    /**
     * Test for failed doGet. Servlet runner requests a restaurantKey that is
     * not matched to anything in the mock Datastore so it should return an
     * HttpNotFoundException.
     * @throws IOException
     * @throws SAXException if the ServletRunner parser cannot handle the request
     */
    @Test(expected = HttpNotFoundException.class)
    public void testDoGetFailure() throws IOException, SAXException {
        // Create a client to run the servlet
        ServletRunner sr = new ServletRunner();
        sr.registerServlet("restaurant", RestaurantServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // This call to getResponse should throw an exception
        WebRequest request = new GetMethodWebRequest("http://localhost:8080/restaurant?restaurantKey=0");
        sc.getResponse(request);
    }

    /**
     * Test the doPost method of RestaurantServlet by creating a request with random params
     * then querying the mock Datastore to ensure the new restaurant was entered correctly.
     * @throws IOException
     * @throws SAXException if the ServletRunner parser cannot handle the request
     */
    @Test
    public void testDoPost() throws IOException, SAXException {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        String name =  "Wildfire";
        String story = "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.";
        String cuisineList = "Steakhouse, American";
        String phone = "8472797900";
        String website = "https://www.wildfirerestaurant.com";

        // User must be logged in to post a restaurant
        helper.setEnvIsAdmin(false).setEnvIsLoggedIn(true);
        helper.setEnvEmail("wildfire@gmail.com");
        helper.setEnvAuthDomain("google");

        // Create a client to run the servlet
        ServletRunner sr = new ServletRunner();
        sr.registerServlet("restaurant", RestaurantServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // Set params for the post request
        WebRequest request = new PostMethodWebRequest("http://localhost:8080/restaurant");
        request.setParameter("name", name);
        request.setParameter("cuisine", cuisineList);
        request.setParameter("story", story);
        request.setParameter("phone", phone);
        request.setParameter("website", website);
        sc.getResponse(request);

        // Simple query of the mock Datastore to check if the restaurant was entered correctly
        Query query = new Query("RestaurantInfo")
                .setFilter(new Query.FilterPredicate(
                        "name", Query.FilterOperator.EQUAL, name));
        PreparedQuery results = datastoreService.prepare(query);
        Entity resultEntity = results.asSingleEntity();

        assertEquals(name, resultEntity.getProperty("name"));
        assertEquals("[Steakhouse,  American]", resultEntity.getProperty("cuisine").toString());
        assertEquals(story, resultEntity.getProperty("story"));
        assertEquals(phone, resultEntity.getProperty("phone"));
        assertEquals(website, resultEntity.getProperty("website"));
    }

    /**
     * Test the doPost method of RestaurantServlet with a user who is not logged in
     * to ensure it will throw an exception.
     * @throws IOException
     * @throws SAXException if the ServletRunner parser cannot handle the request
     */
    @Test(expected = HttpException.class)
    public void testDoPostLoginFailure() throws IOException, SAXException {
        String name =  "Wildfire";
        String story = "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.";
        String cuisineList = "Steakhouse, American";
        String phone = "8472797900";
        String website = "https://www.wildfirerestaurant.com";

        // User is not logged in
        helper.setEnvIsAdmin(false).setEnvIsLoggedIn(false);

        // Create a client to run the servlet
        ServletRunner sr = new ServletRunner();
        sr.registerServlet("restaurant", RestaurantServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // Build request as normal
        WebRequest request = new PostMethodWebRequest("http://localhost:8080/restaurant");
        request.setParameter("name", name);
        request.setParameter("cuisine", cuisineList);
        request.setParameter("story", story);
        request.setParameter("phone", phone);
        request.setParameter("website", website);

        // This getResponse should cause a failing status because the user is not logged in.
        sc.getResponse(request);
    }

}
