/** Tests for the various data parsing methods in visualizationdata.js */

import {beforeEach} from '@jest/globals';

const functions = require('./visualizationdata');

let pageViewExample1;
let pageViewExample2;
let pageViewExample3;
let visualizationDataExample1;
let visualizationDataExample2;
let dateArr1;
let dateArr2;

beforeEach(() => {
  // Set up all example data values
  pageViewExample1 = [
    {
      'name': 'Wildfire',
      'pageViews': [
        {
          'week': 10,
          'year': 2020,
          'count': 2,
        },
        {
          'week': 11,
          'year': 2020,
          'count': 10,
        },
      ],
    },
    {
      'name': 'Poke Doke',
      'pageViews': [
        {
          'week': 11,
          'year': 2020,
          'count': 8,
        },
      ],
    },
  ];

  pageViewExample2 = [
    {
      'name': 'The Goog Noodle',
      'pageViews': [
        {
          'week': 11,
          'year': 2020,
          'count': 2,
        },
      ],
    },
    {
      'name': 'Wildfire',
      'pageViews': [
        {
          'week': 11,
          'year': 2020,
          'count': 10,
        },
      ],
    },
    {
      'name': 'Clucker\'s',
      'pageViews': [
        {
          'week': 11,
          'year': 2020,
          'count': 8,
        },
        {
          'week': 13,
          'year': 2020,
          'count': 18,
        },
      ],
    },
  ];

  pageViewExample3 = [
    {
      'name': 'Wildfire',
      'pageViews': [
        {
          'week': 10,
          'year': 2020,
          'count': 2,
        },
        {
          'week': 11,
          'year': 2020,
          'count': 10,
        },
      ],
    },
    {
      'name': 'Poke Doke',
      'pageViews': [
        {
          'week': 25,
          'year': 2019,
          'count': 8,
        },
      ],
    },
  ];

  dateArr1 = [new Date(2020, 2, 4), new Date(2020, 2, 11)];
  dateArr2 =
      [new Date(2020, 2, 11), new Date(2020, 2, 18), new Date(2020, 2, 25)];

  visualizationDataExample1 = {
    'restaurantData': [
      {
        'restaurantName': 'Wildfire',
        'clickData': [2, 10],
      },
      {
        'restaurantName': 'Poke Doke',
        'clickData': [0, 8],
      },
    ],
    'dates': dateArr1,
  };

  visualizationDataExample2 = {
    'restaurantData': [
      {
        'restaurantName': 'The Goog Noodle',
        'clickData': [2, 0, 0],
      },
      {
        'restaurantName': 'Wildfire',
        'clickData': [10, 0, 0],
      },
      {
        'restaurantName': 'Clucker\'s',
        'clickData': [8, 0, 18],
      },
    ],
    'dates': dateArr2,
  };
});

/** Tests for getFirstDate function to ensure it finds the correct minimum */
test('Test getFirstDate with empty restaurantPageViews data', () => {
  expect(functions.getFirstDate([]))
      .toMatchObject({'minWeek': null, 'minYear': null});
});

test(
    'Test getFirstDate with simple restaurantPageViews with two date options',
    () => {
      expect(functions.getFirstDate(pageViewExample1))
          .toMatchObject({'minWeek': 10, 'minYear': 2020});
    });

test('Test getFirstDate with restaurantPageViews data that skips weeks', () => {
  expect(functions.getFirstDate(pageViewExample2))
      .toMatchObject({'minWeek': 11, 'minYear': 2020});
});

test(
    'Test getFirstDate with restaurantPageViews data in different years',
    () => {
      expect(functions.getFirstDate(pageViewExample3))
          .toMatchObject({'minWeek': 25, 'minYear': 2019});
    });

/**
 * Tests for getDateFromWeekYear to ensure it finds the correct Date from
 * January 1 based on checking the calendar. All dates are Sundays of the
 * week requested.
 */
test('Test getDateFromWeekYear with a week in January 2020', () => {
  expect(functions.getDateFromWeekYear(4, 2020))
      .toMatchObject(new Date(2020, 0, 19));
});

test('Test getDateFromWeekYear with the first week of 2020', () => {
  expect(functions.getDateFromWeekYear(1, 2020))
      .toMatchObject(new Date(2019, 11, 29));
});

test('Test getDateFromWeekYear with a week in June 2020', () => {
  expect(functions.getDateFromWeekYear(26, 2020))
      .toMatchObject(new Date(2020, 5, 21));
});

test('Test getDateFromWeekYear with a negative week', () => {
  expect(functions.getDateFromWeekYear(-1, 2020)).toBe(null);
});

test('Test getDateFromWeekYear with a negative year', () => {
  expect(functions.getDateFromWeekYear(2, -50)).toBe(null);
});

/**
 * Tests for getFullDateArray with different cases of crossing across
 * years/months, etc
 */
test('Test getFullDateArray with two consecutive weeks', () => {
  const expected1 = [new Date(2020, 6, 15), new Date(2020, 6, 22)];
  expect(
      functions.getFullDateArray(new Date(2020, 6, 15), new Date(2020, 6, 22)))
      .toMatchObject(expected1);
});

test('Test getFullDateArray with three weeks', () => {
  const expected2 =
      [new Date(2020, 6, 1), new Date(2020, 6, 8), new Date(2020, 6, 15)];
  expect(
      functions.getFullDateArray(new Date(2020, 6, 1), new Date(2020, 6, 21)))
      .toMatchObject(expected2);
});

test('Test getFullDateArray with crossing into a new year', () => {
  const expected3 =
      [new Date(2019, 11, 25), new Date(2020, 0, 1), new Date(2020, 0, 8)];
  expect(
      functions.getFullDateArray(new Date(2019, 11, 25), new Date(2020, 0, 9)))
      .toMatchObject(expected3);
});

/**
 * Tests getNumWeeksBetween with different edge cases of same dates, crossing
 * years/months, etc
 */
test('Test getNumWeeksBetween with exactly a week difference', () => {
  expect(functions.getNumWeeksBetween(
             new Date(2020, 6, 21), new Date(2020, 6, 28)))
      .toBe(1);
});

test('Test getNumWeeksBetween across a month boundary', () => {
  expect(
      functions.getNumWeeksBetween(new Date(2020, 6, 21), new Date(2020, 7, 4)))
      .toBe(2);
});

test('Test getNumWeeksBetween with the same date', () => {
  expect(functions.getNumWeeksBetween(
             new Date(2020, 6, 21), new Date(2020, 6, 21)))
      .toBe(0);
});

test('Test getNumWeeksBetween across a year boundary', () => {
  expect(functions.getNumWeeksBetween(
             new Date(2019, 11, 22), new Date(2020, 0, 19)))
      .toBe(4);
});

test('Test getNumWeeksBetween with the later date as the first param', () => {
  expect(functions.getNumWeeksBetween(
             new Date(2020, 6, 28), new Date(2020, 6, 21)))
      .toBe(1);
});

test('Test getNumWeeksBetween across a month boundary', () => {
  expect(
      functions.getNumWeeksBetween(new Date(2020, 7, 4), new Date(2020, 6, 21)))
      .toBe(2);
});

/** Tests setUpVisualizationData using all available preset data */
test('Test setUpVisualizationData with two restaurants and two weeks', () => {
  expect(functions.setUpVisualizationData(pageViewExample1, dateArr1))
      .toMatchObject(visualizationDataExample1);
});

test(
    'Test setUpVisualizationData with three restaurants and three weeks',
    () => {
      expect(functions.setUpVisualizationData(pageViewExample2, dateArr2))
          .toMatchObject(visualizationDataExample2);
    });

/** Tests getAveragePageViewArray using all available preset data */
test('Test getAveragePageViewArray 1', () => {
  expect(functions.getAveragePageViewArray(pageViewExample1)).toMatchObject([
    6,
    8,
  ]);
});

test('Test getAveragePageViewArray 2', () => {
  expect(functions.getAveragePageViewArray(pageViewExample2)).toMatchObject([
    2,
    10,
    13,
  ]);
});

/** Tests getRestaurantNames using all available preset data */
test('Test getRestaurantNames 1', () => {
  expect(functions.getRestaurantNames(pageViewExample1)).toMatchObject([
    'Wildfire',
    'Poke Doke',
  ]);
});

test('Test getRestaurantNames 2', () => {
  expect(functions.getRestaurantNames(pageViewExample2)).toMatchObject([
    'The Goog Noodle',
    'Wildfire',
    'Clucker\'s',
  ]);
});
