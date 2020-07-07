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

package com.google.step.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Class with a main method script to seed any data desired. */
public class SeedData {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

/**
 * Seeds desired data. Change any method calls or add new data types to use the script
 * for different types of testing when needed.
 */
  public static void main(String[] args) {
    seedRestaurantInfo(0, "McDonald's", new GeoPt((float) 42.297522, (float) -87.956039), "We're a global business with the best french fries around.", "American,Fast food", "(847)362-3040", "https://www.mcdonalds.com/us/en-us/location/il/libertyville/1330-n-milwaukee-ave/119.html?cid=RF:YXT:GMB::Clicks", "GOOD", 3.5);
    seedRestaurantInfo(1, "Casa Bonita", new GeoPt((float) 42.289267, (float) -87.954920), "Classic Mexican eats & over 150 tequilas in a colorful, fiesta setting with a heated patio."," Mexican", "(847)362-4400", "https://www.casabonitalibertyville.com","OKAY", 2.5);
    seedRestaurantInfo(2, "Lauretta's", new GeoPt((float) 42.2532875, (float) -88.0003111), "Quaint cafe & bakeshop whipping up Italian favorites, cakes & cookies in a snug space with a patio.","Italian,Cafe,Bakery","(847)566-0883","https://www.laurettasbakeshop.info","STRUGGLING", 2.0);
    seedRestaurantInfo(3, "Thai Noodles Cafe", new GeoPt((float) 42.2803482, (float) -87.9528155), "Relaxed Thai eatery serving traditional dishes in a quaint setting inside a converted house.","Thai","(847)362-3494","https://www.easyordering.com/local/thainoodlecafe","OKAY",2.3);
    seedRestaurantInfo(4, "Wildfire", new GeoPt((float) 42.1784405, (float) -87.9284299), "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.","Steakhouse,American","(847)279-7900","https://www.wildfirerestaurant.com","STRUGGLING", 1.1);

    seedRestaurantUser(0, "mcdonalds@gmail.com");
    seedRestaurantUser(1, "casabonita@gmail.com");
    seedRestaurantUser(2, "laurettas@gmail.com");
    seedRestaurantUser(3, "thainoodles@gmail.com");
    seedRestaurantUser(4, "wildfire@gmail.com");
  }

/**
 * Creates a RestaurantInfo Entity and puts it in Datastore.
 */
  private static void seedRestaurantInfo(long restaurantKey, String name, GeoPt location, String story,
      String cuisines, String phone, String website, String status, double score) {
        Entity restaurantInfo = new Entity("RestaurantInfo");
        restaurantInfo.setProperty("restaurantKey", restaurantKey);
        restaurantInfo.setProperty("name", name);
        restaurantInfo.setProperty("location", location);
        restaurantInfo.setProperty("story", story);
        List<String> cuisineList = Arrays.asList(cuisines.split(","));
        restaurantInfo.setProperty("cuisine", cuisineList);
        restaurantInfo.setProperty("phone", "(847)362-3040");
        restaurantInfo.setProperty("website", website);
        restaurantInfo.setProperty("score", score);
        restaurantInfo.setProperty("status", status);
        datastore.put(restaurantInfo);
  }

/**
 * Creates a RestaurantUser Entity and puts it in Datastore.
 */
  private static void seedRestaurantUser(long restaurantKey, String email) {
    Entity restaurantUser = new Entity("RestaurantUser");
    restaurantUser.setProperty("restaurantKey", restaurantKey);
    restaurantUser.setProperty("email", email);
    datastore.put(restaurantUser);
  }
}