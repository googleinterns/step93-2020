package com.google.step.tests;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.step.servlets.LoginServlet;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import java.io.IOException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;

public class LoginServletTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  // Run this test twice to prove we're not leaking any state across tests
  public void checkIfDatastoreWorks() {
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    assertEquals(0, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
    datastoreService.put(new Entity("User"));
    datastoreService.put(new Entity("User"));
    assertEquals(2, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
  }

  @Test
  public void testCheckType1() {
    checkIfDatastoreWorks();
  }

  @Test
  public void testCheckType2() {
    checkIfDatastoreWorks();
  }

  @Test
  public void testIsLoggedIn() {
    // Check if this works to set the user as logged in and not the admin
    helper.setEnvIsAdmin(false).setEnvIsLoggedIn(true);
    UserService userService = UserServiceFactory.getUserService();
    assertTrue(userService.isUserLoggedIn());
    assertFalse(userService.isUserAdmin());
  }

  @Test
  public void testIsNotLoggedIn() {
    // Check if it works to show the user is not logged in
    helper.setEnvIsAdmin(false).setEnvIsLoggedIn(false);
    UserService userService = UserServiceFactory.getUserService();
    assertFalse(userService.isUserLoggedIn());
    // Can't also check if the user is an admin because the method will throw an error since the
    // user is not even logged in.
  }

  @Test
  public void testDoGetWithoutLogIn() throws IOException, SAXException {
    // Testing doGet method without having the user logged in.
    // Result:
    // {"Loggedin":"false","loginUrl":"someUrl","userSignUpUrl":"someUrl,restaurantSinUpUrl":"someUrl"}
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("User");
    entity.setProperty("email", "mheberling@google.com");
    entity.setProperty("userName", "Martin");
    datastoreService.put(entity);

    ServletRunner sr = new ServletRunner();
    // The first parameter has to be the same as whatever goes after the '/' in the urlString for
    // the GetMethodWebRequest so for example if the first parameter is 'restaurant' then the link
    // would be: 'http://localhost:8080/restaurant' even though the actual servlet may be called
    // something else.
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

  // Test that runs the doGet with different types of logged in users.
  public void testDoGetWithTypeLogIn(String type) throws IOException, SAXException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Entity user = new Entity(type);
    user.setProperty("email", "example@gmail.com");
    ds.put(user);

    // If you set an email you must also set the auth domain.
    helper.setEnvIsLoggedIn(true)
        .setEnvIsAdmin(false)
        .setEnvEmail("example@gmail.com")
        .setEnvAuthDomain("example@gmail.com");

    ServletRunner sr = new ServletRunner();

    sr.registerServlet("login", LoginServlet.class.getName());
    ServletUnitClient sc = sr.newClient();

    WebRequest request = new GetMethodWebRequest("http://localhost:8080/login");
    WebResponse response = sc.getResponse(request);

    assertNotNull("No response received", response);
    assertEquals("content type", "application/json", response.getContentType());

    JsonObject jsonObject = turnStringToJson(response.getText());

    assertEquals("Loggedin", "true", jsonObject.get("Loggedin").toString());

    // Have to add the extra quotes in the expected because for some reason the JSON Parser adds
    // them again.
    assertEquals("Type", "\"" + type + "\"", jsonObject.get("Type").toString());
    assertEquals("Email", "\"example@gmail.com\"", jsonObject.get("Email").toString());

    assertNotNull("Log out url", jsonObject.get("logOutUrl").toString());
  }

  @Test
  @Parameters
  public void testDoGetWithUserLogIn() throws IOException, SAXException {
    // Returns: {"Loggedin":"true","Type":"User", "Email":"example@gmail.com",
    // "logOutUrl":"someUrl"}
    testDoGetWithTypeLogIn("User");
  }

  @Test
  @Parameters
  public void testDoGetWithRestaurantLogIn() throws IOException, SAXException {
    // Returns: {"Loggedin":"true","Type":"Restaurant", "Email":"example@gmail.com",
    // "logOutUrl":"someUrl"}
    testDoGetWithTypeLogIn("Restaurant");
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
