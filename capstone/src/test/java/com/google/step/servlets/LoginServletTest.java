package com.google.step.servlets;

import static org.junit.Assert.*;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class LoginServletTest {
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoGetWithoutLogIn() throws IOException, SAXException {
        // Testing doGet method without having the user logged in.
        // Result: {"LoggedIn":"false","loginURL":"someURL"}

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
        assertEquals("Json Loggedin", "false", jsonObject.get("LoggedIn").toString());

        UserService userService = UserServiceFactory.getUserService();
        assertEquals("Login URL", "\"" + userService.createLoginURL("/") + "\"",
                jsonObject.get("LoginURL".toString()).toString());
    }

    @Test
    public void testDoGetWithLogIn() throws IOException, SAXException {
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

        assertEquals("LoggedIn", "true", jsonObject.get("LoggedIn").toString());

        // Have to add the extra quotes in the expected because for some reason the JSON Parser adds
        // them again.
        assertEquals("Email", "\"example@gmail.com\"", jsonObject.get("Email").toString());

        UserService userService = UserServiceFactory.getUserService();
        assertEquals("Log out URL", "\"" + userService.createLogoutURL("/") + "\"",
                jsonObject.get("LogOutURL").toString());
    }

    /**
     * Turns String into a json object
     * @param json String to be turned to json
     * @return JsonObject corresponding to the string.
     */
    private static JsonObject turnStringToJson(String json) {
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        return jsonObject;
    }
}
