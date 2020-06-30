// Copyright 2019 Google LLC
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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
// import com.google.sps.data.Status;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that seeds some example content to datastore. */
@WebServlet("/seedData")
public class SeedDataServlet extends HttpServlet { 
  enum Status 
{ 
  STRUGGLING(0), OKAY(1), GOOD(2);

  Status(int value) {
    this.value = value;
  } 

  private int value;

  public int getValue() {
    return value;
  }
}

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Create entity and load with data
    Entity restaurantInfo1 = new Entity("RestaurantInfo");
    restaurantInfo1.setProperty("restaurantKey", 0);
    restaurantInfo1.setProperty("name", "McDonald's");
    restaurantInfo1.setProperty("location", new GeoPt((float)42.297522, (float)-87.956039));
    restaurantInfo1.setProperty("story", "We're a global business with the best french fries around.");
    List<String> cuisineList1 = new ArrayList<>();
    cuisineList1.add("American");
    cuisineList1.add("Fast food");
    restaurantInfo1.setProperty("cuisine", cuisineList1);
    restaurantInfo1.setProperty("phone", "(847)362-3040");
    restaurantInfo1.setProperty("website", "https://www.mcdonalds.com/us/en-us/location/il/libertyville/1330-n-milwaukee-ave/119.html?cid=RF:YXT:GMB::Clicks");
    restaurantInfo1.setProperty("score", 3.5);
    restaurantInfo1.setProperty("status", Status.GOOD.getValue());

    Entity restaurantUser1 = new Entity("RestaurantUser");
    restaurantUser1.setProperty("restaurantKey", 0);
    restaurantUser1.setProperty("email", "mcdonalds@gmail.com");

    // Put entities into Datastore
    datastore.put(restaurantInfo1);
    datastore.put(restaurantUser1);

    // Create entity and load with data
    Entity restaurantInfo2 = new Entity("RestaurantInfo");
    restaurantInfo2.setProperty("restaurantKey", 1);
    restaurantInfo2.setProperty("name", "Casa Bonita");
    restaurantInfo2.setProperty("location", new GeoPt((float)42.289267, (float)-87.954920));
    restaurantInfo2.setProperty("story", "Classic Mexican eats & over 150 tequilas in a colorful, fiesta setting with a heated patio.");
    List<String> cuisineList2 = new ArrayList<>();
    cuisineList2.add("Mexican");
    restaurantInfo2.setProperty("cuisine", cuisineList2);
    restaurantInfo2.setProperty("phone", "(847)362-4400");
    restaurantInfo2.setProperty("website", "https://www.casabonitalibertyville.com");
    restaurantInfo2.setProperty("score", 2.5);
    restaurantInfo2.setProperty("status", Status.OKAY.getValue());

    Entity restaurantUser2 = new Entity("RestaurantUser");
    restaurantUser2.setProperty("restaurantKey", 1);
    restaurantUser2.setProperty("email", "casabonita@gmail.com");

    // Put entities into Datastore
    datastore.put(restaurantInfo2);
    datastore.put(restaurantUser2);

    // Create entity and load with data
    Entity restaurantInfo3 = new Entity("RestaurantInfo");
    restaurantInfo3.setProperty("restaurantKey", 2);
    restaurantInfo3.setProperty("name", "Lauretta's");
    restaurantInfo3.setProperty("location", new GeoPt((float)42.2532875, (float)-88.0003111));
    restaurantInfo3.setProperty("story", "Quaint cafe & bakeshop whipping up Italian favorites, cakes & cookies in a snug space with a patio.");
    List<String> cuisineList3 = new ArrayList<>();
    cuisineList3.add("Italian");
    cuisineList3.add("Cafe");
    cuisineList3.add("Bakery");
    restaurantInfo3.setProperty("cuisine", cuisineList3);
    restaurantInfo3.setProperty("phone", "(847)566-0883");
    restaurantInfo3.setProperty("website", "https://www.laurettasbakeshop.info");
    restaurantInfo3.setProperty("score", 1.5);
    restaurantInfo3.setProperty("status", Status.STRUGGLING.getValue());

    Entity restaurantUser3 = new Entity("RestaurantUser");
    restaurantUser3.setProperty("restaurantKey", 2);
    restaurantUser3.setProperty("email", "laurettas@gmail.com");

    // Put entities into Datastore
    datastore.put(restaurantInfo3);
    datastore.put(restaurantUser3);

    // Create entity and load with data
    Entity restaurantInfo4 = new Entity("RestaurantInfo");
    restaurantInfo4.setProperty("restaurantKey", 3);
    restaurantInfo4.setProperty("name", "Thai Noodles Cafe");
    restaurantInfo4.setProperty("location", new GeoPt((float)42.2803482, (float)-87.9528155));
    restaurantInfo4.setProperty("story", "Relaxed Thai eatery serving traditional dishes in a quaint setting inside a converted house.");
    List<String> cuisineList4 = new ArrayList<>();
    cuisineList4.add("Thai");
    restaurantInfo4.setProperty("cuisine", cuisineList4);
    restaurantInfo4.setProperty("phone", "(847)362-3494");
    restaurantInfo4.setProperty("website", "https://www.easyordering.com/local/thainoodlecafe");
    restaurantInfo4.setProperty("score", 2.3);
    restaurantInfo4.setProperty("status", Status.OKAY.getValue());

    Entity restaurantUser4 = new Entity("RestaurantUser");
    restaurantUser4.setProperty("restaurantKey", 3);
    restaurantUser4.setProperty("email", "thainoodles@gmail.com");

    // Put entities into Datastore
    datastore.put(restaurantInfo4);
    datastore.put(restaurantUser4);

    // Create entity and load with data
    Entity restaurantInfo5 = new Entity("RestaurantInfo");
    restaurantInfo5.setProperty("restaurantKey", 4);
    restaurantInfo5.setProperty("name", "Wildfire");
    restaurantInfo5.setProperty("location", new GeoPt((float)42.1784405, (float)-87.9284299));
    restaurantInfo5.setProperty("story", "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.");
    List<String> cuisineList5 = new ArrayList<>();
    cuisineList5.add("Steakhouse");
    cuisineList5.add("American");
    restaurantInfo5.setProperty("cuisine", cuisineList5);
    restaurantInfo5.setProperty("phone", "(847)279-7900");
    restaurantInfo5.setProperty("website", "https://www.wildfirerestaurant.com");
    restaurantInfo5.setProperty("score", 1.1);
    restaurantInfo5.setProperty("status", Status.STRUGGLING.getValue());

    Entity restaurantUser5 = new Entity("RestaurantUser");
    restaurantUser5.setProperty("restaurantKey", 4);
    restaurantUser5.setProperty("email", "wildfire@gmail.com");

    // Put entities into Datastore
    datastore.put(restaurantInfo5);
    datastore.put(restaurantUser5);
  }
}
