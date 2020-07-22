package com.google.step.search;

import com.google.step.data.RestaurantHeader;

import java.io.IOException;
import java.util.List;

/**
 * The {@code RestaurantHeaderSearchClient} sends requests to add, update and query
 *  {@code RestaurantHeaders} in a search Index.
 */
public interface RestaurantHeaderSearchClient {

  /**
   * Given a {@link RestaurantHeader}, sends HTTP request to the search server to add a document
   * that represents a {@link RestaurantHeader} to the "restaurants" index.
   * @param restaurantHeader a {@link RestaurantHeader} that will be updated in the search index
   * @throws IOException if request cannot be made or executed properly
   */
  void updateRestaurantHeader(RestaurantHeader restaurantHeader) throws IOException;

  /**
   * Queries search server for {@link RestaurantHeader} fields that match {@code query} by either
   * "name" or "cuisine" field.
   * @param query valid query string
   * @return list of {@link RestaurantHeader} objects sorted by descending relevance score
   * @throws IOException if request cannot be made or executed properly
   */
  List<RestaurantHeader> queryRestaurantHeaders(String query) throws IOException;

  /**
   * Queries search server, matching everything possible.
   * @return list of {@link RestaurantHeader} objects sorted arbitrarily
   * @throws IOException if request cannot be made or executed properly
   */
  List<RestaurantHeader> getRandomRestaurants() throws IOException;
}
