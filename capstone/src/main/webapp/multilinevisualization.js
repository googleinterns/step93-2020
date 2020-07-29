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
 * Setup dummy data for sample visualization
 * TODO: amend this function to get actual datastore data!
 */
function getDataForMultilineVisualization() {
  const dateArr = [
    new Date(2020, 2, 1),
    new Date(2020, 2, 8),
    new Date(2020, 2, 15),
    new Date(2020, 2, 22),
    new Date(2020, 2, 29),
    new Date(2020, 3, 5),
    new Date(2020, 3, 12),
  ];
  return {
    'restaurantData': [
      {
        'restaurantName': 'Wildfire',
        'clickData': [2, 10, 10, 8, 3, 18, 20],
      },
      {
        'restaurantName': 'Poke Doke',
        'clickData': [0, 8, 10, 5, 20, 22, 17],
      },
      {
        'restaurantName': 'The Goog Noodle',
        'clickData': [20, 18, 12, 21, 20, 19, 15],
      },
    ],
    'dates': dateArr,
  };
}

/**
 * Creates a multi-line graph with x-axis being the week/date,
 * y-axis being the number of page views, and lines for each
 * restaurant going across weeks. Hovering over a line will
 * display the restaurant's name and grey out the rest of the lines.
 */
async function createMultilineVisualization() {
  const d3 = window.d3;
  // Need to disable validator because parseData is defined in another file
  // Can't import it because then this file would have to become a module,
  // causing other issues
  // eslint-disable-next-line no-undef
  const data = await parseData();
  console.log(data);
  const height = 400;
  const width = 600;
  const margin = ({top: 20, right: 20, bottom: 30, left: 30});

  // Use the scaleTime function to scale the x-axis by the date data
  const x = d3.scaleTime().domain(d3.extent(data.dates)).range([
    margin.left,
    width - margin.right,
  ]);

  // Scale the y-axis up to the maximum of all the clickData points
  const y =
      d3.scaleLinear()
          .domain([0, d3.max(data.restaurantData, (d) => d3.max(d.clickData))])
          .nice()
          .range([height - margin.bottom, margin.top]);

  // Used to make lines between the date/click x/y pairs
  // Ensures all the data is numeric before trying to plot
  const line = d3.line()
                   .defined((d) => !isNaN(d))
                   .x((d, i) => x(data.dates[i]))
                   .y((d) => y(d));

  // Translate everything to the right by the left margin amount
  // Use linearly scaled y variable for the y-axis
  const yAxis = (g) =>
      g.attr('transform', `translate(${margin.left},0)`).call(d3.axisLeft(y));

  // Translate everything upwards by the bottom margin amount
  // Create bottom axis with x variable scaled by time
  const xAxis = (g) =>
      g.attr('transform', `translate(0,${height - margin.bottom})`)
          .call(d3.axisBottom(x).ticks(width / 80).tickSizeOuter(0));

  // Select the HTML scalable vector graphic for the line graph
  // Set the width and height
  const svg = d3.select('svg')
                  .attr('viewBox', [0, 0, width, height])
                  .style('overflow', 'visible');

  // Append a 'g' element with the xAxis
  // 'g' elements are containers for graphic and can group related graphics
  // elements
  svg.append('g').call(xAxis);

  // Append the yAxis in a 'g' element
  svg.append('g').call(yAxis);

  // Create title
  svg.append('g')
      .append('text')
      .attr('x', width / 2)
      .attr('y', 0)
      .style('text-anchor', 'middle')
      .attr('font-weight', 'bold')
      .text('Page View Weekly Data');

  // Create x-axis
  svg.append('g')
      .append('text')
      .attr('x', width / 2)
      .attr('y', height + margin.bottom)
      .style('text-anchor', 'middle')
      .attr('font-weight', 'bold')
      .text('Date');

  // Create y-axis
  svg.append('g')
      .append('text')
      .attr('transform', 'rotate(-90)')
      .attr('x', 0 - height / 2)
      .attr('y', 0 - margin.left)
      .attr('dy', '1em')
      .style('text-anchor', 'middle')
      .attr('font-weight', 'bold')
      .text('# of Page Views');

  // Append all the lines between click data elements
  // All the lines can be grouped in one 'g' element
  const path = svg.append('g')
                   .attr('fill', 'none')
                   .attr('stroke', 'darkviolet')
                   .attr('stroke-width', 1.5)
                   .attr('stroke-linejoin', 'round')
                   .attr('stroke-linecap', 'round')
                   .selectAll('path')
                   .data(data.restaurantData)
                   .enter()
                   .append('path')
                   .style('mix-blend-mode', 'multiply')
                   .attr('d', (d) => line(d.clickData));

  // Triggered upon hovering over any line
  svg.call(hover, path);

  /**
   * Checks the mouse position upon hovering over a graph and displays
   * the closest point to that position. Also greys out all lines but the one
   * with the closest point and displays the name of the restaurant the selected
   * line belongs to
   * @param svg, the graph being hovered over
   * @param path, the path of lines to look through
   */
  function hover(svg, path) {
    // Setup functions to be called on different actions
    svg.on('mousemove', moved).on('mouseenter', entered).on('mouseleave', left);

    // Create a blank dot with blank text
    const dot = svg.append('g').attr('display', 'none');
    dot.append('circle').attr('r', 2.5);
    dot.append('text')
        .attr('font-family', 'sans-serif')
        .attr('font-size', 10)
        .attr('text-anchor', 'middle')
        .attr('y', -8);

    // Function for when the mouse is moved within the graph area
    function moved() {
      d3.event.preventDefault();
      // Get current mouse position
      // eslint-disable-next-line no-invalid-this
      const mouse = d3.mouse(this);
      // The invert function will return the value from the domain given
      // a value from the range
      // Range was set when originally declaring x and y to the width/height, so
      // our mouse values will be properly converted
      const xm = x.invert(mouse[0]);
      const ym = y.invert(mouse[1]);

      // The bisectLeft function will return the index in the dates
      // array you would insert xm in order to maintain a sorted order
      // Essentially returns an integer index in the array
      // represented on the x-axis so we know which tick on the x-axis the mouse
      // is currently closest to
      // Check both the point to the left and right of current position to see
      // which is closer
      const i1 = d3.bisectLeft(data.dates, xm, 1);
      const i0 = i1 - 1;
      const i = xm - data.dates[i0] > data.dates[i1] - xm ? i1 : i0;

      // Now look through the different y-values, aka click data for each
      // restaurant, to find which is closest to the mouse's current y-position
      // Keep track of the full restaurant object with the closest data point
      let minTrack = Math.abs(data.restaurantData[0].clickData[i] - ym);
      let s = data.restaurantData[0];
      for (let k = 1; k < data.restaurantData.length; k++) {
        if (Math.abs(data.restaurantData[k].clickData[i] - ym) < minTrack) {
          minTrack = Math.abs(data.restaurantData[k].clickData[i] - ym);
          s = data.restaurantData[k];
        }
      }

      // Make every line greyed out except the one currently selected by the
      // mouse
      path.attr('stroke', (d) => d === s ? null : '#ddd')
          .filter((d) => d === s)
          .raise();

      // Translate the dot to be in the correct location on the screen based on
      // the dates array and the clickData of the selected restaurant
      dot.attr(
          'transform', `translate(${x(data.dates[i])},${y(s.clickData[i])})`);
      // Label the line currently being hovered over with the current restaurant
      // name
      dot.select('text').text(s.restaurantName);
    }

    // Function for when mouse has entered the graph area
    function entered() {
      // Grey out all the lines and show the dot on the display
      path.style('mix-blend-mode', null).attr('stroke', '#ddd');
      dot.attr('display', null);
    }

    // Function for when the mouse has left the graph area
    function left() {
      // Allow the lines to go back to full color and hide the dot from the
      // display
      path.style('mix-blend-mode', 'multiply').attr('stroke', null);
      dot.attr('display', 'none');
    }
  }
}
