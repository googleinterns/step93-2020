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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.List;

/** A restaurant to be displayed. */
public final class Restaurant {
  private final long restaurantKey;
  private final String name;
  private final GeoPt location;
  private final String story;
  private final List<String> cuisine;
  private final String phone;
  private final String website;
  private final String status;

  public Restaurant(long restaurantKey, String name, GeoPt location, String story,
      List<String> cuisine, String phone, String website, String status) {
    this.restaurantKey = restaurantKey;
    this.name = name;
    this.location = location;
    this.story = story;
    this.cuisine = cuisine;
    this.phone = phone;
    this.website = website;
    this.status = status;
  }

  /**
   * Factory method to return a Restaurant based on an Entity
   * @param entity
   * @return Restaurant, or null if the entity is the wrong kind
   */
  public static Restaurant fromEntity(Entity entity) {
    if (!entity.getKind().equals("RestaurantInfo")) {
      throw new IllegalArgumentException(
          "Element of type " + entity.getKind() + ", should be RestaurantInfo");
    }
    long restaurantKey = (Long) entity.getProperty("restaurantKey");
    String name = (String) entity.getProperty("name");
    GeoPt location = (GeoPt) entity.getProperty("location");
    String story = (String) entity.getProperty("story");
    List<String> cuisine = (List<String>) entity.getProperty("cuisine");
    String phone = (String) entity.getProperty("phone");
    String website = (String) entity.getProperty("website");
    String status = (String) entity.getProperty("status");

    // Return Restaurant object holding all info
    return new Restaurant(restaurantKey, name, location, story, cuisine, phone, website, status);
  }

  /**
   * @return long restaurantKey, unique identifier for the restaurant
   */
  public long getRestaurantKey() {
    return this.restaurantKey;
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
    return this.cuisine;
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
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Restaurant other = (Restaurant) obj;
    if (cuisine == null) {
      if (other.cuisine != null)
        return false;
    } else if (!cuisine.equals(other.cuisine))
      return false;
    if (location == null) {
      if (other.location != null)
        return false;
    } else if (!location.equals(other.location))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (phone == null) {
      if (other.phone != null)
        return false;
    } else if (!phone.equals(other.phone))
      return false;
    if (restaurantKey != other.restaurantKey)
      return false;
    if (status == null) {
      if (other.status != null)
        return false;
    } else if (!status.equals(other.status))
      return false;
    if (story == null) {
      if (other.story != null)
        return false;
    } else if (!story.equals(other.story))
      return false;
    if (website == null) {
      if (other.website != null)
        return false;
    } else if (!website.equals(other.website))
      return false;
    return true;
  }

  /**
   * hashCode method for use in testing.
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cuisine == null) ? 0 : cuisine.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((phone == null) ? 0 : phone.hashCode());
    result = prime * result + (int) (restaurantKey ^ (restaurantKey >>> 32));
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((story == null) ? 0 : story.hashCode());
    result = prime * result + ((website == null) ? 0 : website.hashCode());
    return result;
  }
}
