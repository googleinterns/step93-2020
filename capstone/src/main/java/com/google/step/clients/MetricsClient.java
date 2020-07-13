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

package com.google.step.clients;

// Datastore and entities class
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;

public class MetricsClient {

    public MetricsClient() {}

    DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();

    //Method to get pageViews
    //Method to put page views

    //Method to get likes
    // Maybe return boolean to the servlet to show that the request could be handled
    public int getRestaurantLikes(String restaurantKey) {

        Query query = new Query("Likes")
                .setFilter(new Query.FilterPredicate(
                        "restaurantKey", Query.FilterOperator.EQUAL, restaurantKey
                ));
        PreparedQuery result = dataStore.prepare(query);
        Entity resultEntity = result.asSingleEntity();

        return Integer.parseInt(resultEntity.getProperty("count").toString());
    }

    //Method to put likes
    public void putRestaurantLike(String restaurantKey) {
        int currentLikes = getRestaurantLikes(restaurantKey);
        Entity likes = new Entity("Likes");
        // How would it work if several people got to put al ike at the same time?
    }

    //Method to get num people ordered
    //Method to put num people ordered

    //Method to get survey answers
    // Method to put survey answers

}
