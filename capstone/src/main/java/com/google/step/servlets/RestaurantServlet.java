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

package com.google.step.servlets;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.step.clients.RestaurantClient;
import com.google.step.data.Restaurant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for sending/getting a restaurant to/from Datastore. */
@WebServlet("/restaurant")
public class RestaurantServlet extends HttpServlet {
  private RestaurantClient restaurantClient = new RestaurantClient();

  /**
   * Will put the information gathered from the signup page and add the
   * information into their respective properties for .
   * @param request sent by the frontend. Requires the user to be logged in
   *                and have the following parameters in the request body:
   *                "name": name of the restaurant,
   *                "cuisine": list of cuisines of the restaurant as CSV,
   *                "story": story of the restaurant,
   *                "phone": cellphone of the restaurant,
   *                "website": link to their website,
   * @param response will redirect to the home page of the website. If the
   *                 user is not logged in when requesting the POST, the
   *                 response will have a SC_FORBIDDEN status.
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

    // The following value is hardcoded while we implement the properties.
    String status = "OKAY";

    // Dummy value as id will be set in the client using the Entity's id.
    long id = 0;

    Restaurant restaurant =
        new Restaurant(null, name, geoPoint, story, cuisineList, phone, website, status);

    restaurantClient.putRestaurant(restaurant, email);

    response.sendRedirect("/index.html");
  }

  private String changeNonNumericCharacters(String num) {
    return num.replaceAll("[^0-9]", "");
  }

  /**
   * Returns a single Restaurant based on restaurantKey.
   * @param response All details for the requested restaurant, in the following json format:
    {
      "restaurant" : {
        "restaurantKey": long,
        "name": String,
        "location": GeoPt,
        "story": String,
        "cuisine": List<String>,
        "phone": String,
        "website": String,
        "status": String
      }
    }
   * @param request Specifies restaurant key, in the following format:
     /restaurant?restaurantKey=int
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long restaurantKeyParam = Long.parseLong(request.getParameter("restaurantKey"));

    // Restaurant object to hold all info
    Optional<Restaurant> restaurant = restaurantClient.getSingleRestaurant(restaurantKeyParam);

    if (restaurant.isPresent()) {
      // Format restaurant List to JSON for return
      Gson gson = new Gson();
      String json = gson.toJson(restaurant);

      // Send the JSON as the response
      response.setContentType("application/json;");

      JsonObject ret = new JsonObject();
      ret.addProperty("restaurant", json);
      response.getWriter().println(ret);
    }
    else {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
