package com.google.step.servlets;

import com.google.step.clients.ScoreClient;
import com.google.step.data.RestaurantScore;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
