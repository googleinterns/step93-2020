package com.google.step.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;

/**
 * Servlet that will handle login information.
 */

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");

        UserService userService = UserServiceFactory.getUserService();
        Map<String, Object> login = new HashMap<>();

        if (userService.isUserLoggedIn()) {
            String useremail = userService.getCurrentUser().getEmail();
            String type = checkIfRestaurantOrUser(useremail);

            //If user is loggedin but not in our system.
            if (type == null) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String urltoRedirectToAfterUserLogsOut = "/";
            String logoutUrl = userService.createLogoutURL(urltoRedirectToAfterUserLogsOut);

            login.put("Loggedin", true);
            login.put("Type", type);
            login.put("Email", useremail);
            login.put("logOutUrl", logoutUrl);
        } else {

            String urltoRedirectToAfterUserLogIn = "/";
            String urltoRedirectAfterUserSignUp = "/user-signup.html";
            String urltoRedirectAfterRestaurantSignUp = "/restaurant-signup.html";

            String loginUrl = userService.createLoginURL(urltoRedirectToAfterUserLogIn);
            String userSignUpUrl = userService.createLoginURL(urltoRedirectAfterUserSignUp);
            String restaurantSignUpUrl = userService.createLoginURL(urltoRedirectAfterRestaurantSignUp);

            login.put("Loggedin", false);
            login.put("loginUrl", loginUrl);
            login.put("userSignUpUrl", userSignUpUrl);
            login.put("restaurantSignUpUrl", restaurantSignUpUrl);
        }

        String json = convertToJsonUsingGson(login);
        response.getWriter().println(json);
    }

    // TODO: Change to have object return and return User or Restaurant Object
    /**
     * Checks if the current email is from a user or a restaurant.
     * @param email of the current user.
     * @return String containing the words User or Restaurant.
     */
    private static String checkIfRestaurantOrUser(String email) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("User").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email));
        PreparedQuery results = datastore.prepare(query);
        Entity entity = results.asSingleEntity();
        if (entity != null) {
            return "User";
        } else {
            query = new Query("RestaurantUser").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email));
            if (entity != null) {
                return "Restaurant";
            } else {
                return null;
            }
        }
    }

    /**
     * Converts to Json using Gson.
     * @param login Map to convert to Gson.
     * @return String in JSON format.
     */
    private static String convertToJsonUsingGson(Map login) {
        Gson gson = new Gson();
        String json = gson.toJson(login);
        return json;
    }
}