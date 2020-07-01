package com.google.step.servlets;

import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/get-restaurants")
public class getRestaurantsServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Map> data = new ArrayList<>();

    for (int i = 0; i < 5; i ++) {
      Map<String, Number> locationData = new HashMap<>();
      locationData.put("latitude", 42.0579432);
      locationData.put("longitude", -87.6856514);

      Map<String, Object> restaurantInfo = new HashMap<>();
      restaurantInfo.put("name", "Restaurant " + i);
      restaurantInfo.put("cuisine", Arrays.asList("pizza", "italian"));
      restaurantInfo.put("location", locationData);
      restaurantInfo.put("isStruggling", i % 3 == 1 || i % 3 == 2);

      data.add(restaurantInfo);
    }
    Gson gson = new Gson();
    String jsonResponse = gson.toJson(data);

    response.setContentType("application/json");
    response.getWriter().println(jsonResponse);
  }
}