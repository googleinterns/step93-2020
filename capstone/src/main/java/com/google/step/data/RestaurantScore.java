package com.google.step.data;

/**
 * Class that holds the score for a specific restaurant.
 */
public class RestaurantScore {

    // The name should always remain the same but the score will change when calculating it.
    private final String restaurantName;
    private double score;

    /**
     * Constructor for the class.
     * @param restaurantName name of the restaurant.
     * @param score for the restaurant.
     */
    public RestaurantScore(String restaurantName, double score) {
        this.restaurantName = restaurantName;
        this.score = score;
    }

    /**
     * Get the restaurant Name.
     * @return String representing the restaurant name.
     */
    public String getRestaurantName() {
        return restaurantName;
    }

    /**
     * Get the score for the restaurant.
     * @return double representing the score.
     */
    public double getScore() {
        return score;
    }

    /**
     * Set the score to a new value.
     * @param score double representing the new score.
     */
    public void setScore(double score) {
        this.score = score;
    }

}
