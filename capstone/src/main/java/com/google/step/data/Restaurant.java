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

package com.google.step.data;

import com.google.appengine.api.datastore.GeoPt;
import java.util.ArrayList;
import java.util.List;

/** A restaurant to be displayed. */
public final class Restaurant {
  private final long restaurantKey;
  private final String name;
  private final GeoPt location;
  private final String story;
  private final List<String> cuisine;
  private final String phone;
  private final String website;
  private final String status;

  public Restaurant(long restaurantKey, String name, GeoPt location, String story,
      List<String> cuisine, String phone, String website, String status) {
    this.restaurantKey = restaurantKey;
    this.name = name;
    this.location = location;
    this.story = story;
    this.cuisine = cuisine;
    this.phone = phone;
    this.website = website;
    this.status = status;
  }
}
