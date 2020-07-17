// Copyright 2019 Google LLC
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

/**
 * Sets the page details based on the restaurant
 * stored in the session.
 */
async function setRestaurantDetails() {
  // Get restaurant key from URL and query for individual restaurant
  const keyString = window.location.search;
  const urlParam = new URLSearchParams(keyString);
  const restaurantKey = urlParam.get('restaurantKey');
  const responsePath = '/restaurant?restaurantKey=' + restaurantKey;
  const response = await fetch(responsePath);
  const resp = await response.json();

  if (resp !== null) {
    // Retrieve and parse restaurant JSON from get restaurant response
    const currRestaurant = resp.value;
    // Set UI elements
    if (document.getElementById('restaurant-detail-area') !== null) {
      document.getElementById('current-restaurant-title').innerText =
          currRestaurant.name;
      document.getElementById('current-restaurant-story').innerText =
          currRestaurant.story;
      const link = document.createElement('a');
      link.setAttribute('href', currRestaurant.website);
      link.innerText = currRestaurant.website;
      // Clear out any link that was there before and append the new one
      document.getElementById('current-restaurant-website').innerHTML = '';
      document.getElementById('current-restaurant-website').appendChild(link);
      document.getElementById('current-restaurant-phone').innerText =
          currRestaurant.phone;
      const location = currRestaurant.location;
      const lat = location.latitude;
      const long = location.longitude;
      createMap(lat, long);
      for (let i = 0; i < currRestaurant.cuisine.length; i++) {
        appendCuisineTag(currRestaurant.cuisine[i]);
      }
    }
  }

    // Update page views for this restaurant
    await updatePageViews(restaurantKey);
}

/**
 * Return to home landing page.
 */
function returnHome() {
  window.location.href = '/';
}


/**
 * Creates a map at centered at and
 * containing a marker for the specified
 * latitude and longitude.
 */
function createMap(latitude, longitude) {
  const google = window.google;
  const map = new google.maps.Map(
      document.getElementById('current-restaurant-map'),
      {center: {lat: latitude, lng: longitude}, zoom: 15});
  addLandmark(map, latitude, longitude);
}

/**
 * Adds a marker to the map.
 */
function addLandmark(map, lat, lng) {
  const google = window.google;
  const marker =
      new google.maps.Marker({position: {lat: lat, lng: lng}, map: map});
}

/**
 * Appends a material-style tag for
 * the specified cuisine name to the
 * cuisine area.
 */
function appendCuisineTag(cuisineName) {
  const outerDiv = document.createElement('div');
  outerDiv.className = 'mdc-chip';
  outerDiv.setAttribute('role', 'row');
  const innerDiv = document.createElement('div');
  innerDiv.className = 'mdc-chip__ripple';
  const span1 = document.createElement('span');
  span1.setAttribute('role', 'gridcell');
  const span2 = document.createElement('span');
  span2.className = 'mdc-chip__text';
  span2.innerText = cuisineName;
  span1.appendChild(span2);
  outerDiv.appendChild(innerDiv);
  outerDiv.appendChild(span1);
  if (document.getElementById('current-restaurant-cuisines') !== null) {
    document.getElementById('current-restaurant-cuisines')
        .appendChild(outerDiv);
  }
}

/**
 * Update the page views for a restaurant using the PageViewServlet.
 * @param restaurantKey
 * @param restaurantName
 * @return {Promise<void>}
 */
async function updatePageViews(restaurantKey) {
  const params = 'restaurantKey=' + restaurantKey;
  await fetch('/page-view', {
    method: 'post',
    body: params,
    headers: {'content-type': 'application/x-www-form-urlencoded'},
  });
}

