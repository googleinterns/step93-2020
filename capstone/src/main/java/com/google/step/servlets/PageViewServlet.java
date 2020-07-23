package com.google.step.servlets;

import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.List;

import com.google.step.clients.MetricsClient;
import com.google.step.data.WeeklyPageView;
import com.google.step.data.RestaurantPageViews;

/**
 * Servlet that will call the metricsClient for any doGet or doPost requests.
 */
@WebServlet("/page-view")
public class PageViewServlet extends HttpServlet {

    private static MetricsClient metricsClient = new MetricsClient();

    /**
     *
     * @param request with a mix of the following parameters: <String, restaurantKey> and <int, year>.
     *                These can be in the request or not at all, and depending on the mix a different
     *                method from the client will be called.
     *                String restaurantKey -> getCurrentPageViews()
     *                int year, String restaurantKey -> getYearRestaurantPageViews()
     *                empty -> getAllPageViews();
     *                A SC_FORBIDDEN status will be set if none of these three specified combinations
     *                is present.
     * @param response Json String response that varies depending of the method called by the mix of
     *                 parameters in the request.
     *                 getCurrentPageViews() -> WeeklyPageView (as Json)
     *                 getYearRestaurantPageViews() -> List<WeeklyPageView> (as Json list)
     *                 getAllPageViews() -> List<RestaurantPageViews> (as Json list)
     * @throws IOException can be thrown by the writer for the response.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String restaurantKey;
        if (request.getParameter("restaurantKey") != null) {
            restaurantKey = request.getParameter("restaurantKey");
        } else {
            restaurantKey = null;
        }

        int year = 0;
        if (request.getParameter("year") != null) {
            year = Integer.parseInt(request.getParameter("year"));
        }

        String json;
        if (restaurantKey == null && year == 0) {
            // getAllPageViews()
            List<RestaurantPageViews> restaurantPageViewsList = metricsClient.getAllPageViews();
            json = convertRestaurantPageViewListToJsonUsingGson(restaurantPageViewsList);
        } else if (year == 0 && restaurantKey != null) {
            // getCurrentPageViews()
            WeeklyPageView weeklyPageView = metricsClient.getCurrentPageViews(restaurantKey);
            json = convertWeeklyPageViewToJsonUsingGson(weeklyPageView);

        } else if (year != 0 && restaurantKey != null) {
            // getYearRestaurantPageViews
            List<WeeklyPageView> weeklyPageViewList = metricsClient.getYearRestaurantPageViews(year, restaurantKey);
            json = convertWeeklyPageViewListToJsonUsingGson(weeklyPageViewList);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    /**
     *
     * @param request should contain the <String, restaurantKey> and <String, restaurantName> as
     *                parameters. A SC_FORBIDDEN status can be sent to the frontend if one of these
     *                parameters is not present.
     * @param response no response.
     * @throws IOException can be thrown by the response.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String restaurantKey = request.getParameter("restaurantKey");
        String restaurantName = request.getParameter("restaurantName");

        if (restaurantKey == null || restaurantName == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        metricsClient.putPageView(restaurantName, restaurantKey);
    }

    /**
     * Converts to Json using Gson.
     * @param weeklyPageView Map to convert to Gson.
     * @return String in Json format.
     */
    private static String convertWeeklyPageViewToJsonUsingGson(WeeklyPageView weeklyPageView) {
        Gson gson = new Gson();
        String json = gson.toJson(weeklyPageView);
        return json;
    }

    /**
     * Converts a list of weeklyPageViews to Json using Gson.
     * @param list of weeklyPageviews.
     * @return String in Json format.
     */
    private static String convertWeeklyPageViewListToJsonUsingGson(List<WeeklyPageView> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    /**
     * Converts a list of restaurantPageViews to Json using Gson.
     * @param list of restaurantPageViews
     * @return String in Json format.
     */
    private static String convertRestaurantPageViewListToJsonUsingGson(List<RestaurantPageViews> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
