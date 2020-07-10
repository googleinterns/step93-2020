package com.google.step.clients;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.step.data.Restaurant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class RestaurantClient {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Puts a single entity of any kind into datastore.
   * @param entity, any kind, to be put into datastore
   */
  public static void putEntity(Entity entity) {
    datastore.put(entity);
  }

  /**
   * Queries for a single restaurant in datastore based on restaurantKey.
   * @param restaurantKey, unique key of the restaurant to query for
   * @return Restaurant, parsed from the datastore properties
   * @throws NoSuchElementException if no RestaurantInfo entity is found with the requested key
   */
  public static Restaurant getSingleRestaurant(long restaurantKey) {
    Query query = new Query("RestaurantInfo")
                      .setFilter(new Query.FilterPredicate(
                          "restaurantKey", Query.FilterOperator.EQUAL, restaurantKey));
    PreparedQuery results = datastore.prepare(query);
    Entity resultEntity = results.asSingleEntity();
    if (resultEntity == null) {
      throw new NoSuchElementException("No RestaurantInfo found with key " + restaurantKey);
    }

    // Return Restaurant object with all info from the Entity
    return fromEntity(resultEntity);
  }

  /**
   * Gets all RestaurantInfo entities from datastore and returns them as a list.
   * TODO: Change this method to return a list of RestaurantHeaders instead of Restaurants.
   * TODO: Add location or other filters.
   * @return List<RestaurantHeader>, list of necessary restaurant info for display on search
   *     page</RestaurantHeader>
   */
  public static List<Restaurant> getRestaurantsNoFilter() {
    Query query = new Query("RestaurantInfo");
    PreparedQuery results = datastore.prepare(query);

    List<Restaurant> restaurants = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      // Restaurant object to hold all info
      Restaurant restaurant = RestaurantClient.fromEntity(entity);
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
}
