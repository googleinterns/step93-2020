package com.google.step.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// import com.google.step.clients.MetricsClient;

/**
 * Servlet that will connect with the frontend and the client of everything related to metrics.
 */
@WebServlet("/metrics")
public class MetricsServlet extends HttpServlet {

    // Wait to merge that branch to master
    // private static MetricsClient metricsClient = new MetricsClient();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Request parameters:
        //      String restaurantName, String restaurantKey -> getCurrentPageViews()
        //      int year, String restaurantKey -> getYearRestaurantPageViews()
        //      emtpy -> getAllPageViews()
        response.setContentType("application/json;");



    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Request parameters:
        //      String restaurantName, String restaurantKey -> putPageView()
    }
}
