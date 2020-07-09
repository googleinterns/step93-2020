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
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Gives back information regarding the current user to the frontend.
    // Output (ex): {"Loggedin":true, "Email":"example@gmail.com", "logOutUrl":"exampleUrl"}
    response.setContentType("application/json;");

    UserService userService = UserServiceFactory.getUserService();
    Map<String, Object> login = new HashMap<>();

    if (userService.isUserLoggedIn()) {
      String useremail = userService.getCurrentUser().getEmail();

      String urltoRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urltoRedirectToAfterUserLogsOut);

      login.put("Loggedin", true);
      login.put("Email", useremail);
      login.put("logOutUrl", logoutUrl);
    } else {
      String urltoRedirectToAfterUserLogIn = "/";

      String loginUrl = userService.createLoginURL(urltoRedirectToAfterUserLogIn);

      login.put("Loggedin", false);
      login.put("loginUrl", loginUrl);
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
