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

import com.google.appengine.api.datastore.GeoPt;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public final class RestaurantHeaderTest {
  private final long KEY_1 = 12345L;
  private final long KEY_2 = 1111L;

  private final String NAME_1 = "The Goog Noodle";
  private final String NAME_2 = "The Statue";

  private final GeoPt LOC_1 = new GeoPt(37.4220621f,-122.0862784f);
  private final GeoPt LOC_2 = new GeoPt(40.6892494f,-74.0445004f);

  private final List<String> CUISINE_1 = Collections.unmodifiableList(Arrays.asList("pizza", "American"));
  private final List<String> CUISINE_2 = Collections.singletonList("Indian");

  private final Restaurant RESTAURANT_1 = new Restaurant(KEY_1, NAME_1, LOC_1, "", CUISINE_1, "", "", "");
  private final Restaurant RESTAURANT_2 = new Restaurant(KEY_2, NAME_2, LOC_2, "", CUISINE_2, "", "", "");

  @Test
  public void testLikeRestaurantHeadersEqual() {
    RestaurantHeader restaurantHeader1 = RestaurantHeader.createHeaderFromRestaurant(RESTAURANT_1);
    RestaurantHeader restaurantHeader2 = RestaurantHeader.createHeaderFromRestaurant(RESTAURANT_1);

    assertEquals(restaurantHeader1, restaurantHeader2);
  }

  @Test
  public void testDifferentRestaurantHeadersNotEqual() {
    RestaurantHeader restaurantHeader1 = RestaurantHeader.createHeaderFromRestaurant(RESTAURANT_1);
    RestaurantHeader restaurantHeader2 = RestaurantHeader.createHeaderFromRestaurant(RESTAURANT_2);

    assertNotEquals(restaurantHeader1, restaurantHeader2);
  }

  @Test
  public void testFieldsSetCorrectly() {
    RestaurantHeader restaurantHeader = RestaurantHeader.createHeaderFromRestaurant(RESTAURANT_1);

    assertEquals(KEY_1, restaurantHeader.getRestaurantKey());
    assertEquals(NAME_1, restaurantHeader.getName());
    assertEquals(LOC_1, restaurantHeader.getLocation());
    assertEquals(CUISINE_1, restaurantHeader.getCuisine());
    assertEquals(0.5, restaurantHeader.getMetricsScore(), 0.001);
  }

  @Test
  public void testRestaurantHeadersWithDifferentScoresStillEqual() {
    RestaurantHeader restaurantHeader = RestaurantHeader.createHeaderFromRestaurant(RESTAURANT_1);
    RestaurantHeader restaurantHeaderWithDifferentScore =
        new RestaurantHeader(KEY_1, NAME_1, LOC_1, CUISINE_1, Optional.of(0.2));

    assertEquals(restaurantHeader, restaurantHeaderWithDifferentScore);
  }
}
