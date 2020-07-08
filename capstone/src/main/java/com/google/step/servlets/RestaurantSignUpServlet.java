package com.google.step.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that will put the restaurant information in Datastore.
 */
@WebServlet("/restaurantSignUp")
public class RestaurantSignUpServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // Only available if the user is logged in.
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    String id = userService.getCurrentUser().getUserId();
    String email = userService.getCurrentUser().getEmail();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // TODO: add a check if the email already exists as a user
    // If the restaurant email already exists in datastore don't add it.
    Query query =
        new Query("RestaurantUser")
            .setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity resultEntity = results.asSingleEntity();
    if (resultEntity != null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // Add current user to RestaurantUser entity.
    Entity restaurantUser = new Entity("RestaurantUser", id);
    restaurantUser.setProperty("restaurantKey", id);
    restaurantUser.setProperty("email", email);
    datastore.put(restaurantUser);

    String name = request.getParameter("name");

    // Split the cuisine into a list
    String cuisineString = request.getParameter("cuisine");
    List<String> cuisineList = Arrays.asList(cuisineString.split(","));

    String story = request.getParameter("story");

    // Make sure its only numbers
    String phone = request.getParameter("phone");
    phone = phone.replaceAll("^[\\p{IsDigit}]", "");

    // TODO: Make sure its a link.
    String website = request.getParameter("website");

    // Hard coded while we don't have Maps API functionality
    GeoPt geoPoint = new GeoPt((float) 42.23422, (float) -87.234987);

    Entity restaurantInfo = new Entity("RestaurantInfo", id);
    restaurantInfo.setProperty("restaurantKey", id);
    restaurantInfo.setProperty("name", name);
    restaurantInfo.setProperty("location", geoPoint);
    restaurantInfo.setProperty("story", story);
    restaurantInfo.setProperty("cuisine", cuisineList);
    restaurantInfo.setProperty("phone", phone);
    restaurantInfo.setProperty("website", website);

    // Both of the following values are hardcoded while we implement the properties.
    restaurantInfo.setProperty("score", 2.5);
    restaurantInfo.setProperty("status", "OKAY");

    datastore.put(restaurantInfo);

    response.sendRedirect("/index.html");
  }
}
