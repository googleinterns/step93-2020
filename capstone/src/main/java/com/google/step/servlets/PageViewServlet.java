package com.google.step.servlets;

import com.google.gson.Gson;
import com.google.step.clients.MetricsClient;
import com.google.step.data.RestaurantPageViews;
import com.google.step.data.WeeklyPageView;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that will read and write page view metrics to and from a persistent store.
 */
@WebServlet("/page-view")
public class PageViewServlet extends HttpServlet {
  private final MetricsClient metricsClient = new MetricsClient();
  private final Gson gson = new Gson();

  /**
   * Method that retrieves different page view metrics depending on the query parameters.
   * @param request with a mix of the following parameters: <String, restaurantKey> and <int, year>.
   *                These can be in the request or not at all, and depending on the mix a different
   *                method from the client will be called.
   *                String restaurantKey -> getCurrentPageViews()
   *                int year, String restaurantKey -> getYearRestaurantPageViews()
   *                empty -> getAllPageViews();
   *                A SC_BAD_REQUEST status will be set if none of these three specified
   * combinations is present.
   * @param response Json String response that varies depending of the method called by the mix of
   *                 parameters in the request.
   *                 getCurrentPageViews() -> WeeklyPageView (as Json)
   *                 getYearRestaurantPageViews() -> List<WeeklyPageView> (as Json list)
   *                 getAllPageViews() -> List<RestaurantPageViews> (as Json list)
   * @throws IOException can be thrown by the writer for the response.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String restaurantKey = null;
    if (request.getParameter("restaurantKey") != null) {
      restaurantKey = request.getParameter("restaurantKey");
    }

    int year = 0;
    if (request.getParameter("year") != null) {
      year = Integer.parseInt(request.getParameter("year"));
    }

    String json;
    if (restaurantKey == null && year <= 0) {
      List<RestaurantPageViews> restaurantPageViewsList = metricsClient.getAllPageViews();
      json = gson.toJson(restaurantPageViewsList);
    } else if (year <= 0 && restaurantKey != null) {
      WeeklyPageView weeklyPageView = metricsClient.getCurrentPageViews(restaurantKey);
      json = gson.toJson(weeklyPageView);
    } else if (year != 0 && restaurantKey != null) {
      List<WeeklyPageView> weeklyPageViewList =
          metricsClient.getYearRestaurantPageViews(year, restaurantKey);
      json = gson.toJson(weeklyPageViewList);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Post request that updates this week's pageViews for a specific restaurant.
   * @param request should contain the <String, restaurantKey> and <String, restaurantName> as
   *                parameters. A SC_BAD_REQUEST status can be sent to the frontend if one of these
   *                parameters is not present.
   * @param response no response.
   * @throws IOException can be thrown by the response.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String restaurantKey = request.getParameter("restaurantKey");

    if (restaurantKey == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    metricsClient.putPageView(restaurantKey);
  }
}
