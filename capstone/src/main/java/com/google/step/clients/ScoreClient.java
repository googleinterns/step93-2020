package com.google.step.clients;

import com.google.step.data.RestaurantPageViews;
import com.google.step.data.RestaurantScore;

import java.util.List;
import java.util.ArrayList;

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
        List<RestaurantPageViews> allPageViews = metricsClient.getAllPageViews();
        double systemAverage = getSystemAverage(allPageViews);

        // Calculate score
        for (RestaurantPageViews restaurant: allPageViews) {
            double restaurantAverage = calculateRestaurantAveragePageViews(restaurant);

            // Since the data is sorted we can just get the latest pageView for that restaurant.
            int lastPageViewIndex = restaurant.getPageViews().size() - 1;
            int latestPageView = restaurant.getPageViews().get(lastPageViewIndex).getCount();

            double score = calculateRawScore(latestPageView, restaurantAverage, systemAverage);
            scores.add(new RestaurantScore(restaurant.getName(), score));
        }

        standardizeScores(scores);
        normalizeScores(scores);

        return scores;
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
     * Standardizes the scores for the entire list of scores using z - score.
     * @param scores list of restaurantScores to be standardized.
     */
    private void standardizeScores(List<RestaurantScore> scores) {
        double sum = 0.0f;

        for (RestaurantScore score : scores) {
            sum += score.getScore();
        }

        double mean = sum / scores.size();

        double standardDeviation = 0.0f;
        for (RestaurantScore score : scores) {
            standardDeviation += Math.pow(score.getScore() - mean, 2);
        }

        standardDeviation = Math.sqrt(standardDeviation / (scores.size() - 1));

        double standardizedScore;
        for (RestaurantScore score : scores) {
            standardizedScore = (score.getScore() - mean) / standardDeviation;
            score.setScore(standardizedScore);
        }
    }

    /**
     * Normalizess the scores into a range between 0 and 1 using min max normalizing.
     * @param scores list of restaurantScores to be normalized.
     */
    private void normalizeScores(List<RestaurantScore> scores) {
        double min = Integer.MAX_VALUE;
        double max = Integer.MIN_VALUE;

        for (RestaurantScore score : scores) {
            if (score.getScore() < min) {
                min = score.getScore();
            }
            if (score.getScore() > max) {
                max = score.getScore();
            }
        }

        double standardizedScore;
        for (RestaurantScore score : scores) {
            standardizedScore = (score.getScore() - min) / (max - min);
            score.setScore(standardizedScore);
        }
    }
}
