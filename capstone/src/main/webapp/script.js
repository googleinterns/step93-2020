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


function getRestaurants() {
  fetch('/restaurants').then(function(response) {
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

function createRestaurantNameDiv(restaurant) {
  const params = new URLSearchParams();
  params.append('restaurantKey', restaurant.restaurantKey);

  const linkElement = document.createElement('a');
  linkElement.classList.add('restaurant-name-link');
  linkElement.innerText = restaurant.name;
  linkElement.href = '/restaurantDetails?' + params;

  const restaurantNameHeader = document.createElement('h3');
  restaurantNameHeader.appendChild(linkElement);

  const restaurantNameDiv = document.createElement('div');
  restaurantNameDiv.classList.add(
      'restaurant-name-container', 'mdc-typography--headline6');
  restaurantNameDiv.appendChild(restaurantNameHeader);

  return restaurantNameDiv;
}

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
