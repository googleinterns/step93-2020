function setRestaurantDetailsPage(currRestaurant) {
  //console.log(window.document);
  //window.document.title.innerText = restaurant.name;
  document.getElementById('title').innerText = currRestaurant.name;
  document.getElementById('story').innerText = currRestaurant.story;
  const location = currRestaurant.location;
  const lat = location.latitude
  const long = location.longitude;
  createMap(lat, long);
}

function returnHome() {
  window.open('/', '_self', false);
}

function createMap(latitude, longitude) {
  const google = window.google;
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