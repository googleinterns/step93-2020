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
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.List;


/**
 * A {@code RestaurantHeader} is an object that hold as subset of information about a restaurant.
 * It holds a {@code Restaurant.restaurantKey}, a {@code Restaurant.name} a {@code Restaurant.location},
 * and a {@code Restaurant.cuisine}.
 */
public final class RestaurantHeader {
  private final long restaurantKey;
  private final String name;
  private final GeoPt location;
  private final List<String> cuisine;

  public RestaurantHeader(long restaurantKey,
                           String name,
                           GeoPt location,
                           List<String> cuisine) {
    this.restaurantKey = restaurantKey;
    this.name = name;
    this.location = location;
    this.cuisine = Collections.unmodifiableList(cuisine);
  }

  /**
   * Given a {@code Restaurant} object, creates a {@code RestaurantHeader} object using the fields of the {@code Restaurant}.
   * @param restaurant The {@code Restaurant} from which the header information would come
   * @return A newly created RestaurantHeader object
   */
  public static RestaurantHeader createRestaurantHeader(Restaurant restaurant) {
    return new RestaurantHeader(
        restaurant.getRestaurantKey().orElse(0L),
        restaurant.getName(),
        restaurant.getLocation(),
        restaurant.getCuisine());
  }

  public long getRestaurantKey() {
    return restaurantKey;
  }

  public String getName() {
    return name;
  }

  public GeoPt getLocation() {
    return location;
  }

  public List<String> getCuisine() {
    return cuisine;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(restaurantKey)
        .append(name)
        .append(location)
        .append(cuisine)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof RestaurantHeader)) {
      return false;
    }

    RestaurantHeader otherRestaurantHeader = (RestaurantHeader) other;
    if (other.hashCode() != this.hashCode()) {
      return false;
    }

    return otherRestaurantHeader.getRestaurantKey() == (this.restaurantKey)
        && otherRestaurantHeader.getName().equals(this.name)
        && otherRestaurantHeader.getLocation().equals(this.location)
        && otherRestaurantHeader.getCuisine().equals(this.cuisine);
  }
}
