package com.google.step.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import com.google.step.clients.ScoreClient;
import com.google.step.data.RestaurantScore;

/**
 * Servlet that will update the search index using the score calculations.
 */
@WebServlet("/update-score")
public class ScoreServlet extends HttpServlet {
    private final ScoreClient scoreClient = new ScoreClient();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<RestaurantScore> restaurantScores = scoreClient.calculateScores();
    }
}
