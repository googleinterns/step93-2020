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
    public Builder query(String query) {
      this.query = query;
      return this;
    }

    /**
     * Set cuisine to be {@code cuisineList}. The cuisine represents a list of cuisine types to be
     * searched over.
     * @param cuisineList list of strings representing cuisines
     * @return {@code this} builder
     */
    public Builder cuisine(List<String> cuisineList) {
      this.cuisine = Collections.unmodifiableList(cuisineList);
      return this;
    }

    /**
     * Set the email query parameter to be {@code email}.
     * @param email a string representing an email address.
     * @return {@code this} builder
     */
    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public RestaurantQueryParams build() {
      return new RestaurantQueryParams(this);
    }
  }

  private RestaurantQueryParams(Builder builder) {
    this.query = builder.query;
    this.cuisine = builder.cuisine;
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
