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
 * Gets the restaurants using the RestaurantServlet
 * and adds them to the site interface.
 */
async function getRestaurants() {
  // Fetch restaurants from servlet
  const responsePath = '/restaurants';
  const response = await fetch(responsePath);
  const resp = await response.json();

  const restaurantArea = document.getElementById('restaurants-list');
  if (restaurantArea !== null && resp.restaurants !== null) {
    // Retrieve and parse restaurants JSON from get restaurants response
    let restaurants = resp.restaurants;
    restaurants = JSON.parse(restaurants);
    // Clear restaurant area in case page is being reloaded
    restaurantArea.innerHTML = '';

    // Append restaurants to page
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

  // Restaurant element UI details
  const title = document.createElement('h2');
  title.innerText = restaurant.name;
  restaurantElement.appendChild(title);

  restaurantElement.addEventListener('click', () => {
    // Redirect to restaurant detail page with correct restaurant key
    const redirect =
        '/restaurantDetails.html?restaurantKey=' + restaurant.restaurantKey;
    window.location.href = redirect;
  });
  return restaurantElement;
}
