package com.google.step.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to hold data for restaurant search queries.
 */
public class RestaurantQueryParams {
  private final String query;
  private final List<String> cuisine;
  private final String email;

  public static class Builder {
    private String query = "";
    private List<String> cuisine = new ArrayList<>();
    private String email = "";

    public Builder() { }

    /**
     * Sets query parameter to {@code query}
     * @param query String representing a search query
     * @return {@code this} builder
     */
    public Builder setQuery(String query) {
      this.query = query;
      return this;
    }

    /**
     * Set cuisine to be {@code cuisineList}. The cuisine represents a list of cuisine types to be
     * searched over.
     * @param cuisineList list of strings representing cuisines
     * @return {@code this} builder
     */
    public Builder setCuisine(List<String> cuisineList) {
      this.cuisine = cuisineList;
      return this;
    }

    /**
     * Add {@code cuisineItem} to list of cuisines to search for.
     * @param cuisineItem a string representing a cuisine to search for
     * @return {@code this} builder
     */
    public Builder addCuisine(String cuisineItem) {
      this.cuisine.add(cuisineItem);
      return this;
    }

    /**
     * Set the email query parameter to be {@code email}.
     * @param email a string representing an email address.
     * @return {@code this} builder
     */
    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }
  }

  private RestaurantQueryParams(Builder builder) {
    this.query = builder.query;
    this.cuisine = Collections.unmodifiableList(builder.cuisine);
    this.email = builder.email;
  }

  public List<String> getCuisine() {
    return cuisine;
  }

  public String getEmail() {
    return email;
  }

  public String getQuery() {
    return query;
  }
}
