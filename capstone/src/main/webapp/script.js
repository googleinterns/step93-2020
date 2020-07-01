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

// mdc.ripple.MDCRipple.attachTo(document.querySelector('.foo-button'));

function getRestaurants() {
  fetch('/get-restaurants').then(function(response) {
    let restaurantsList = document.getElementById('restaurants-list');

    if (response.ok) {
      response.json().then((restaurants) => {
        restaurantsList.innerHTML = '';

        restaurants.forEach((restaurant) => {
          addRestaurant(restaurant, restaurantsList);
        });
      })
    }
  });
}

function addRestaurant(restaurant, containerElement) {
  let restaurantDiv = document.createElement('div');
  restaurantDiv.classList.add('mdc-card', 'restaurant-container');

  let restaurantNameDiv = createRestaurantNameDiv(restaurant);
  let restaurantCuisineDiv = createRestaurantCuisineDiv(restaurant);
  let restaurantStrugglingDiv = createRestaurantIsStrugglingDiv(restaurant);

  restaurantDiv.appendChild(restaurantNameDiv);
  restaurantDiv.appendChild(restaurantCuisineDiv);
  restaurantDiv.appendChild(restaurantStrugglingDiv);

  containerElement.appendChild(restaurantDiv);
}

function createRestaurantNameDiv(restaurant) {
  let restaurantNameDiv = document.createElement('div');
  restaurantNameDiv.classList.add('restaurant-name-container', 'mdc-typography--headline6');
  restaurantNameDiv.innerHTML = '<h3>' + restaurant.name + '</h3>';

  return restaurantNameDiv;
}

function createRestaurantCuisineDiv(restaurant) {
  let restaurantCuisineDiv = document.createElement('div');
  restaurantCuisineDiv.classList.add('restaurant-cuisine-container');
  restaurant.cuisine.forEach((item) => {
    let cuisineItemDiv = document.createElement('div');
    cuisineItemDiv.innerHTML = '<p>' + item + '</p>';
    cuisineItemDiv.classList.add('mdc-button');
    restaurantCuisineDiv.appendChild(cuisineItemDiv);
  });

  return restaurantCuisineDiv;
}

function createRestaurantIsStrugglingDiv(restaurant) {
  let restaurantStrugglingDiv = document.createElement('div');
  restaurantStrugglingDiv.classList.add('restaurant-struggling-container');
  if (restaurant.isStruggling) {
    restaurantStrugglingDiv.innerHTML = '<p>This restaurant needs help!</p>'
  } else {
    restaurantStrugglingDiv.innerHTML = '<p>Keep this restaurant growing!</p>'
  }

  return restaurantStrugglingDiv;
}
