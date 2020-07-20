/** Tests for the various data parsing methods in visualizations.js */

import {beforeEach} from "@jest/globals";

const functions = require('./visualizations');

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
            "restaurantName": "Wildfire",
            "week": 10,
            "year": 2020,
            "numClicks": 2,
        },
        {
            "restaurantName": "Wildfire",
            "week": 11,
            "year": 2020,
            "numClicks": 10,
        },
        {
            "restaurantName": "Poke Doke",
            "week": 11,
            "year": 2020,
            "numClicks": 8,
        },
    ];

    pageViewExample2 = [
        {
            "restaurantName": "The Goog Noodle",
            "week": 11,
            "year": 2020,
            "numClicks": 2,
        },
        {
            "restaurantName": "Wildfire",
            "week": 11,
            "year": 2020,
            "numClicks": 10,
        },
        {
            "restaurantName": "Clucker's",
            "week": 11,
            "year": 2020,
            "numClicks": 8,
        },
        {
            "restaurantName": "Clucker's",
            "week": 13,
            "year": 2020,
            "numClicks": 18,
        },
    ];

    pageViewExample3 = [
        {
            "restaurantName": "Wildfire",
            "week": 10,
            "year": 2020,
            "numClicks": 2,
        },
        {
            "restaurantName": "Wildfire",
            "week": 11,
            "year": 2020,
            "numClicks": 10,
        },
        {
            "restaurantName": "Poke Doke",
            "week": 25,
            "year": 2019,
            "numClicks": 8,
        },
    ];

    dateArr1 = [new Date(2020, 2, 4), new Date(2020, 2, 11)];
    dateArr2 = [new Date(2020, 2, 11), new Date(2020, 2, 18), new Date(2020, 2, 25)];

    visualizationDataExample1 = {
        "restaurantData": [
            {
                "restaurantName": "Wildfire",
                "clickData": [2, 10],
            },
            {
                "restaurantName": "Poke Doke",
                "clickData": [0, 8],
            },
        ],
        "dates": dateArr1,
    };

    visualizationDataExample2 = {
        "restaurantData": [
            {
                "restaurantName": "The Goog Noodle",
                "clickData": [2, 0, 0],
            },
            {
                "restaurantName": "Wildfire",
                "clickData": [10, 0, 0],
            },
            {
                "restaurantName": "Clucker's",
                "clickData": [8, 0, 18],
            },
        ],
        "dates": dateArr2,
    };

});

/** Test for getFirstDate function to ensure it finds the correct minimum */
test('Test getFirstDate with different inputs', () => {
    // Empty dataset provided
    expect(functions.getFirstDate([])).toMatchObject([]);

    // Tests with different month/year edge cases
    expect(functions.getFirstDate(pageViewExample1)).toMatchObject([10, 2020]);
    expect(functions.getFirstDate(pageViewExample2)).toMatchObject([11, 2020]);
    expect(functions.getFirstDate(pageViewExample3)).toMatchObject([25, 2019]);
});

/** Test for getDateFromWeekYear to ensure it finds the correct Date from January 1 based on checking the calendar */
test('Test getDateFromWeekYear with different inputs', () => {
    expect(functions.getDateFromWeekYear(4, 2020)).toMatchObject(new Date(2020, 0, 22));
    expect(functions.getDateFromWeekYear(1, 2020)).toMatchObject(new Date(2020, 0, 1));
    expect(functions.getDateFromWeekYear(26, 2020)).toMatchObject(new Date(2020, 5, 24));

    // Return null for negative year or week values
    expect(functions.getDateFromWeekYear(-1, 2020)).toBe(null);
    expect(functions.getDateFromWeekYear(2, -50)).toBe(null);
});

/** Test for getFullDateArray with different cases of crossing across years/months, etc */
test('Test getFullDateArray with different inputs', () => {
    const expected1 = [new Date(2020, 6, 15), new Date(2020, 6, 22)];
   expect(functions.getFullDateArray(new Date(2020, 6, 15), new Date(2020, 6, 22))).toMatchObject(expected1);
    const expected2 = [new Date(2020, 6, 1), new Date(2020, 6, 8), new Date(2020, 6, 15)];
   expect(functions.getFullDateArray(new Date(2020, 6, 1), new Date(2020, 6, 21))).toMatchObject(expected2);
   const expected3 = [new Date(2019, 11, 25), new Date(2020, 0, 1), new Date(2020, 0, 8)];
    expect(functions.getFullDateArray(new Date(2019, 11, 25), new Date(2020, 0, 9))).toMatchObject(expected3);
});

/** Test getNumWeeksBetween with different edge cases of same dates, crossing years/months, etc */
test('Test getNumWeeksBetween with different inputs', () => {
    expect(functions.getNumWeeksBetween(new Date(2020, 6, 21), new Date(2020, 6, 28))).toBe(1);
    expect(functions.getNumWeeksBetween(new Date(2020, 6, 21), new Date(2020, 7, 4))).toBe(2);
    expect(functions.getNumWeeksBetween(new Date(2020, 6, 21), new Date(2020, 6, 21))).toBe(0);
    expect(functions.getNumWeeksBetween(new Date(2019, 11, 22), new Date(2020, 0, 19))).toBe(4);

    // Should work in reverse param order also
    expect(functions.getNumWeeksBetween(new Date(2020, 6, 28), new Date(2020, 6, 21))).toBe(1);
    expect(functions.getNumWeeksBetween(new Date(2020, 7, 4), new Date(2020, 6, 21))).toBe(2);
});

/** Test setUpVisualizationData using all available preset data */
test('Test setUpVisualizationData with different inputs', () => {
    expect(functions.setUpVisualizationData(pageViewExample1, dateArr1)).toMatchObject(visualizationDataExample1);
    expect(functions.setUpVisualizationData(pageViewExample2, dateArr2)).toMatchObject(visualizationDataExample2);
});
