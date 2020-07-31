package com.google.step.clients;

import com.google.step.data.RestaurantPageViews;
import com.google.step.data.RestaurantScore;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;


/**
 * Will take care of calculating the score for each restaurant.
 */
public class ScoreClient {

    private final float minimum = 10.0f;
    private final MetricsClient metricsClient = new MetricsClient();

    /**
     * Only method to be called and calculates the scores of each restaurant.
     * @return list of restaurantScore class.
     */
    public List<RestaurantScore> calculateScores() {
        List<RestaurantScore> scores = new ArrayList<>();

        Map<Long, Double> scoreMap = new HashMap<>();
        List<Long> restaurantIds = new ArrayList<>();
        List<RestaurantPageViews> allPageViews = metricsClient.getAllPageViews("restaurantKey");

        double systemAverage = getSystemAverage(allPageViews);

        // Calculate score
        for (RestaurantPageViews restaurant: allPageViews) {
            double restaurantAverage = calculateRestaurantAveragePageViews(restaurant);

            // Since the data is sorted we can just get the latest pageView for that restaurant.
            int lastPageViewIndex = restaurant.getPageViews().size() - 1;
            int latestPageView = restaurant.getPageViews().get(lastPageViewIndex).getCount();

            double score = calculateRawScore(latestPageView, restaurantAverage, systemAverage);
            restaurantIds.add(Long.parseLong(restaurant.getName()));
            scoreMap.put(Long.parseLong(restaurant.getName()), score);
        }

        standardizeScores(scoreMap, restaurantIds);
        normalizeScores(scoreMap, restaurantIds);

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
            for (int i = 0; i < restaurant.getPageViews().size(); i++) {
                sum += restaurant.getPageViews().get(i).getCount();
                count++;
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
        double sum = 0;
        double count = 0;

        for (int i = 0; i < restaurant.getPageViews().size(); i++) {
            sum += restaurant.getPageViews().get(i).getCount();
            count++;
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
        double score = (latestPageViews / (latestPageViews + minimum)) * restaurantAverage
                + (minimum / (latestPageViews + minimum)) * systemAverage;
        return score;
    }

    /**
<<<<<<< HEAD
     * Standardizes the scores for the entire map of scores using z - score.
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
<<<<<<< HEAD
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
