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
 * Gets the information from the textboxes to send to datastore.
 */
function sendSignUpInfo() {
  const name = document.getElementById('new-restaurant-name').value;
  const cuisine = document.getElementById('new-restaurant-cuisine').value;
  const story = document.getElementById('new-restaurant-story').value;
  const phone = document.getElementById('new-restaurant-phone').value;
  const website = document.getElementById('new-restaurant-website').value;

  // Check if any area doesn't contain any text.
  if (name === '' || cuisine === '' || story === '' || phone === '' ||
      website === '') {
    const pElement = createP('You must fill all text boxes!');
    const error = document.getElementById('missing-information-error');
    error.appendChild(pElement);
  } else {
    // Build params for POST request.
    const params = new URLSearchParams();
    params.append('name', name);
    params.append('cuisine', cuisine);
    params.append('story', story);
    params.append('phone', phone);
    params.append('website', website);

    fetch('/restaurant', {method: 'POST', body: params})
        .then(function(response) {
          if (response.ok) {
            window.location.href = '/';
          } else {
            const errorElement =
                createP('Request failed. Please make sure you are logged in.');
            const error = document.getElementById('missing-information-error');
            error.appendChild(errorElement);
          }
        });
  }
}

/**
 * Create a p tag to add text to an area.
 * @param content that I want to be shown as text.
 * @return {HTMLParagraphElement} Completed p tag with text.
 */
function createP(content) {
  const pTag = document.createElement('p');
  const node = document.createTextNode(content);
  pTag.appendChild(node);
  return pTag;
}

// /**
//  * Return to home landing page.
//  */
// function returnHome() {
//   window.location.href = '/';
// }
