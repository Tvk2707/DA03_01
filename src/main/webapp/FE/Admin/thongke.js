document.addEventListener("DOMContentLoaded", function () {
    var state = {
        mode: "month",
        year: 2026,
        month: 4,
        quarter: 2
    };

    var monthNames = [
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];

    var revenueByMonth = {
        "2026-04": [
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 500000, 3000000, 21000000, 0
        ]
    };

    var themeStyles = getComputedStyle(document.documentElement);
    var colors = [
        themeStyles.getPropertyValue("--primary-color").trim() || "#b8956a",
        themeStyles.getPropertyValue("--primary-dark").trim() || "#9a7d55"
    ];
    var periodMode = document.getElementById("periodMode");
    var periodFields = document.getElementById("periodFields");
    var chart = document.getElementById("revenueChart");
    var chartLegend = document.getElementById("chartLegend");
    var chartTotal = document.getElementById("chartTotal");
    var chartDescription = document.getElementById("chartDescription");

    if (!periodMode || !periodFields || !chart) {
        return;
    }

    function formatCurrency(value) {
        return new Intl.NumberFormat("vi-VN").format(value) + " đ";
    }

    function formatYAxis(value) {
        return value === 0 ? "0" : (value / 1000000).toFixed(1) + "M";
    }

    function monthKey(year, month) {
        return year + "-" + String(month).padStart(2, "0");
    }

    function getMonthData(year, month) {
        var key = monthKey(year, month);
        var days = new Date(year, month, 0).getDate();
        var values = revenueByMonth[key] || [];

        return Array.from({ length: days }, function (_, index) {
            return {
                label: String(index + 1),
                value: values[index] || 0
            };
        });
    }

    function getQuarterData(year, quarter) {
        var startMonth = (quarter - 1) * 3 + 1;

        return [0, 1, 2].map(function (offset) {
            var month = startMonth + offset;
            var total = getMonthData(year, month).reduce(function (sum, item) {
                return sum + item.value;
            }, 0);

            return {
                label: "T" + month,
                value: total
            };
        });
    }

    function getYearData(year) {
        return Array.from({ length: 12 }, function (_, index) {
            var month = index + 1;
            var total = getMonthData(year, month).reduce(function (sum, item) {
                return sum + item.value;
            }, 0);

            return {
                label: "T" + month,
                value: total
            };
        });
    }

    function getActiveSeries() {
        if (state.mode === "quarter") {
            return {
                name: "Doanh thu (quý " + state.quarter + "/" + state.year + ")",
                description: "Doanh thu theo tháng trong quý " + state.quarter + "/" + state.year,
                data: getQuarterData(state.year, state.quarter)
            };
        }

        if (state.mode === "year") {
            return {
                name: "Doanh thu (năm " + state.year + ")",
                description: "Doanh thu theo tháng trong năm " + state.year,
                data: getYearData(state.year)
            };
        }

        return {
            name: "Doanh thu (tháng " + String(state.month).padStart(2, "0") + "/" + state.year + ")",
            description: "Doanh thu theo ngày trong tháng " + String(state.month).padStart(2, "0") + "/" + state.year,
            data: getMonthData(state.year, state.month)
        };
    }

    function createSelect(id, label, options, value) {
        var field = document.createElement("label");
        field.className = "stat-select-field";

        var hiddenLabel = document.createElement("span");
        hiddenLabel.className = "sr-only";
        hiddenLabel.textContent = label;

        var select = document.createElement("select");
        select.id = id;

        options.forEach(function (option) {
            var item = document.createElement("option");
            item.value = option.value;
            item.textContent = option.label;
            if (String(option.value) === String(value)) {
                item.selected = true;
            }
            select.appendChild(item);
        });

        field.appendChild(hiddenLabel);
        field.appendChild(select);
        return field;
    }

    function renderPeriodFields() {
        var years = [2024, 2025, 2026, 2027].map(function (year) {
            return { value: year, label: String(year) };
        });

        periodFields.innerHTML = "";

        if (state.mode === "month") {
            periodFields.appendChild(createSelect(
                "reportMonth",
                "Tháng",
                monthNames.map(function (name, index) {
                    return { value: index + 1, label: name + " " + state.year };
                }),
                state.month
            ));
        }

        if (state.mode === "quarter") {
            periodFields.appendChild(createSelect(
                "reportQuarter",
                "Quý",
                [1, 2, 3, 4].map(function (quarter) {
                    return { value: quarter, label: "Quý " + quarter };
                }),
                state.quarter
            ));
        }

        if (state.mode !== "month") {
            periodFields.appendChild(createSelect("reportYear", "Năm", years, state.year));
        }

        var reportMonth = document.getElementById("reportMonth");
        var reportQuarter = document.getElementById("reportQuarter");
        var reportYear = document.getElementById("reportYear");

        if (reportMonth) {
            reportMonth.addEventListener("change", function () {
                state.month = Number(this.value);
                renderChart();
            });
        }

        if (reportQuarter) {
            reportQuarter.addEventListener("change", function () {
                state.quarter = Number(this.value);
                renderChart();
            });
        }

        if (reportYear) {
            reportYear.addEventListener("change", function () {
                state.year = Number(this.value);
                renderPeriodFields();
                renderChart();
            });
        }
    }

    function makeSmoothPath(points) {
        if (points.length === 0) {
            return "";
        }

        if (points.length === 1) {
            return "M " + points[0].x + " " + points[0].y;
        }

        var path = "M " + points[0].x + " " + points[0].y;

        for (var i = 0; i < points.length - 1; i++) {
            var current = points[i];
            var next = points[i + 1];
            var previous = points[i - 1] || current;
            var afterNext = points[i + 2] || next;
            var cp1x = current.x + (next.x - previous.x) / 6;
            var cp1y = current.y + (next.y - previous.y) / 6;
            var cp2x = next.x - (afterNext.x - current.x) / 6;
            var cp2y = next.y - (afterNext.y - current.y) / 6;

            path += " C " + cp1x + " " + cp1y + ", " + cp2x + " " + cp2y + ", " + next.x + " " + next.y;
        }

        return path;
    }

    function renderChart() {
        var series = getActiveSeries();
        var data = series.data;
        var total = data.reduce(function (sum, item) {
            return sum + item.value;
        }, 0);
        var maxValue = Math.max(25000000, Math.ceil(Math.max.apply(null, data.map(function (item) {
            return item.value;
        })) / 5000000) * 5000000);
        var width = 1200;
        var height = 310;
        var padding = { top: 14, right: 16, bottom: 30, left: 42 };
        var plotWidth = width - padding.left - padding.right;
        var plotHeight = height - padding.top - padding.bottom;

        var points = data.map(function (item, index) {
            var x = padding.left + (data.length === 1 ? plotWidth / 2 : (plotWidth / (data.length - 1)) * index);
            var y = padding.top + plotHeight - (item.value / maxValue) * plotHeight;
            return {
                x: x,
                y: y,
                label: item.label,
                value: item.value
            };
        });

        var yTicks = [0, 0.2, 0.4, 0.6, 0.8, 1].map(function (ratio) {
            return maxValue * ratio;
        });

        chartLegend.innerHTML =
            '<span class="stat-legend-item"><i class="stat-legend-color" style="background:' + colors[0] + '"></i>' +
            series.name + "</span>";
        chartTotal.textContent = formatCurrency(total);
        chartDescription.textContent = series.description;

        var gridLines = yTicks.map(function (tick) {
            var y = padding.top + plotHeight - (tick / maxValue) * plotHeight;
            return '<line class="grid-line" x1="' + padding.left + '" y1="' + y + '" x2="' + (width - padding.right) + '" y2="' + y + '"></line>' +
                '<text class="axis-label" x="8" y="' + (y + 4) + '">' + formatYAxis(tick) + "</text>";
        }).join("");

        var xLabels = points.map(function (point, index) {
            var showAll = points.length <= 12;
            var shouldShow = showAll || index % 1 === 0;
            return shouldShow
                ? '<text class="axis-label" x="' + point.x + '" y="' + (height - 8) + '" text-anchor="middle">' + point.label + "</text>"
                : "";
        }).join("");

        var path = makeSmoothPath(points);
        var pointNodes = points.map(function (point) {
            return '<circle class="chart-point" cx="' + point.x + '" cy="' + point.y + '" r="4" fill="' + colors[0] + '" tabindex="0" ' +
                'data-label="' + point.label + '" data-value="' + point.value + '"></circle>';
        }).join("");

        chart.innerHTML =
            '<svg viewBox="0 0 ' + width + " " + height + '" preserveAspectRatio="none" aria-hidden="true">' +
            '<defs><filter id="lineShadow" x="-20%" y="-20%" width="140%" height="140%">' +
            '<feDropShadow dx="0" dy="2" stdDeviation="2" flood-color="' + colors[0] + '" flood-opacity="0.22"></feDropShadow>' +
            "</filter></defs>" +
            gridLines +
            xLabels +
            '<path class="chart-line" d="' + path + '" stroke="' + colors[0] + '" filter="url(#lineShadow)"></path>' +
            pointNodes +
            "</svg>" +
            '<div class="stat-chart-tooltip" id="revenueTooltip" role="status"></div>';

        bindTooltip(series.name);
    }

    function bindTooltip(seriesName) {
        var tooltip = document.getElementById("revenueTooltip");
        var points = chart.querySelectorAll(".chart-point");

        points.forEach(function (point) {
            function showTooltip() {
                var bounds = chart.getBoundingClientRect();
                var pointBounds = point.getBoundingClientRect();
                var label = point.getAttribute("data-label");
                var value = Number(point.getAttribute("data-value"));

                tooltip.innerHTML = "<strong>" + label + "</strong><span>" + seriesName + ": " + formatCurrency(value) + "</span>";
                tooltip.style.left = (pointBounds.left - bounds.left + pointBounds.width / 2) + "px";
                tooltip.style.top = (pointBounds.top - bounds.top - 10) + "px";
                tooltip.classList.add("is-visible");
            }

            function hideTooltip() {
                tooltip.classList.remove("is-visible");
            }

            point.addEventListener("mouseenter", showTooltip);
            point.addEventListener("focus", showTooltip);
            point.addEventListener("mouseleave", hideTooltip);
            point.addEventListener("blur", hideTooltip);
        });
    }

    periodMode.addEventListener("change", function () {
        state.mode = this.value;
        renderPeriodFields();
        renderChart();
    });

    renderPeriodFields();
    renderChart();
});
