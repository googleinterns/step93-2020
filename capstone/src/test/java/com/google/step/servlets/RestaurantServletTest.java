package com.google.step.servlets;

import static org.junit.Assert.*;

import com.google.appengine.api.datastore.*;

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
import java.util.List;

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
        Entity restaurantInfo1 = new Entity("RestaurantInfo");
        restaurantInfo1.setProperty("restaurantKey", 4);
        restaurantInfo1.setProperty("name", "Wildfire");
        restaurantInfo1.setProperty("location", new GeoPt((float) 42.17844, (float) -87.92843));
        restaurantInfo1.setProperty(
                "story", "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.");
        List<String> cuisineList1 = Arrays.asList("Steakhouse", "American");
        restaurantInfo1.setProperty("cuisine", cuisineList1);
        restaurantInfo1.setProperty("phone", "(847)279-7900");
        restaurantInfo1.setProperty("website",
                "https://www.wildfirerestaurant.com");
        restaurantInfo1.setProperty("score", 1.1);
        restaurantInfo1.setProperty("status", "STRUGGLING");
        datastoreService.put(restaurantInfo1);

        // Set expected JSON response based on props
        String expected = "{\"restaurant\":\"{\\\"value\\\":{\\\"restaurantKey\\\":4,\\\"name\\\":\\\"Wildfire\\\"," +
                "\\\"location\\\":{\\\"latitude\\\":42.17844,\\\"longitude\\\":-87.92843},\\\"story\\\":\\\"" +
                "Swanky American chain serving steak, chops \\\\u0026 seafood, plus burgers, sides \\\\u0026 cocktails.\\\"," +
                "\\\"cuisine\\\":[\\\"Steakhouse\\\",\\\"American\\\"],\\\"phone\\\":\\\"(847)279-7900\\\",\\\"website\\\":\\\"" +
                "https://www.wildfirerestaurant.com\\\",\\\"status\\\":\\\"STRUGGLING\\\"}}\"}\n";

        // Create a client to run the servlet
        ServletRunner sr = new ServletRunner();
        sr.registerServlet("restaurant", RestaurantServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // This can throw the SAXException.
        WebRequest request = new GetMethodWebRequest("http://localhost:8080/restaurant?restaurantKey=4");
        WebResponse response = sc.getResponse(request);
        assertNotNull("No response received", response);
        assertEquals("content type", "application/json", response.getContentType());
        String actual = response.getText();

        assertEquals(expected, actual);
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

        // This getResponse should throw an exception because the user is not logged in.
        sc.getResponse(request);
    }
}
