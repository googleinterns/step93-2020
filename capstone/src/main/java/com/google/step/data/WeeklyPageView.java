package com.google.step.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A simple class that contains the information for a weekly page view.
 */
public class WeeklyPageView {
  private final int week;
  private final int year;
  private final int count;

  public WeeklyPageView(int week, int year, int count) {
    this.week = week;
    this.year = year;
    this.count = count;
  }

  /**
   * Equals method for comparisons
   * @param other object to compare with
   * @return boolean for true if they are equals and
   *         false if they are not.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    WeeklyPageView pageView = (WeeklyPageView) other;
    if (this.hashCode() != other.hashCode()) {
      return false;
    }
    return pageView.getWeek() == this.week && pageView.getYear() == this.year
        && pageView.getCount() == this.count;
  }

  /**
   * Hashcode method for comparisons
   * @return integer representing the code
   */
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(week).append(year).append(count).toHashCode();
  }

  /**
   * Get the week of the instance
   * @return int representing the week
   */
  public int getWeek() {
    return week;
  }

  /**
   * Get the year of the instance
   * @return in representing the year
   */
  public int getYear() {
    return year;
  }

  /**
   * Get the count of the instance
   * @return int representing the count
   */
  public int getCount() {
    return count;
  }
}
