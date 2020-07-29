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


/**
 * Populate `restaurants-list` HTML element with cards holding brief pieces of
 * restaurant data
 */
function getRestaurants() {
  const params = new URLSearchParams();
  params.append("query", "");

  fetch('/search', {method: 'GET', body: params}).then(function(response) {
    const restaurantsList = document.getElementById('restaurants-list');

    if (response.ok) {
      response.json().then((responseJson) => {
        restaurantsList.innerHTML = '';
        responseJson.restaurants.forEach((restaurant) => {
          addRestaurant(restaurant, restaurantsList);
        });
      });
    }
  });
}

/**
 * Creates a Material card holding restaurant data and adds it to an outer HTML
 * element
 * @param restaurant  the subject restaurant
 * @param containerElement  the HTML element that will hold the restaurant
 */
function addRestaurant(restaurant, containerElement) {
  const restaurantDiv = document.createElement('div');
  restaurantDiv.classList.add('mdc-card', 'restaurant-container');

  const restaurantNameDiv = createRestaurantNameDiv(restaurant);
  const restaurantCuisineDiv = createRestaurantCuisineDiv(restaurant);
  const restaurantStrugglingDiv = createRestaurantIsStrugglingDiv(restaurant);

  restaurantDiv.appendChild(restaurantNameDiv);
  restaurantDiv.appendChild(restaurantCuisineDiv);
  restaurantDiv.appendChild(restaurantStrugglingDiv);

  containerElement.appendChild(restaurantDiv);
}

/**
 * Creates a div containing the name of a given restaurant
 * @param restaurant  the subject restaurant
 * @return {HTMLDivElement} div containing a link to a restaurant's details page
 */
function createRestaurantNameDiv(restaurant) {
  const params = new URLSearchParams();
  params.append('restaurantKey', restaurant.restaurantKey);

  const linkElement = document.createElement('a');
  linkElement.classList.add('restaurant-name-link');
  linkElement.innerText = restaurant.name;
  linkElement.href = '/restaurantDetails.html?' + params;

  const restaurantNameHeader = document.createElement('h3');
  restaurantNameHeader.appendChild(linkElement);

  const restaurantNameDiv = document.createElement('div');
  restaurantNameDiv.classList.add(
      'restaurant-name-container', 'mdc-typography--headline6');
  restaurantNameDiv.appendChild(restaurantNameHeader);

  return restaurantNameDiv;
}

/**
 * Creates a div containing a list of buttons representing restaurant cuisines
 * @param restaurant  the subject restaurant
 * @return {HTMLDivElement} div holding all of the cuisine buttons
 */
function createRestaurantCuisineDiv(restaurant) {
  const restaurantCuisineDiv = document.createElement('div');
  restaurantCuisineDiv.classList.add('restaurant-cuisine-container');
  restaurant.cuisine.forEach((item) => {
    const cuisineItemButton = document.createElement('button');
    cuisineItemButton.innerHTML = item;
    cuisineItemButton.classList.add('mdc-button', 'mdc-ripple-surface');
    restaurantCuisineDiv.appendChild(cuisineItemButton);
  });

  return restaurantCuisineDiv;
}

/**
 * Creates a div that displays a message based on whether a restaurant is
 * struggling or not
 * @param restaurant  the subject restaurant
 * @return {HTMLDivElement} a div holding a message about the restaurant status
 */
function createRestaurantIsStrugglingDiv(restaurant) {
  const restaurantStrugglingDiv = document.createElement('div');
  restaurantStrugglingDiv.classList.add('restaurant-struggling-container');
  if (restaurant.isStruggling) {
    restaurantStrugglingDiv.innerHTML = '<p>This restaurant needs help!</p>';
  } else {
    restaurantStrugglingDiv.innerHTML = '<p>Keep this restaurant growing!</p>';
  }

  return restaurantStrugglingDiv;
}

/**
 * Queries LoginServlet to see the current login status and updates available
 * buttons accordingly. When the user is logged in, they can see the restaurant
 * signup button.
 */
function getLoginState() {
  fetch('/login').then(function(response) {
    const loginButton = document.getElementById('login-button');
    const loginButtonIcon = document.getElementById('login-button-icon');
    const loginButtonText = document.getElementById('login-button-text');
    const restaurantSignupButton =
        document.getElementById('create-restaurant-button');

    if (response.ok) {
      response.json().then((responseJson) => {
        if (responseJson.loggedIn) {
          loginButtonIcon.innerText = 'logout';
          loginButtonText.innerText = 'Logout';
          loginButton.href = responseJson.logOutURL;
          restaurantSignupButton.style.display = 'block';
        } else {
          loginButtonIcon.innerText = 'login';
          loginButtonText.innerText = 'Login';
          loginButton.href = responseJson.loginURL;
          restaurantSignupButton.style.display = 'none';
        }
      });
    }
  });
}

/**
 * Setup the home page by getting the restaurants
 * to display and getting the login state.
 */
function setHomePage() {
  getRestaurants();
  getLoginState();
}
