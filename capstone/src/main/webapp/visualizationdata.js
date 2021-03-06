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
 *                        with dates array
 *         },
 *         ...
 *     ],
 *     dates: [Date, Date(one week later), ..., Date(this week)],
 * }
 */
async function parseData() {
  const restaurantPageViews = await getPageViewData();

  const firstDate = getFirstDate(restaurantPageViews);
  if (firstDate.minWeek === null) {
    // restaurantPageViews was empty: no data for d3 chart
    return {
      'restaurantData': [],
      'dates': [],
    };
  }
  const firstDateObj =
      getDateFromWeekYear(firstDate.minWeek, firstDate.minYear);
  const currDate = new Date();
  const dateArray = getFullDateArray(firstDateObj, currDate);

  return setUpVisualizationData(restaurantPageViews, dateArray);
}

/**
 * Gets the earliest date with data based on the pageViews array.
 * @param restaurantPageViews
 * @return an object containing minWeek and minYear, or an object with null
 *     values if no data was supplied
 */
function getFirstDate(restaurantPageViews) {
  // Check for if there is no data stored yet
  if (restaurantPageViews == null || restaurantPageViews.length <= 0) {
    return {'minWeek': null, 'minYear': null};
  }

  // Start the variables at the first data point values
  // Assumes any populated restaurantPageViews member will have at least one
  // element in its pageView array, which is guaranteed by the backend
  let minWeek = restaurantPageViews[0].pageViews[0].week;
  let minYear = restaurantPageViews[0].pageViews[0].year;

  // Check through rest of the first data points in other restaurants'
  // pageViews arrays to see if any of the dates are earlier
  // Assumes each RestaurantPageView pageViews member is sorted in ascending
  // order, which is guaranteed by the backend
  for (let i = 1; i < restaurantPageViews.length; i++) {
    const currRestaurantMinPageView = restaurantPageViews[i].pageViews[0];
    if (currRestaurantMinPageView.year < minYear) {
      minYear = currRestaurantMinPageView.year;
      minWeek = currRestaurantMinPageView.week;
    } else if (
        currRestaurantMinPageView.week < minWeek &&
        currRestaurantMinPageView.year === minYear) {
      minWeek = currRestaurantMinPageView.week;
    }
  }

  return {'minWeek': minWeek, 'minYear': minYear};
}

/**
 * Get a javascript Date object based on a week and year. The date is
 * calculated by adding 7 days for each week to January 1, then subtracting
 * the day number to ensure the returned date is the Sunday of that week.
 * @param week
 * @param year
 * @return {Date}
 */
function getDateFromWeekYear(week, year) {
  if (week < 0 || year < 0) {
    return null;
  }
  // January 1st plus 7 days for each week
  // Weeks of year start at 1 instead of 0, hence the -1
  const day = 1 + (week - 1) * 7;
  const date = new Date(year, 0, day);
  // Subtract day to ensure returned date is a Sunday
  date.setDate(date.getDate() - date.getDay());
  return date;
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
 * Set up the final visualization data array based on all restaurantPageViews
 * data and formatted datesArr
 * @param restaurantPageViews
 * @param datesArr
 * @return dates object of the form:
 * {
 *     restaurantData: [
 *         {
 *             restaurantName: String,
 *             clickData: [int(# page views that week), int, int, ...], lines up
 *                        with dates array
 *         },
 *         ...
 *     ],
 *     dates: [Date, Date(one week later), ..., Date(this week)],
 * }
 */
function setUpVisualizationData(restaurantPageViews, datesArr) {
  const data = {
    restaurantData: [],
    dates: datesArr,
  };

  const firstDate = datesArr[0];

  for (let i = 0; i < restaurantPageViews.length; i++) {
    const currPageViewArray = restaurantPageViews[i].pageViews;
    const restaurantName = restaurantPageViews[i].name;

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

    for (let j = 0; j < currPageViewArray.length; j++) {
      const week = currPageViewArray[j].week;
      const year = currPageViewArray[j].year;
      const numClicks = currPageViewArray[j].count;

      // Calculate weeks between first available date and current page view data
      // date
      const currPageViewDate = getDateFromWeekYear(week, year);
      const weeksBetween = getNumWeeksBetween(firstDate, currPageViewDate);
      // Update the current restaurant's clickData array at the proper index
      // with the number of clicks
      const currRestaurantObj = data.restaurantData[index];
      currRestaurantObj.clickData[weeksBetween] = numClicks;
    }
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
  return Math.round(Math.abs(date2 - date1) / MILLISECONDS_ONE_WEEK);
}

/**
 * Returns data for the bar chart visualization.
 * @return data object of the form:
 * {
 *     'data': [
 *          [last week's page views for restaurant x1, last week's page views
 * for restaurant x2, ...] [average page views for restaurant x1, average page
 * views for restaurant x2, ...],
 *     ],
 *     'restaurantNames': [x1, x2, ...]
 * }
 */
async function parseBarChartData() {
  const restaurantPageViews = await getPageViewData();

  const averages = getAveragePageViewArray(restaurantPageViews);
  const lastWeekVals = getLastWeekPageViewArray(restaurantPageViews);
  const data = [lastWeekVals, averages];

  const restaurantNames = getRestaurantNames(restaurantPageViews);

  return {
    'data': data,
    'restaurantNames': restaurantNames,
  };
}

/**
 * Returns an array of the average page views for each restaurant.
 * @param restaurantPageViews
 * @return array of averages or empty array if no data
 */
function getAveragePageViewArray(restaurantPageViews) {
  // Check for if there is no data stored yet
  if (restaurantPageViews == null || restaurantPageViews.length <= 0) {
    return [];
  }

  const averagePageViews = [];
  // Iterate through all page view data for each restaurant
  // and push average for each to return array
  for (let i = 0; i < restaurantPageViews.length; i++) {
    const currRestaurantPageViews = restaurantPageViews[i].pageViews;
    let sum = 0;
    for (let j = 0; j < currRestaurantPageViews.length; j++) {
      sum += currRestaurantPageViews[j].count;
    }
    const count = currRestaurantPageViews.length;
    let average = 0;
    if (count !== 0) {
      average = sum / count;
    }
    averagePageViews.push(average);
  }

  return averagePageViews;
}

/**
 * Returns an array of the last week's page views for each restaurant.
 * @param restaurantPageViews
 * @return array of page view counts or empty array if no data
 */
function getLastWeekPageViewArray(restaurantPageViews) {
  // Check for if there is no data stored yet
  if (restaurantPageViews == null || restaurantPageViews.length <= 0) {
    return [];
  }

  // Calculate current week number
  const currDate = new Date();
  const currWeek =
      getNumWeeksBetween(new Date(currDate.getFullYear(), 0, 1), currDate);

  const lastWeekPageViews = [];
  // Iterate through all page view data for each restaurant
  // and push last week's page view data
  for (let i = 0; i < restaurantPageViews.length; i++) {
    const currRestaurantPageViews = restaurantPageViews[i].pageViews;
    let found = false;
    // Need to check last two elements, since we could have data for this
    // week and last week, could only have data for last week, or could have
    // data for neither
    for (let j = currRestaurantPageViews.length - 2;
         j < currRestaurantPageViews.length; j++) {
      if (j >= 0 && currRestaurantPageViews[j].week === (currWeek - 1)) {
        // We found last week's data
        lastWeekPageViews.push(currRestaurantPageViews[j].count);
        found = true;
        break;
      }
    }
    // If had no data for last week, insert a 0
    if (!found) {
      lastWeekPageViews.push(0);
    }
  }

  return lastWeekPageViews;
}

/**
 * Returns an array of the names of each restaurant.
 * @param restaurantPageViews
 * @return array of names or empty array if no data
 */
function getRestaurantNames(restaurantPageViews) {
  // Check for if there is no data stored yet
  if (restaurantPageViews == null || restaurantPageViews.length <= 0) {
    return [];
  }

  const restaurantNames = [];
  // Iterate through all restaurants and add names to array
  for (let i = 0; i < restaurantPageViews.length; i++) {
    restaurantNames.push(restaurantPageViews[i].name);
  }

  return restaurantNames;
}


// Export the functions for the Jest testing
module.exports = {
  getFirstDate,
  getDateFromWeekYear,
  getFullDateArray,
  getNumWeeksBetween,
  setUpVisualizationData,
  parseBarChartData,
  getAveragePageViewArray,
  getLastWeekPageViewArray,
  getRestaurantNames,
};
