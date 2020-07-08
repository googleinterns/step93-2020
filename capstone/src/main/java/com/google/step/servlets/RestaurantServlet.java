package com.google.step.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

/** Servlet responsible for sending/getting a restaurant to/from Datastore. */
@WebServlet("/restaurant")
public class RestaurantServlet extends HttpServlet {

  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Will post a new restaurant into datastore.
   * @param request sent by the frontend.
   * @param response to the frontend.
   * @throws IOException
   */
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

    String name = request.getParameter("name");

    // Split the cuisine into a list
    String cuisineString = request.getParameter("cuisine");
    List<String> cuisineList = Arrays.asList(cuisineString.split(","));

    String story = request.getParameter("story");

    // Make sure its only numbers
    String phone = request.getParameter("phone");
    phone = changeNonNumericCharacters(phone);

    // TODO: Make sure its a link.
    String website = request.getParameter("website");

    // Hard coded while we don't have Maps API functionality
    GeoPt geoPoint = new GeoPt((float) 42.23422, (float) -87.234987);

    Entity restaurantInfo = new Entity("RestaurantInfo", id);
    restaurantInfo.setProperty("restaurantKey", id);
    restaurantInfo.setProperty("name", name);
    restaurantInfo.setProperty("email", email);
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

  private String changeNonNumericCharacters(String num) {
    return num.replaceAll("[^0-9]", "");
  }
}
