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

package com.google.step.search;

import com.google.step.data.RestaurantHeader;
import com.google.step.data.RestaurantScore;

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
   * @throws IOException thrown if request cannot be made or executed properly
   */
  void updateRestaurantHeader(RestaurantHeader restaurantHeader) throws IOException;

  /**
   * Given a list of {@link RestaurantScore} objects, updates the metrics-based score field of the
   * restaurant search documents corresponding to the {@code restaurantKey} fields of the {@code
   * RestaurantScore} objects.
   * @param scores list of {@link RestaurantScore} objects
   * @throws IOException thrown if request cannot be made or executed properly
   */
  void updateRestaurantScores(List<RestaurantScore> scores) throws IOException;

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
