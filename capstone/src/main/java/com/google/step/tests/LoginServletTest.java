package com.google.step.tests;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;
import com.google.step.servlets.LoginServlet;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;

import com.meterware.servletunit.ServletUnitClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.xml.sax.SAXException;

import java.io.IOException;



public class LoginServletTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    // Run this test twice to prove we're not leaking any state across tests
    public void checkIfRestaurantOrUserTest() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        assertEquals(0, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
        datastoreService.put(new Entity("User"));
        datastoreService.put(new Entity("User"));
        assertEquals(2, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
    }

    @Test
    public void testInsert1() {
        checkIfRestaurantOrUserTest();
    }

    @Test
    public void testInsert2() {
        checkIfRestaurantOrUserTest();
    }

    @Test
    public void testDoGet() throws IOException, SAXException {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity("User");
        entity.setProperty("email", "mheberling@google.com");
        entity.setProperty("userName", "Martin");
        datastoreService.put(entity);

        ServletRunner sr = new ServletRunner();
        // The first parameter has to be the same as whatever goes after the '/' in the urlString for the GetMethodWebRequest
        // so for example if the first parameter is 'restaurant' then the link would be: 'http://localhost:8080/restaurant'
        // even though the actual servlet may be called something else.
        sr.registerServlet("login", LoginServlet.class.getName());
        ServletUnitClient sc = sr.newClient();

        // This can throw the SAXException.
        WebRequest request = new GetMethodWebRequest("http://localhost:8080/login");
        WebResponse response = sc.getResponse(request);
        assertNotNull("No response received", response);
        assertEquals("content type", "application/json", response.getContentType());

        // Turn content to JSon
        JsonObject jsonObject = turnStringToJson(response.getText());

        assertNotNull("Return Json", response.getText());
        assertEquals("Json Loggedin", "false", jsonObject.get("Loggedin").toString());

        // Can't check for the link to be correct, but we can check that it is not null.
        assertNotNull("Login URL", jsonObject.get("loginUrl").toString());
        assertNotNull("User Sign Up URL", jsonObject.get("userSignUpUrl").toString());
        assertNotNull("Restaurant Sign Up URL", jsonObject.get("restaurantSignUpUrl").toString());
    }

    /**
     * Turns String into a json object
     * @param json String to be turned to json
     * @return JsonObject corresponding to the string.
     */
    private static JsonObject turnStringToJson(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        return jsonObject;
    }

}
