package com.google.step.clients;

import com.google.step.data.RestaurantPageViews;
import com.google.step.data.RestaurantScore;
import com.google.step.data.WeeklyPageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Will take care of calculating the score for each restaurant.
 */
public class ScoreClient {
  private final MetricsClient metricsClient = new MetricsClient();

  /**
   * Calculates the scores of each restaurant.
   * @return an unmodifiable list of restaurantScore class.
   */
  public List<RestaurantScore> calculateScores() {
    List<RestaurantPageViews> allPageViews = metricsClient.getAllPageViews();
    if (allPageViews.isEmpty()) {
      return Collections.emptyList();
    }

    double systemAverage = getSystemAverage(allPageViews);

    long[] ids = new long[allPageViews.size()];
    double[] scores = new double[allPageViews.size()];
    int currentIndex = 0;
    for (RestaurantPageViews restaurant : allPageViews) {
      ids[currentIndex] = (Long.parseLong(restaurant.getId()));

      double restaurantAverage = calculateRestaurantAveragePageViews(restaurant);

      // Since the data is sorted we can just get the latest pageView for that restaurant.
      List<WeeklyPageView> pageViewList = restaurant.getPageViews();
      int lastPageViewIndex = pageViewList.size() - 1;
      int latestPageView = pageViewList.get(lastPageViewIndex).getCount();

      double currentRestaurantScore =
          calculateRawScore(latestPageView, restaurantAverage, systemAverage);
      scores[currentIndex] = currentRestaurantScore;
      currentIndex++;
    }

    standardizeScores(scores);
    normalizeScores(scores);

    List<RestaurantScore> restaurantScoresList = new ArrayList<>();
    for (int i = 0; i < currentIndex; i++) {
      restaurantScoresList.add(new RestaurantScore(ids[i], scores[i]));
    }

    return Collections.unmodifiableList(restaurantScoresList);
  }

  /**
   * Gets the average pageViews for the entire system.
   * @param allPageViews list of restaurantPageViews holding all the pageViews.
   * @return double representing the average pageViews for the system.
   */
  private double getSystemAverage(List<RestaurantPageViews> allPageViews) {
    double sum = 0;
    double count = 0;

    for (RestaurantPageViews restaurant : allPageViews) {
      // Can't be zero because if the RestaurantPageViews exists then it will have at a minimum
      // one pageView inside it.
      List<WeeklyPageView> pageViews = restaurant.getPageViews();
      count += pageViews.size();
      for (WeeklyPageView pageView : pageViews) {
        sum += pageView.getCount();
      }
    }

    return sum / count;
  }

  /**
   * Calculate the average page views for a specific restaurant.
   * @param restaurant has all the pageViews for a specific restaurant.
   * @return double representing the average pageViews for that restaurant.
   */
  private double calculateRestaurantAveragePageViews(RestaurantPageViews restaurant) {
    List<WeeklyPageView> pageViews = restaurant.getPageViews();

    double sum = 0;

    // This can't be zero because the instance of a RestaurantPageViews only exists if a
    // WeeklyPageView exists as well.
    double count = pageViews.size();

    for (WeeklyPageView pageView : pageViews) {
      sum += pageView.getCount();
    }

    return sum / count;
  }

  /**
   * Calculates the score for a restaurant using the baynesian theorem formula.
   * @param latestPageViews calculates the score respective to the latest pageViews for the
   *     restaurant.
   * @param restaurantAverage average pageViews for the restaurant.
   * @param systemAverage average pageViews for the entire system.
   * @return double representing the score for that specific restaurant.
   */
  private double calculateRawScore(
      int latestPageViews, double restaurantAverage, double systemAverage) {
    final double minimumWeeks = 10.0;
    double score = (latestPageViews / (latestPageViews + minimumWeeks)) * restaurantAverage
        + (minimumWeeks / (latestPageViews + minimumWeeks)) * systemAverage;
    return score;
  }

  /**
   * Standardizes the scores for the entire map of scores using z - score calculations.
   * @param scores array of the current scores for each restaurant.
   */
  private void standardizeScores(double[] scores) {
    double sum = 0.0;
    for (double score : scores) {
      sum += score;
    }

    double mean = sum / scores.length;

    double standardDeviation = 0.0;
    for (double score : scores) {
      standardDeviation += Math.pow(score - mean, 2);
    }

    standardDeviation = Math.sqrt(standardDeviation / (scores.length - 1));

    double standardizedScore;
    for (int i = 0; i < scores.length; i++) {
      standardizedScore = ((scores[i] - mean) / standardDeviation);
      scores[i] = standardizedScore;
    }
  }

  /**
   * Normalizes the scores into a range between 0 and 1 using min max normalizing.
   * It also inverts the score so a 1 is a low score and a 0 is a high score.
   * @param scores array of the current scores for each restaurant.
   */
  private void normalizeScores(double[] scores) {
    double min = Integer.MAX_VALUE;
    double max = Integer.MIN_VALUE;

    for (double score : scores) {
      if (score < min) {
        min = score;
      }
      if (score > max) {
        max = score;
      }
    }

    double normalizedScore;
    for (int i = 0; i < scores.length; i++) {
      normalizedScore = 1 - ((scores[i] - min) / (max - min));
      scores[i] = normalizedScore;
    }
  }
}
