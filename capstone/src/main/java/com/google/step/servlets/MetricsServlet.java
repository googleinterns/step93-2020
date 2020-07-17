package com.google.step.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet that will connect with the frontend and the client of everything related to metrics.
 */
@WebServlet("/metrics")
public class MetricsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Request parameters:
        //      String restaurantName, String restaurantKey -> getCurrentPageViews()
        //      int year, String restaurantKey -> getYearRestaurantPageViews()
        //      emtpy -> getAllPageViews()

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Request parameters:
        //      String restaurantName, String restaurantKey -> putPageView()
    }
}
