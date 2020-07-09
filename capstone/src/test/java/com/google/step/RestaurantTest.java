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

package com.google.step;

import java.util.ArrayList;
import java.util.List;
import com.google.appengine.api.datastore.Entity;
import com.google.step.data.Restaurant;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.lang.IllegalArgumentException;

/** Test class for the Restaurant.java class */
@RunWith(JUnit4.class)
public final class RestaurantTest {
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

/**
 * Test that the factory method returns null when
 * given the wrong entity type.
 */
  @Test(expected = IllegalArgumentException.class)
  public void incorrectEntityKind() {
    Entity wrongKind = new Entity("RestaurantUser");
    Restaurant exception = Restaurant.fromEntity(wrongKind);
  }

/**
 * Test the general correct case for the factory
 * method returning a Restaurant based on a 
 * RestaurantInfo entity.
 */
  @Test
  public void correctEntityKind() {
    Entity restaurantInfo = new Entity("RestaurantInfo");
    restaurantInfo.setProperty("restaurantKey", (long)0);
    restaurantInfo.setProperty("name", "McDonald's");
    restaurantInfo.setProperty("location", new GeoPt((float)42.297522, (float)-87.956039));
    restaurantInfo.setProperty("story", "We're a global business with the best french fries around.");
    List<String> cuisineList = new ArrayList<>();
    cuisineList.add("American");
    cuisineList.add("Fast food");
    restaurantInfo.setProperty("cuisine", cuisineList);
    restaurantInfo.setProperty("phone", "(847)362-3040");
    restaurantInfo.setProperty("website", "https://www.mcdonalds.com/us/en-us/location/il/libertyville/1330-n-milwaukee-ave/119.html?cid=RF:YXT:GMB::Clicks");
    restaurantInfo.setProperty("score", 3.5);
    restaurantInfo.setProperty("status", "GOOD");

    Restaurant actual = Restaurant.fromEntity(restaurantInfo);
    Restaurant expected = new Restaurant(0, "McDonald's", new GeoPt((float)42.297522, (float)-87.956039), 
        "We're a global business with the best french fries around.", cuisineList, "(847)362-3040", 
        "https://www.mcdonalds.com/us/en-us/location/il/libertyville/1330-n-milwaukee-ave/119.html?cid=RF:YXT:GMB::Clicks", "GOOD");

    Assert.assertEquals(expected, actual);
  }
}
