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

//let currRestaurant = null;
const google = window.google;

async function getRestaurants() {
  // Fetch comments from servlet
  const responsePath = '/restaurant';
  const response = await fetch(responsePath);
  const resp = await response.json();

  const restaurantArea = document.getElementById('restaurant-space');
  if (restaurantArea !== null && resp.restaurants !== null) {
    // Retrieve and parse comments JSON from get-comments response
    let restaurants = resp.restaurants;
    restaurants = JSON.parse(restaurants);
    // Clear comment area in case page is being reloaded
    restaurantArea.innerHTML = '';

    // Append current comments to page
    for (let i = 0; i < restaurants.length; i++) {
      const restaurant = restaurants[i];
      const restaurantElement = createRestaurantElement(restaurant);
      restaurantArea.appendChild(restaurantElement);
    }
  }
}

/**
 * Creates an element for a restaurant, including 
 * redirecting to its detail page on click.
 */
function createRestaurantElement(restaurant) {
  // Article tag to encapsulate restaurant elements
  const restaurantElement = document.createElement('article');
  const title = document.createElement('h2');
  title.innerText = restaurant.name;
  restaurantElement.appendChild(title);

  // Restaurant element UI details

  restaurantElement.addEventListener('click', () => {
    // Open restaurant details page
    //currRestaurant = restaurant;
    let details = window.open('/restaurantDetails.html');
    
    details.onload = function() {
      setTimeout(function() { 
          //setRestaurantDetailsPage(details, details.document, restaurant);
          details.setRestaurantDetailsPage(restaurant);
      }, 1000);
    }
    //details.addEventListener('load', setRestaurantDetailsPage(details, restaurant), true);
    //details.onload = setRestaurantDetailsPage(restaurant);
    //details.focus();
    // document.addEventListener('load', function() {
    //   setRestaurantDetailsPage(details, restaurant);
    //   console.log("hi");
    //   details.alert("new win");
    // }, true);
    // Set all the details
    // $(details.document).load(function() {
    //     console.log("loaded");
    //     setRestaurantDetailsPage(restaurant);
    // })
    // window.addEventListener('load', function() {
    //   setRestaurantDetailsPage(restaurant);
    // })
  });
  return restaurantElement;
}

// function setRestaurantDetailsPage(window, document, currRestaurant) {
//   //console.log(window.document);
//   //window.document.title.innerText = restaurant.name;
//   document.getElementById('title').innerText = currRestaurant.name;
//   document.getElementById('story').innerText = currRestaurant.story;
//   const location = currRestaurant.location;
//   const lat = location.latitude
//   const long = location.longitude;
//   createMap(lat, long);
// }

function returnHome() {
  window.open('/', '_self', false);
}

function createMap(latitude, longitude) {
//   const google = window.google;
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: latitude, lng: longitude}, zoom: 10});
  addLandmark(map, latitude, longitude);
}

/**
 * Adds a marker to the map.
 */
function addLandmark(map, lat, lng) {
  const google = window.google;
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map});
}