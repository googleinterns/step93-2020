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
function setRestaurantDetails() {
  // Retrieve stored restaurant from session
  const currRestaurant =
      JSON.parse(sessionStorage.getItem('selectedRestaurant'));

  // Set UI elements
  if (currRestaurant !== null && currRestaurant !== '') {
    if (document.getElementById('title') !== null) {
      document.getElementById('title').innerText = currRestaurant.name;
    }
    if (document.getElementById('story') !== null) {
      document.getElementById('story').innerText = currRestaurant.story;
    }
    if (document.getElementById('website') !== null) {
      document.getElementById('website').innerHTML = '<a href=\'' +
          currRestaurant.website + '\'>' + currRestaurant.website + '</a>';
    }
    if (document.getElementById('phone') !== null) {
      document.getElementById('phone').innerText = currRestaurant.phone;
    }
    const location = currRestaurant.location;
    const lat = location.latitude;
    const long = location.longitude;
    createMap(lat, long);
    for (let i = 0; i < currRestaurant.cuisine.length; i++) {
      appendCuisineTag(currRestaurant.cuisine[i]);
    }
  }
}


/**
 * Return to home landing page.
 */
function returnHome() {
  window.open('/', '_self', false);
}


/**
 * Creates a map at centered at and
 * containing a marker for the specified
 * latitude and longitude.
 */
function createMap(latitude, longitude) {
  const google = window.google;
  const map = new google.maps.Map(
      document.getElementById('map'),
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
  if (document.getElementById('cuisines') !== null) {
    document.getElementById('cuisines').appendChild(outerDiv);
  }
}
