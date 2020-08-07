package com.google.step.data;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Object that contains all the weekly page views of a specific restaurant.
 */
public class RestaurantPageViews {
  private final String name;
    private final String id;
  private final List<WeeklyPageView> pageViews;

  public RestaurantPageViews(String name, String id, List<WeeklyPageView> pageViews) {
      this.name = name;
      this.id = id;
      this.pageViews = Collections.unmodifiableList(pageViews);
  }

  /**
   * Gets the name of the instance
   * @return String representing the name of the restaurant.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the list of weeklyPageViews of the restaurant
   * @return list of weekly page views
   */
  public List<WeeklyPageView> getPageViews() {
    return pageViews;
  }

  /**
   * Gets the id of the instance
   * @return String representing the id of the restaurant.
   */
  public String getId() {
      return id;
  }

  /**
   * Equals method for comparisons
   * @param other object to compare to
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    RestaurantPageViews restaurantPageViews = (RestaurantPageViews) other;
    return this.name.equals(restaurantPageViews.getName())
        && this.pageViews.equals(restaurantPageViews.getPageViews());
  }

  /**
   * Hashcode method for comparisons
   * @return integer representing the code
   */
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(name).append(id).append(pageViews).toHashCode();
  }
}
