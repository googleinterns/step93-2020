package com.google.step.clients;

import com.google.step.data.RestaurantPageViews;
import com.google.step.data.RestaurantScore;
import com.google.step.data.WeeklyPageView;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;


/**
 * Will take care of calculating the score for each restaurant.
 */
public class ScoreClient {

    private final double minimumWeeks = 10.0;
    private final MetricsClient metricsClient = new MetricsClient();

    /**
     * Calculates the scores of each restaurant.
     * @return an unmodifiable list of restaurantScore class.
     */
    public List<RestaurantScore> calculateScores() {
        Map<Long, Double> scoreMap = new HashMap<>();
        List<Long> restaurantIds = new ArrayList<>();

        List<RestaurantPageViews> allPageViews = metricsClient.getAllPageViews();

        double systemAverage = getSystemAverage(allPageViews);

        // Calculate score
        for (RestaurantPageViews restaurant: allPageViews) {
            double restaurantAverage = calculateRestaurantAveragePageViews(restaurant);

            // Since the data is sorted we can just get the latest pageView for that restaurant.
            List<WeeklyPageView> pageViewList = restaurant.getPageViews();
            int lastPageViewIndex = pageViewList.size() - 1;
            int latestPageView = pageViewList.get(lastPageViewIndex).getCount();

            double score = calculateRawScore(latestPageView, restaurantAverage, systemAverage);
            restaurantIds.add(Long.parseLong(restaurant.getId()));
            scoreMap.put(Long.parseLong(restaurant.getId()), score);
        }

        standardizeScores(scoreMap, restaurantIds);
        normalizeScores(scoreMap, restaurantIds);

        // Build the scores return list.
        List<RestaurantScore> scores = new ArrayList<>();
        for (Long restaurantId: restaurantIds) {
            scores.add(new RestaurantScore(restaurantId, scoreMap.get(restaurantId)));
        }

        return Collections.unmodifiableList(scores);
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
            List<WeeklyPageView> pageViews = restaurant.getPageViews();
            count += pageViews.size();
            for (int i = 0; i < pageViews.size(); i++) {
                sum += pageViews.get(i).getCount();
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
        double count = pageViews.size();

        for (int i = 0; i < pageViews.size(); i++) {
            sum += pageViews.get(i).getCount();
        }

        return sum / count;
    }

    /**
     * Calculates the score for a restaurant using the baynesian theorem formula.
     * @param latestPageViews calculates the score respective to the latest pageViews for the restaurant.
     * @param restaurantAverage average pageViews for the restaurant.
     * @param systemAverage average pageViews for the entire system.
     * @return double representing the score for that specific restaurant.
     */
    private double calculateRawScore(int latestPageViews, double restaurantAverage, double systemAverage) {
        double score = (latestPageViews / (latestPageViews + minimumWeeks)) * restaurantAverage
                + (minimumWeeks / (latestPageViews + minimumWeeks)) * systemAverage;
        return score;
    }

    /**
     * Standardizes the scores for the entire map of scores using z - score calculations.
     * @param scoreMap map that holds the restaurant id as a key and the score of that restaurant as a value.
     * @param restaurantIds list that holds the restaurant ids to get the values of the map.
     */
    private void standardizeScores(Map<Long, Double> scoreMap, List<Long> restaurantIds) {
        double sum = 0.0f;

        for (Long restaurantId : restaurantIds) {
            sum += scoreMap.get(restaurantId);
        }

        double mean = sum / restaurantIds.size();

        double standardDeviation = 0.0f;
        for (Long restaurantId : restaurantIds) {
            standardDeviation += Math.pow(scoreMap.get(restaurantId) - mean, 2);
        }

        standardDeviation = Math.sqrt(standardDeviation / (restaurantIds.size() - 1));

        double standardizedScore;
        for (Long restaurantId : restaurantIds) {
            standardizedScore = (scoreMap.get(restaurantId) - mean) / standardDeviation;
            scoreMap.put(restaurantId, standardizedScore);
        }
    }

    /**
     * Normalizes the scores into a range between 0 and 1 using min max normalizing.
     * It also inverts the score so a 1 is a low score and a 0 is a high score.
     * @param scoreMap map that holds the restaurant id as a key and the score of that restaurant as a value.
     * @param restaurantIds list that holds the restaurant ids to get the values of the map.
     */
    private void normalizeScores(Map<Long, Double> scoreMap, List<Long> restaurantIds) {
        double min = Integer.MAX_VALUE;
        double max = Integer.MIN_VALUE;

        for (Long restaurantId : restaurantIds) {
            if (scoreMap.get(restaurantId) < min) {
                min = scoreMap.get(restaurantId);
            }
            if (scoreMap.get(restaurantId) > max) {
                max = scoreMap.get(restaurantId);
            }
        }

        double normalizedScore;
        for (Long restaurantId : restaurantIds) {
            normalizedScore = 1 - ((scoreMap.get(restaurantId) - min) / (max - min));
            scoreMap.put(restaurantId, normalizedScore);
        }
    }
}
