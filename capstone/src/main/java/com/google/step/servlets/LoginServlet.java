package com.google.step.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that will handle login information.
 */

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  /**
   * Method that gets login information from the front end.
   * @param request sent from the frontend without any parameters, or headers.
   * @param response depends on if the user is logged in.
   *                 Sent to the frontend with login information in json format:
   *                 {
   *                 "LoggedIn": <boolean>,
   *                 "Email": <string, only specified if loggedIn is true>,
   *                 "logOutURL": <string, only specified if loggedIn is true>,
   *                 "loginURL": <string, only specified if loggedIn is false>
   *                 }
   *
   * @throws IOException
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");

    UserService userService = UserServiceFactory.getUserService();
    Map<String, Object> login = new HashMap<>();

    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();

      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutURL = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      login.put("loggedIn", true);
      login.put("email", userEmail);
      login.put("logOutURL", logoutURL);
    } else {
      String urlToRedirectToAfterUserLogIn = "/";

      String loginURL = userService.createLoginURL(urlToRedirectToAfterUserLogIn);

      login.put("loggedIn", false);
      login.put("loginURL", loginURL);
    }

    String json = convertToJsonUsingGson(login);
    response.getWriter().println(json);
  }

  /**
   * Converts to Json using Gson.
   * @param login Map to convert to Gson.
   * @return String in JSON format.
   */
  private static String convertToJsonUsingGson(Map login) {
    Gson gson = new Gson();
    String json = gson.toJson(login);
    return json;
  }
}
