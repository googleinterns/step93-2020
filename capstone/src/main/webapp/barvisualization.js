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
function getDataForBarVisualization() {
    return [
        [5, 8, 4, 10, 7],
        [5.5, 3.4, 2.1, 11, 8.8],
    ];
}

/**
 * Creates a multi-line graph with x-axis being the week/date,
 * y-axis being the number of page views, and lines for each
 * restaurant going across weeks. Hovering over a line will
 * display the restaurant's name and grey out the rest of the lines.
 */
function createBarVisualization(type) {
    const d3 = window.d3;
    const height = 600;
    const width = 600;
    const margin = ({top: 0, right: 0, bottom: 10, left: 0});
    const restaurantNames = ["Wildfire", "Goog Noodle", "Thai Noodles", "Inovasi", "Alinea"];
    const data = getDataForBarVisualization();
    const dataStack = d3.stack()
        .keys(d3.range(2))
        (d3.transpose(data)) // stacked data
        .map((d, i) => d.map(([y0, y1]) => [y0, y1, i]));
    const xVals = d3.range(5);
    const x = d3.scaleBand()
        .domain(xVals)
        .rangeRound([margin.left, width - margin.right])
        .padding(0.08);
    const xAxis = svg => svg.append("g")
        .attr("transform", `translate(0,${height - margin.bottom})`)
        .call(d3.axisBottom(x).tickSizeOuter(0).tickFormat((i) => restaurantNames[i]));
    const z = d3.scaleSequential(d3.interpolatePurples)
        .domain([-0.5 * 2, 1.5 * 2]);
    const dataMax = d3.max(data, d => d3.max(d));
    const dataStackMax = d3.max(dataStack, data => d3.max(data, d => d[1]));
    const y = d3.scaleLinear()
        .domain([0, dataStackMax])
        .range([height - margin.bottom, margin.top]);
    const yAxis = (g) =>
        g.attr('transform', `translate(${margin.left},0)`).call(d3.axisLeft(y));

    const svg = d3.select('#barChart')
        .attr('viewBox', [0, 0, width, height])
        .style('overflow', 'visible');

    const rect = svg.selectAll("g")
        .data(dataStack)
        .join("g")
        .attr("fill", (d, i) => z(i))
        .selectAll("rect")
        .data(d => d)
        .join("rect")
        .attr("x", (d, i) => x(i))
        .attr("y", height - margin.bottom)
        .attr("width", x.bandwidth())
        .attr("height", 0);

    svg.append("g")
        .call(xAxis);

    function transitionGrouped() {
        y.domain([0, dataMax]);
        svg.append("g")
            .call(yAxis);
        rect.transition()
            .duration(500)
            .delay((d, i) => i * 20)
            .attr("x", (d, i) => x(i) + x.bandwidth() / 2 * d[2])
            .attr("width", x.bandwidth() / 2)
            .transition()
            .attr("y", d => y(d[1] - d[0]))
            .attr("height", d => y(0) - y(d[1] - d[0]));
    }

    function transitionStacked() {
        y.domain([0, dataStackMax]);
        svg.append("g")
            .call(yAxis);
        rect.transition()
            .duration(500)
            .delay((d, i) => i * 20)
            .attr("y", d => y(d[1]))
            .attr("height", d => y(d[0]) - y(d[1]))
            .transition()
            .attr("x", (d, i) => x(i))
            .attr("width", x.bandwidth());
    }

    if (type === "stacked") {
        transitionStacked();
    }
    else {
        transitionGrouped();
    }
}

function setLegned() {
    const legend = d3.select("#legend")

    legend.append("circle").attr("cx",10).attr("cy",10).attr("r", 6).style("fill", "#9e9bc9")
    legend.append("circle").attr("cx",10).attr("cy",30).attr("r", 6).style("fill", "#cecde4")
    legend.append("text").attr("x", 20).attr("y", 10).text("Average of all weekly page views").style("font-size", "15px").attr("alignment-baseline","middle")
    legend.append("text").attr("x", 20).attr("y", 30).text("Last week's page views").style("font-size", "15px").attr("alignment-baseline","middle")
}
