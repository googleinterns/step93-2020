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
   * Given a {@link RestaurantHeader}, updates search index with a document representing the
   * restaurant header. If the document for the restaurant header does not currently exist, a new
   * document will be created. Otherwise, the document is update with the new information.
   * @param restaurantHeader a {@link RestaurantHeader} that will be updated in the search index
   * @throws IOException if request cannot be made or executed properly
   */
  void updateRestaurantHeader(RestaurantHeader restaurantHeader) throws IOException;

  /**
   * Queries search server for {@link RestaurantHeader} objects that match {@code query} by either
   * "name" or "cuisine" field, if the query is not empty. If the query is empty, returns a random
   * assortment of restaurant headers.
   * @param query valid query string, or empty string
   * @return list of {@link RestaurantHeader} objects sorted by descending relevance score
   * @throws IOException thrown if request cannot be made or executed properly
   */
  List<RestaurantHeader> searchRestaurants(String query) throws IOException;
}