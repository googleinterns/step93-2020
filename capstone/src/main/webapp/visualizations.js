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
 * Gets the page view data using the PageViewServlet.
 */
async function getPageViewData() {
  // Fetch data from servlet
  const responsePath = '/page-view';
  const response = await fetch(responsePath);
  return await response.json();
}

/**
 * Parses the page view data into the desired format for the
 * multi-line chart visualization.
 * @return data object of the form:
 * {
 *     restaurantData: [
 *         {
 *             restaurantName: String,
 *             clickData: [int(# page views that week), int, int, ...], lines up
 * with dates array
 *         },
 *         ...
 *     ],
 *     dates: [Date, Date(one week later), ..., Date(this week)],
 * }
 */
async function parseData() {
  const jsonResponse = await getPageViewData();
  let pageViews = jsonResponse.pageViews;
  pageViews = JSON.parse(pageViews);

  const firstDate = getFirstDate(pageViews);
  const firstDateObj = getDateFromWeekYear(firstDate[0], firstDate[1]);
  const currDate = new Date();
  const dateArray = getFullDateArray(firstDateObj, currDate);

  return setUpVisualizationData(pageViews, dateArray);
}

/**
 * Gets the earliest date with data based on the pageViews array.
 * @param pageViews
 * @return an array containing minWeek and minYear
 */
function getFirstDate(pageViews) {
  // Start the variables at the first data point values
  let minWeek = pageViews[0].week;
  let minYear = pageViews[0].year;

  // Check through rest of the data points to see if any of the
  // dates are earlier
  for (let i = 1; i < pageViews.length; i++) {
    const currPageView = pageViews[i];
    if (currPageView.year < minYear) {
      minYear = currPageView.year;
      minWeek = currPageView.week;
    } else if (currPageView.week < minWeek && currPageView.year === minYear) {
      minWeek = currPageView.week;
    }
  }

  return [minWeek, minYear];
}

/**
 * Get a javascript Date object based on a week and year
 * @param week
 * @param year
 * @return {Date}
 */
function getDateFromWeekYear(week, year) {
  // January 1st plus 7 days for each week
  // Weeks of year start at 1 instead of 0, hence the -1
  const day = 1 + (week - 1) * 7;
  return new Date(year, 0, day);
}

/**
 * Get an array of Dates going weekly from firstDate to currDate
 * @param firstDate
 * @param currDate
 * @return array of Dates, starting at firstDate and advancing weekly
 *         until currDate
 */
function getFullDateArray(firstDate, currDate) {
  const dates = [];
  const DAYS_IN_WEEK = 7;
  // Loop through, adding 7 each time for the next week
  for (let date = firstDate; date <= currDate;
    date.setDate(date.getDate() + DAYS_IN_WEEK)) {
    dates.push(new Date(date));
  }
  return dates;
}

/**
 * Set up the final visualization data array based on all pageViews data and
 * formatted datesArr
 * @param pageViews
 * @param datesArr
 * @return dates object of the form:
 * {
 *     restaurantData: [
 *         {
 *             restaurantName: String,
 *             clickData: [int(# page views that week), int, int, ...], lines up
 * with dates array
 *         },
 *         ...
 *     ],
 *     dates: [Date, Date(one week later), ..., Date(this week)],
 * }
 */
function setUpVisualizationData(pageViews, datesArr) {
  const data = {
    restaurantData: [],
    dates: datesArr,
  };

  const firstDate = datesArr[0];

  for (let i = 0; i < pageViews.length; i++) {
    const currPageView = pageViews[i];
    const restaurantName = currPageView.restaurantName;
    const week = currPageView.week;
    const year = currPageView.year;
    const numClicks = currPageView.numClicks;

    // Attempt to find the current restaurant's object in the restaurantData
    // array
    let index = data.restaurantData.findIndex(
        ((obj) => obj.restaurantName === restaurantName));
    if (index === -1) {
      // No object yet for this restaurant: create one
      // Click data for each possible week starts at 0 before populating it
      const restaurantObject = {
        restaurantName: restaurantName,
        clickData: new Array(datesArr.length).fill(0),
      };
      data.restaurantData.push(restaurantObject);
      index = data.restaurantData.length - 1;
    }
    // Calculate weeks between first available date and current page view data
    // date
    const currPageViewDate = getDateFromWeekYear(week, year);
    const weeksBetween = getNumWeeksBetween(firstDate, currPageViewDate);
    // Update the current restaurant's clickData array at the proper index with
    // the number of clicks
    const currRestaurantObj = data.restaurantData[index];
    currRestaurantObj.clickData[weeksBetween] = numClicks;
  }

  return data;
}

/**
 * Calculate the number of weeks between two Dates
 * @param date1
 * @param date2
 * @return {number}
 */
function getNumWeeksBetween(date1, date2) {
  const MILLISECONDS_ONE_WEEK = 7 * 24 * 60 * 60 * 1000;
  return Math.round((date2 - date1) / MILLISECONDS_ONE_WEEK);
}

