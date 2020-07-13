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
  const name = document.getElementById('name').value;
  const cuisine = document.getElementById('cuisine').value;
  const story = document.getElementById('story').value;
  const phone = document.getElementById('phone').value;
  const website = document.getElementById('website').value;

  const params = new URLSearchParams();
  params.append('name', name);
  params.append('cuisine', cuisine);
  params.append('story', story);
  params.append('phone', phone);
  params.append('website', website);

  fetch('/restaurant', {method: 'POST', body: params});
}
