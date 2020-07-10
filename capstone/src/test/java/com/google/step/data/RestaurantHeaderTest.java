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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public final class RestaurantHeaderTest {
  @Mock
  Restaurant RESTAURANT_MOCK_1;

  @Mock
  Restaurant RESTAURANT_MOCK_2;

  private final long KEY_1 = 12345L;
  private final long KEY_2 = 1111L;

  private final String NAME_1 = "The Goog Noodle";
  private final String NAME_2 = "The Statue";

  private final GeoPt LOC_1 = new GeoPt(37.4220621f,-122.0862784f);
  private final GeoPt LOC_2 = new GeoPt(40.6892494f,-74.0445004f);

  private final List<String> CUISINE_1 = Collections.unmodifiableList(Arrays.asList("pizza", "American"));
  private final List<String> CUISINE_2 = Collections.singletonList("Indian");

  @Before
  public void setUp() {
    Mockito.when(RESTAURANT_MOCK_1.getCuisine()).thenReturn(CUISINE_1);
    Mockito.when(RESTAURANT_MOCK_1.getLocation()).thenReturn(LOC_1);
    Mockito.when(RESTAURANT_MOCK_1.getName()).thenReturn(NAME_1);
    Mockito.when(RESTAURANT_MOCK_1.getRestaurantKey()).thenReturn(KEY_1);

    Mockito.when(RESTAURANT_MOCK_2.getCuisine()).thenReturn(CUISINE_2);
    Mockito.when(RESTAURANT_MOCK_2.getLocation()).thenReturn(LOC_2);
    Mockito.when(RESTAURANT_MOCK_2.getName()).thenReturn(NAME_2);
    Mockito.when(RESTAURANT_MOCK_2.getRestaurantKey()).thenReturn(KEY_2);
  }

  @Test
  public void testLikeRestaurantHeadersEqual() {
    RestaurantHeader restaurantHeader1 = RestaurantHeader.createRestaurantHeader(RESTAURANT_MOCK_1);
    RestaurantHeader restaurantHeader2 = RestaurantHeader.createRestaurantHeader(RESTAURANT_MOCK_1);

    assertEquals(restaurantHeader1, restaurantHeader2);
  }

  @Test
  public void testDifferentRestaurantHeadersNotEqual() {
    RestaurantHeader restaurantHeader1 = RestaurantHeader.createRestaurantHeader(RESTAURANT_MOCK_1);
    RestaurantHeader restaurantHeader2 = RestaurantHeader.createRestaurantHeader(RESTAURANT_MOCK_2);

    assertNotEquals(restaurantHeader1, restaurantHeader2);
  }

  @Test
  public void testFieldsSetCorrectly() {
    RestaurantHeader restaurantHeader = RestaurantHeader.createRestaurantHeader(RESTAURANT_MOCK_1);

    assertEquals(KEY_1, restaurantHeader.getRestaurantKey());
    assertEquals(NAME_1, restaurantHeader.getName());
    assertEquals(LOC_1, restaurantHeader.getLocation());
    assertEquals(CUISINE_1, restaurantHeader.getCuisine());
  }
}
