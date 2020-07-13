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

package com.google.step.clients;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.*;
import com.google.step.data.Restaurant;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.IllegalArgumentException;
import java.util.NoSuchElementException;

/** Test class for the Restaurant.java class */
@RunWith(JUnit4.class)
public final class RestaurantClientTest {
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

  /** General datastore tests to ensure mock works correctly */
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

  /**
   * Test the getSingleRestaurant method for the RestaurantClient.
   * Insert an entity into the mock Datastore and retrieve it by key.
   */
  @Test
  public void testGetSingleRestaurant() {
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    // Set random params for a restaurant
    String name = "Wildfire";
    GeoPt location = new GeoPt((float) 42.17844, (float) -87.92843);
    List<String> cuisineList1 = new ArrayList<>();
    cuisineList1.add("Steakhouse");
    cuisineList1.add("American");
    String story = "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.";
    String phone = "(847)279-7900";
    String website = "https://www.wildfirerestaurant.com";
    long score = (long)1.1;
    String status = "STRUGGLING";
    // Put restaurant in the mock datastore service
    Entity returned = putRestaurantInfoEntityWithVals(datastoreService, name, location, story, cuisineList1, phone, website, score, status);

    // Get restaurant key from entity that was put into Datastore, as it was assigned by Datastore
    long restaurantKey = (long)returned.getProperty("restaurantKey");

    // Run client method
    RestaurantClient restaurantClient = new RestaurantClient();
    Restaurant result = restaurantClient.getSingleRestaurant(restaurantKey);
    assertNotNull("No response received", result);

    Restaurant expected = new Restaurant(name, location, story, cuisineList1, phone, website, status);
    expected.setRestaurantKey(restaurantKey);

    assertEquals(expected, result);
  }

  /**
   * Test the getRestaurantsNoFilter method for the RestaurantClient.
   * Insert two entities into the mock Datastore and retrieve them in a list.
   */
  @Test
  public void testGetRestaurantsNoFilter() {
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    // Set random params for two different restaurants
    // Put them into the mock datastore service
    String name = "Wildfire";
    GeoPt location = new GeoPt((float) 42.17844, (float) -87.92843);
    List<String> cuisineList1 = new ArrayList<>();
    cuisineList1.add("Steakhouse");
    cuisineList1.add("American");
    String story = "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.";
    String phone = "(847)279-7900";
    String website = "https://www.wildfirerestaurant.com";
    long score = (long)1.1;
    String status = "STRUGGLING";
    Entity returned1 = putRestaurantInfoEntityWithVals(datastoreService, name, location, story, cuisineList1, phone, website, score, status);

    String name2 = "Burger King";
    GeoPt location2 = new GeoPt((float) 42.17844, (float) -87.92843);
    List<String> cuisineList2 = new ArrayList<>();
    cuisineList2.add("Fast food");
    cuisineList2.add("American");
    String story2 = "Fast food burger chain.";
    String phone2 = "(847)279-7911";
    String website2 = "https://www.burgerking.com";
    long score2 = (long)2.5;
    String status2 = "OKAY";
    Entity returned2 = putRestaurantInfoEntityWithVals(datastoreService, name2, location2, story2, cuisineList2, phone2, website2, score2, status2);

    long restaurantKey = (long)returned1.getProperty("restaurantKey");
    long restaurantKey2 = (long)returned2.getProperty("restaurantKey");

    // Run client method
    RestaurantClient restaurantClient = new RestaurantClient();
    List<Restaurant> result = restaurantClient.getRestaurantsNoFilter();
    assertNotNull("No response received", result);

    // Create expected list
    // TODO: make expected list be of RestaurantHeaders.
    Restaurant rest1 = new Restaurant(name, location, story, cuisineList1, phone, website, status);
    rest1.setRestaurantKey(restaurantKey);
    Restaurant rest2 = new Restaurant(name2, location2, story2, cuisineList2, phone2, website2, status2);
    rest2.setRestaurantKey(restaurantKey2);
    List<Restaurant> expected = new ArrayList<>();
    expected.add(rest1);
    expected.add(rest2);

    assertEquals(expected, result);
  }

  /**
   * Test the putRestaurant method for the RestaurantClient.
   * Insert an entity using the putRestaurant method and retrieve it.
   */
  @Test
  public void testPutRestaurant() {
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    // Set random params for a restaurant
    String name = "Wildfire";
    GeoPt location = new GeoPt((float) 42.17844, (float) -87.92843);
    List<String> cuisineList1 = new ArrayList<>();
    cuisineList1.add("Steakhouse");
    cuisineList1.add("American");
    String story = "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.";
    String phone = "(847)279-7900";
    String website = "https://www.wildfirerestaurant.com";
    String email = "wildfire@gmail.com";
    String status = "STRUGGLING";
    Restaurant restaurant = new Restaurant(name, location, story, cuisineList1, phone, website, status);

    // Run client method
    RestaurantClient restaurantClient = new RestaurantClient();
    restaurantClient.putRestaurant(restaurant, email);

    // Run query for restaurant with this name
    Restaurant actual = getRestaurantForTest(datastoreService, name);

    // Set key for expected restaurant to whatever datastore assigned
    restaurant.setRestaurantKey(actual.getRestaurantKey());

    assertEquals(restaurant, actual);
  }

  /*
   * The following helper methods behave similarly to the putRestaurant
   * and getSingleRestaurant methods in RestaurantClient but have been
   * written here to separate out tests for each method individually.
   */

  /**
   * Helper to create and insert a RestaurantInfo Entity into a mock
   * datastore service based on all necessary params.
   */
  public static Entity putRestaurantInfoEntityWithVals(DatastoreService datastoreService, String name, GeoPt location, String story, List<String> cuisine, String phone, String website, long score, String status) {
    Entity restaurantInfo = new Entity("RestaurantInfo");
    long id = restaurantInfo.getKey().getId();
    restaurantInfo.setProperty("restaurantKey", id);
    restaurantInfo.setProperty("name", name);
    restaurantInfo.setProperty("location", location);
    restaurantInfo.setProperty("story", story);
    restaurantInfo.setProperty("cuisine", cuisine);
    restaurantInfo.setProperty("phone", phone);
    restaurantInfo.setProperty("website",website);
    restaurantInfo.setProperty("score", score);
    restaurantInfo.setProperty("status", status);
    datastoreService.put(restaurantInfo);
    return restaurantInfo;
  }

  /**
   * Helper to get a RestaurantInfo Entity from a mock
   * datastore service based on its name (since restaurantKey
   * is unknown until retrieved from datastore).
   */
  public static Restaurant getRestaurantForTest(DatastoreService datastoreService, String name) {
    Query query = new Query("RestaurantInfo")
            .setFilter(new Query.FilterPredicate(
                    "name", Query.FilterOperator.EQUAL, name));
    PreparedQuery results = datastoreService.prepare(query);
    Entity entity = results.asSingleEntity();

    long restaurantKey = (Long) entity.getProperty("restaurantKey");
    GeoPt location = (GeoPt) entity.getProperty("location");
    String story = (String) entity.getProperty("story");
    List<String> cuisine = (List<String>) entity.getProperty("cuisine");
    String phone = (String) entity.getProperty("phone");
    String website = (String) entity.getProperty("website");
    String status = (String) entity.getProperty("status");

    Restaurant result = new Restaurant(name, location, story, cuisine, phone, website, status);
    result.setRestaurantKey(restaurantKey);

    // Return Restaurant object holding all info
    return result;
  }

}
