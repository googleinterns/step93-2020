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
const mdc = window.mdc;
const drawer = mdc.drawer.MDCDrawer
    .attachTo(document.querySelector('.mdc-drawer'));
const topAppBar = mdc.topAppBar.MDCTopAppBar
    .attachTo(document.getElementById('app-bar'));

topAppBar.setScrollTarget(document.getElementById('main-content'));
topAppBar.listen('MDCTopAppBar:nav', () => {
  drawer.open = !drawer.open;
});

// Change focus when navigation drawer opened
const listEl = document.querySelector('.mdc-drawer .mdc-list');
const mainContentEl = document.querySelector('.main-content');

listEl.addEventListener('click', (event) => {
  drawer.open = false;
});

document.body.addEventListener('MDCDrawer:closed', () => {
  mainContentEl.querySelector('input, button').focus();
});
