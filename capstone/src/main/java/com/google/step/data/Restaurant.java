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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/** A restaurant to be displayed. */
public final class Restaurant {
  private final Long restaurantKey;
  private final String name;
  private final GeoPt location;
  private final String story;
  private final List<String> cuisine;
  private final String phone;
  private final String website;
  private final String status;

  public Restaurant(Long restaurantKey, String name, GeoPt location, String story,
      List<String> cuisine, String phone, String website, String status) {
    this.restaurantKey = restaurantKey;
    this.name = name;
    this.location = location;
    this.story = story;
    this.cuisine = Collections.unmodifiableList(cuisine);
    this.phone = phone;
    this.website = website;
    this.status = status;
  }

  /**
   * @return long restaurantKey, unique identifier for the restaurant
   */
  public Optional<Long> getRestaurantKey() {
    return Optional.ofNullable(restaurantKey);
  }

  /**
   * @return String name, name of the restaurant
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return GeoPt location, containing latitude and longitude of the restaurant
   */
  public GeoPt getLocation() {
    return this.location;
  }

  /**
   * @return String story, personal story of the restaurant
   */
  public String getStory() {
    return this.story;
  }

  /**
   * @return List<String> cuisine, list of cuisines for the restaurant
   */
  public List<String> getCuisine() {
    return cuisine;
  }

  /**
   * @return String phone, phone number of the restaurant
   */
  public String getPhone() {
    return this.phone;
  }

  /**
   * @return String website, website of the restaurant
   */
  public String getWebsite() {
    return this.website;
  }

  /**
   * @return String status, either STRUGGLING, OKAY, or GOOD
   */
  public String getStatus() {
    return this.status;
  }

  /**
   * equals method for use in testing.
   */
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Restaurant)) {
      return false;
    }

    Restaurant otherRestaurant = (Restaurant) other;
    if (other.hashCode() != this.hashCode()) {
      return false;
    }

    return otherRestaurant.getCuisine().equals(this.cuisine)
        && otherRestaurant.getLocation().equals(this.location)
        && otherRestaurant.getName().equals(this.name)
        && otherRestaurant.getPhone().equals(this.phone)
        && otherRestaurant.getStatus().equals(this.status)
        && otherRestaurant.getStory().equals(this.story)
        && otherRestaurant.getWebsite().equals(this.website);
  }

  /**
   * hashCode method for use in testing.
   */
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(cuisine)
        .append(location)
        .append(name)
        .append(phone)
        .append(status)
        .append(story)
        .append(website)
        .toHashCode();
  }
}
