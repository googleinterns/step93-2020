package com.google.step.clients;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.datastore.v1.PropertyFilter;
import com.google.apphosting.datastore.DatastoreV4;
import com.google.step.data.Restaurant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestaurantClient {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Puts a single entity of type RestaurantInfo into Datastore.
   * @param restaurant  to be processed into an Entity and put into Datastore
   * @param ownerEmail  separate from Restaurant class and passed from UserService in the servlet
   * @return newly created restaurantKey
   */
  public long putRestaurant(Restaurant restaurant, String ownerEmail) {
    // Retrieve restaurant properties
    String name = restaurant.getName();
    GeoPt location = restaurant.getLocation();
    String story = restaurant.getStory();
    List<String> cuisineList = restaurant.getCuisine();
    String phone = restaurant.getPhone();
    String website = restaurant.getWebsite();
    String status = restaurant.getStatus();

    // Parse into a RestaurantInfo Entity
    Entity restaurantInfo = new Entity("RestaurantInfo");
    restaurantInfo.setProperty("name", name);
    restaurantInfo.setProperty("email", ownerEmail);
    restaurantInfo.setProperty("location", location);
    restaurantInfo.setProperty("story", story);
    restaurantInfo.setProperty("cuisine", cuisineList);
    restaurantInfo.setProperty("phone", phone);
    restaurantInfo.setProperty("website", website);
    restaurantInfo.setProperty("status", status);

    // The following value is hardcoded while we implement the properties.
    restaurantInfo.setProperty("score", 2.5);
    datastore.put(restaurantInfo);
    return restaurantInfo.getKey().getId();
  }

  /**
   * Queries for a single restaurant in datastore based on restaurantKey.
   * @param restaurantKey, unique key of the restaurant to query for
   * @return Optional<Restaurant>, parsed from the datastore properties or empty if not found
   */
  public Optional<Restaurant> getSingleRestaurant(long restaurantKey) {
    Query query = new Query("RestaurantInfo")
                      .setFilter(new Query.FilterPredicate(
                          Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, KeyFactory.createKey("RestaurantInfo", restaurantKey)));
    PreparedQuery results = datastore.prepare(query);
    Entity resultEntity = results.asSingleEntity();
    if (resultEntity == null) {
      return Optional.empty();
    }

    // Return Restaurant object with all info from the Entity
    return Optional.of(fromEntity(resultEntity));
  }

  /**
   * Gets all RestaurantInfo entities from datastore and returns them as a list.
   * TODO: Change this method to return a list of RestaurantHeaders instead of Restaurants.
   * TODO: Add location or other filters.
   * @return List<RestaurantHeader>, list of necessary restaurant info for display on search page
   */
  public List<Restaurant> getRestaurantsNoFilter() {
    Query query = new Query("RestaurantInfo");
    PreparedQuery results = datastore.prepare(query);

    List<Restaurant> restaurants = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      // Restaurant object to hold all info
      Restaurant restaurant = fromEntity(entity);
      restaurants.add(restaurant);
    }
    return restaurants;
  }

  /**
   * Factory method to return a Restaurant based on an Entity
   * @param entity, a RestaurantInfo entity
   * @return Restaurant, based on properties of the entity
   * @throws IllegalArgumentException if the entity is not of type RestaurantInfo
   */
  private static Restaurant fromEntity(Entity entity) {
    if (!entity.getKind().equals("RestaurantInfo")) {
      throw new IllegalArgumentException(
          "Element of type " + entity.getKind() + ", should be RestaurantInfo");
    }
    long restaurantKey = entity.getKey().getId();
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
}
