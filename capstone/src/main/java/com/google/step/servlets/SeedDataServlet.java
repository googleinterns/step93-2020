// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.step.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Servlet that seeds some example content to datastore. */
@WebServlet("/seedData")
public class SeedDataServlet extends HttpServlet {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Open and parse the restaurant info json file
    JSONParser jsonParser = new JSONParser();
    try (FileReader reader = new FileReader("RestaurantInfoSeedData.json")) {
      Object json = jsonParser.parse(reader);
      JSONArray restaurantList = (JSONArray)json;
      // Parse all the restaurants in the list and add them to datastore
      restaurantList.forEach(restaurant -> finalizeRestaurantInfoEntity((JSONObject)restaurant));
    } catch (FileNotFoundException e) {
        System.out.println(e);
    } catch (IOException e) {
        System.out.println(e);
    } catch (ParseException e) {
        System.out.println(e);
    }
    
    // Open and parse the restaurant user info json file
    try (FileReader userReader = new FileReader("RestaurantUserSeedData.json")) {
      Object userJson = jsonParser.parse(userReader);
      JSONArray restaurantUserList = (JSONArray)userJson;
      // Parse all restaurant users in the list and add them to datastore
      restaurantUserList.forEach(restaurantUser -> finalizeRestaurantUserEntity((JSONObject)restaurantUser));
    } catch (FileNotFoundException e) {
        System.out.println(e);
    } catch (IOException e) {
        System.out.println(e);
    } catch (ParseException e) {
        System.out.println(e);
    }
  }

/** 
 * Parse a JSONObject with all fields for a RestaurantInfo
 * Entity. Build the Entity and add it to Datastore.
 */
  private static void finalizeRestaurantInfoEntity(JSONObject restaurant) {
    Entity restaurantInfo = new Entity("RestaurantInfo");
    restaurantInfo.setProperty("restaurantKey", (long)restaurant.get("restaurantKey"));
    restaurantInfo.setProperty("name", (String)restaurant.get("name"));
    restaurantInfo.setProperty("location", new GeoPt(((Double)restaurant.get("latitude")).floatValue(), ((Double)restaurant.get("longitude")).floatValue()));
    restaurantInfo.setProperty("story", (String)restaurant.get("story"));
    List<String> cuisineList = Arrays.asList(((String)restaurant.get("cuisine")).split(","));
    restaurantInfo.setProperty("cuisine", cuisineList);
    restaurantInfo.setProperty("phone", (String)restaurant.get("phone"));
    restaurantInfo.setProperty("website", (String)restaurant.get("website"));
    restaurantInfo.setProperty("score", (double)restaurant.get("score"));
    restaurantInfo.setProperty("status", (String)restaurant.get("status"));
    datastore.put(restaurantInfo);
  }

/** 
 * Parse a JSONObject with all fields for a RestaurantUser
 * Entity. Build the Entity and add it to Datastore.
 */
  private static void finalizeRestaurantUserEntity(JSONObject restaurantUser) {
    Entity restaurantUserEntity = new Entity("RestaurantUser");
    restaurantUserEntity.setProperty("restaurantKey", (long)restaurantUser.get("restaurantKey"));
    restaurantUserEntity.setProperty("email", (String)restaurantUser.get("email"));
    datastore.put(restaurantUserEntity);
  }
}
