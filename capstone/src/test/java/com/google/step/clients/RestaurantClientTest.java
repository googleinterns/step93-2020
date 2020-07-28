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

import java.util.*;

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

/** Test class for the Restaurant.java class */
@RunWith(JUnit4.class)
public final class RestaurantClientTest {
  private final LocalServiceTestHelper helper =
          new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private Entity restaurantEntity1;
  private Entity restaurantEntity2;

  @Before
  public void setUp() {
    helper.setUp();

    // Set random params for two different restaurants
    String name1 = "Wildfire";
    GeoPt location1 = new GeoPt((float) 42.17844, (float) -87.92843);
    List<String> cuisineList1 = Arrays.asList("Steakhouse", "American");
    String story1 = "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.";
    String phone1 = "(847)279-7900";
    String website1 = "https://www.wildfirerestaurant.com";
    long score1 = (long)1.1;
    String status1 = "STRUGGLING";
    restaurantEntity1 = makeRestaurantInfoEntityWithVals(name1, location1, story1, cuisineList1, phone1, website1, score1, status1);

    String name2 = "Burger King";
    GeoPt location2 = new GeoPt((float) 42.17844, (float) -87.92843);
    List<String> cuisineList2 = Arrays.asList("Fast food", "American");
    String story2 = "Fast food burger chain.";
    String phone2 = "(847)279-7911";
    String website2 = "https://www.burgerking.com";
    long score2 = (long)2.5;
    String status2 = "OKAY";
    restaurantEntity2 = makeRestaurantInfoEntityWithVals(name2, location2, story2, cuisineList2, phone2, website2, score2, status2);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /**
   * Test the getSingleRestaurant method for the RestaurantClient.
   * Insert an entity into the mock Datastore and retrieve it by key.
   */
  @Test
  public void testGetSingleRestaurant() {
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    // Put a restaurant Entity in the mock datastore service
    datastoreService.put(restaurantEntity1);

    // Get restaurant key from entity that was put into Datastore, as it was assigned by Datastore
    long restaurantKey = restaurantEntity1.getKey().getId();

    // Run client method
    RestaurantClient restaurantClient = new RestaurantClient();
    Optional<Restaurant> result = restaurantClient.getSingleRestaurant(restaurantKey);
    assertTrue(result.isPresent());
    Restaurant actual = result.get();

    List<String> cuisineList = Arrays.asList("Steakhouse", "American");
    Restaurant expected = new Restaurant(restaurantKey, "Wildfire", new GeoPt((float) 42.17844, (float) -87.92843),
            "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.",
            cuisineList, "(847)279-7900", "https://www.wildfirerestaurant.com", "STRUGGLING");

    assertEquals(expected, actual);
  }

  /**
   * Test the getRestaurantsNoFilter method for the RestaurantClient.
   * Insert two entities into the mock Datastore and retrieve them in a list.
   */
  @Test
  public void testGetRestaurantsNoFilter() {
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    // Put a restaurant Entities in the mock datastore service
    datastoreService.put(restaurantEntity1);
    datastoreService.put(restaurantEntity2);

    // Get restaurant keys from entities that were put into Datastore, as they were assigned by Datastore
    long restaurantKey1 = restaurantEntity1.getKey().getId();
    long restaurantKey2 = restaurantEntity2.getKey().getId();

    // Create expected list
    // TODO: make expected list be of RestaurantHeaders.

    List<String> cuisineList1 = Arrays.asList("Steakhouse", "American");
    Restaurant rest1 = new Restaurant(restaurantKey1, "Wildfire", new GeoPt((float) 42.17844, (float) -87.92843),
            "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.",
            cuisineList1, "(847)279-7900", "https://www.wildfirerestaurant.com", "STRUGGLING");

    List<String> cuisineList2 = Arrays.asList("Fast food", "American");
    Restaurant rest2 = new Restaurant(restaurantKey2, "Burger King", new GeoPt((float) 42.17844, (float) -87.92843),
            "Fast food burger chain.", cuisineList2, "(847)279-7911", "https://www.burgerking.com", "OKAY");

    List<Restaurant> expected = new ArrayList<>();
    expected.add(rest1);
    expected.add(rest2);

    // Run client method
    RestaurantClient restaurantClient = new RestaurantClient();
    List<Restaurant> result = restaurantClient.getRestaurantsNoFilter();
    assertNotNull("No response received", result);

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
    List<String> cuisineList1 = Arrays.asList("Steakhouse", "American");
    String story = "Swanky American chain serving steak, chops & seafood, plus burgers, sides & cocktails.";
    String phone = "(847)279-7900";
    String website = "https://www.wildfirerestaurant.com";
    String email = "wildfire@gmail.com";
    String status = "STRUGGLING";
    Restaurant restaurant = new Restaurant(null, name, location, story, cuisineList1, phone, website, status);

    // Run client method
    RestaurantClient restaurantClient = new RestaurantClient();
    restaurantClient.putRestaurant(restaurant, email);

    // Run query for restaurant with this name
    Restaurant actual = getRestaurantForTest(datastoreService, name);

    assertEquals(restaurant, actual);
  }

  /*
   * The following helper methods behave similarly to the putRestaurant
   * and getSingleRestaurant methods in RestaurantClient but have been
   * written here to separate out tests for each method individually.
   */

  /**
   * Helper to create a RestaurantInfo Entity based on all necessary params.
   */
  public static Entity makeRestaurantInfoEntityWithVals(String name, GeoPt location, String story, List<String> cuisine, String phone, String website, long score, String status) {
    Entity restaurantInfo = new Entity("RestaurantInfo");
    restaurantInfo.setProperty("name", name);
    restaurantInfo.setProperty("location", location);
    restaurantInfo.setProperty("story", story);
    restaurantInfo.setProperty("cuisine", cuisine);
    restaurantInfo.setProperty("phone", phone);
    restaurantInfo.setProperty("website",website);
    restaurantInfo.setProperty("score", score);
    restaurantInfo.setProperty("status", status);
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

    long restaurantKey = entity.getKey().getId();
    GeoPt location = (GeoPt) entity.getProperty("location");
    String story = (String) entity.getProperty("story");
    List<String> cuisine = (List<String>) entity.getProperty("cuisine");
    String phone = (String) entity.getProperty("phone");
    String website = (String) entity.getProperty("website");
    String status = (String) entity.getProperty("status");

    Restaurant result = new Restaurant(restaurantKey, name, location, story, cuisine, phone, website, status);

    // Return Restaurant object holding all info
    return result;
  }

}
