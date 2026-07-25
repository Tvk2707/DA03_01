document.addEventListener("DOMContentLoaded", function () {
    "use strict";

    var page = document.body;
    var endpoint = page.dataset.statisticsUrl || "admin/thong-ke";
    var currentYear = Number(page.dataset.currentYear) || new Date().getFullYear();
    var currentMonth = Number(page.dataset.currentMonth) || new Date().getMonth() + 1;
    var state = {
        mode: "month",
        year: currentYear,
        month: currentMonth,
        quarter: Math.floor((currentMonth - 1) / 3) + 1,
        series: []
    };

    var monthNames = [
        "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
        "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
    ];
    var themeStyles = getComputedStyle(document.documentElement);
    var primaryColor = themeStyles.getPropertyValue("--primary-color").trim() || "#b8956a";
    var periodMode = document.getElementById("periodMode");
    var periodFields = document.getElementById("periodFields");
    var chart = document.getElementById("revenueChart");
    var chartLegend = document.getElementById("chartLegend");
    var chartTotal = document.getElementById("chartTotal");
    var chartDescription = document.getElementById("chartDescription");
    var reportFrom = document.getElementById("reportFrom");
    var reportTo = document.getElementById("reportTo");
    var applyFilter = document.getElementById("applyReportFilter");
    var resetFilter = document.getElementById("resetReportFilter");

    if (!periodMode || !periodFields || !chart || !chartLegend || !chartTotal || !chartDescription) {
        return;
    }

    function formatCurrency(value) {
        return new Intl.NumberFormat("vi-VN").format(Number(value) || 0) + " đ";
    }

    function formatYAxis(value) {
        if (value === 0) return "0";
        if (value >= 1000000000) return (value / 1000000000).toFixed(1) + "B";
        return (value / 1000000).toFixed(1) + "M";
    }

    function showMessage(message, isError) {
        var toast = document.getElementById("statToast");
        var messageNode = document.getElementById("statToastMessage");
        if (!toast || !messageNode) {
            if (isError) window.alert(message);
            return;
        }
        messageNode.textContent = message;
        toast.classList.toggle("is-error", Boolean(isError));
        toast.classList.add("is-visible");
        window.setTimeout(function () {
            toast.classList.remove("is-visible");
        }, 3000);
    }

    async function readJson(url) {
        var response = await fetch(url, {
            headers: { "Accept": "application/json" }
        });
        var data = await response.json();
        if (!response.ok || data.success === false) {
            throw new Error(data.message || "Không tải được dữ liệu thống kê.");
        }
        return data;
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
            item.selected = String(option.value) === String(value);
            select.appendChild(item);
        });

        field.appendChild(hiddenLabel);
        field.appendChild(select);
        return field;
    }

    function yearOptions() {
        var values = [];
        for (var year = currentYear - 5; year <= currentYear + 1; year++) {
            values.push({ value: year, label: String(year) });
        }
        return values;
    }

    function renderPeriodFields() {
        periodFields.innerHTML = "";

        if (state.mode === "month") {
            periodFields.appendChild(createSelect(
                "reportMonth",
                "Tháng",
                monthNames.map(function (name, index) {
                    return { value: index + 1, label: name };
                }),
                state.month
            ));
        } else if (state.mode === "quarter") {
            periodFields.appendChild(createSelect(
                "reportQuarter",
                "Quý",
                [1, 2, 3, 4].map(function (quarter) {
                    return { value: quarter, label: "Quý " + quarter };
                }),
                state.quarter
            ));
        }

        periodFields.appendChild(createSelect("reportYear", "Năm", yearOptions(), state.year));

        var monthSelect = document.getElementById("reportMonth");
        var quarterSelect = document.getElementById("reportQuarter");
        var yearSelect = document.getElementById("reportYear");

        monthSelect?.addEventListener("change", function () {
            state.month = Number(this.value);
            loadRevenueSeries();
        });
        quarterSelect?.addEventListener("change", function () {
            state.quarter = Number(this.value);
            loadRevenueSeries();
        });
        yearSelect?.addEventListener("change", function () {
            state.year = Number(this.value);
            loadRevenueSeries();
        });
    }

    function seriesName() {
        if (state.mode === "quarter") {
            return "Doanh thu (quý " + state.quarter + "/" + state.year + ")";
        }
        if (state.mode === "year") {
            return "Doanh thu (năm " + state.year + ")";
        }
        return "Doanh thu (tháng " + String(state.month).padStart(2, "0") + "/" + state.year + ")";
    }

    async function loadRevenueSeries() {
        var query = new URLSearchParams({
            action: "revenue-series",
            mode: state.mode,
            year: String(state.year),
            month: String(state.month),
            quarter: String(state.quarter)
        });

        chart.classList.add("is-loading");
        try {
            var data = await readJson(endpoint + "?" + query.toString());
            state.series = Array.isArray(data.series)
                ? data.series.map(function (item) {
                    return {
                        label: String(item.label),
                        value: Number(item.value) || 0
                    };
                })
                : [];
            chartDescription.textContent = data.description || "";
            renderChart();
        } catch (error) {
            state.series = [];
            chartDescription.textContent = "Không tải được dữ liệu doanh thu";
            renderChart();
            showMessage(error.message, true);
        } finally {
            chart.classList.remove("is-loading");
        }
    }

    function makeSmoothPath(points) {
        if (points.length === 0) return "";
        if (points.length === 1) return "M " + points[0].x + " " + points[0].y;

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
            path += " C " + cp1x + " " + cp1y + ", " + cp2x + " " + cp2y + ", "
                + next.x + " " + next.y;
        }
        return path;
    }

    function renderChart() {
        var data = state.series;
        var total = data.reduce(function (sum, item) {
            return sum + item.value;
        }, 0);
        var dataMaximum = data.reduce(function (maximum, item) {
            return Math.max(maximum, item.value);
        }, 0);
        var step = dataMaximum >= 100000000 ? 10000000 : 1000000;
        var maxValue = Math.max(step, Math.ceil(dataMaximum / step) * step);
        var width = 1200;
        var height = 310;
        var padding = { top: 14, right: 16, bottom: 30, left: 55 };
        var plotWidth = width - padding.left - padding.right;
        var plotHeight = height - padding.top - padding.bottom;

        chartLegend.innerHTML =
            '<span class="stat-legend-item"><i class="stat-legend-color" style="background:'
            + primaryColor + '"></i>' + seriesName() + "</span>";
        chartTotal.textContent = formatCurrency(total);

        if (data.length === 0) {
            chart.innerHTML = '<div class="empty-state">Chưa có doanh thu trong giai đoạn này.</div>';
            return;
        }

        var points = data.map(function (item, index) {
            return {
                x: padding.left + (data.length === 1
                    ? plotWidth / 2
                    : (plotWidth / (data.length - 1)) * index),
                y: padding.top + plotHeight - (item.value / maxValue) * plotHeight,
                label: item.label,
                value: item.value
            };
        });
        var yTicks = [0, 0.2, 0.4, 0.6, 0.8, 1].map(function (ratio) {
            return maxValue * ratio;
        });
        var gridLines = yTicks.map(function (tick) {
            var y = padding.top + plotHeight - (tick / maxValue) * plotHeight;
            return '<line class="grid-line" x1="' + padding.left + '" y1="' + y
                + '" x2="' + (width - padding.right) + '" y2="' + y + '"></line>'
                + '<text class="axis-label" x="8" y="' + (y + 4) + '">'
                + formatYAxis(tick) + "</text>";
        }).join("");
        var labelStep = Math.max(1, Math.ceil(points.length / 15));
        var xLabels = points.map(function (point, index) {
            if (index % labelStep !== 0 && index !== points.length - 1) return "";
            return '<text class="axis-label" x="' + point.x + '" y="' + (height - 8)
                + '" text-anchor="middle">' + point.label + "</text>";
        }).join("");
        var pointNodes = points.map(function (point) {
            return '<circle class="chart-point" cx="' + point.x + '" cy="' + point.y
                + '" r="4" fill="' + primaryColor + '" tabindex="0" data-label="'
                + point.label + '" data-value="' + point.value + '"></circle>';
        }).join("");

        chart.innerHTML =
            '<svg viewBox="0 0 ' + width + " " + height
            + '" preserveAspectRatio="none" aria-hidden="true">'
            + gridLines + xLabels
            + '<path class="chart-line" d="' + makeSmoothPath(points)
            + '" stroke="' + primaryColor + '"></path>'
            + pointNodes + "</svg>"
            + '<div class="stat-chart-tooltip" id="revenueTooltip" role="status"></div>';
        bindTooltip();
    }

    function bindTooltip() {
        var tooltip = document.getElementById("revenueTooltip");
        if (!tooltip) return;

        chart.querySelectorAll(".chart-point").forEach(function (point) {
            function showTooltip() {
                var bounds = chart.getBoundingClientRect();
                var pointBounds = point.getBoundingClientRect();
                tooltip.innerHTML = "<strong>" + point.dataset.label + "</strong><span>"
                    + formatCurrency(point.dataset.value) + "</span>";
                tooltip.style.left = (pointBounds.left - bounds.left + pointBounds.width / 2) + "px";
                tooltip.style.top = (pointBounds.top - bounds.top - 10) + "px";
                tooltip.classList.add("is-visible");
            }

            point.addEventListener("mouseenter", showTooltip);
            point.addEventListener("focus", showTooltip);
            point.addEventListener("mouseleave", function () {
                tooltip.classList.remove("is-visible");
            });
            point.addEventListener("blur", function () {
                tooltip.classList.remove("is-visible");
            });
        });
    }

    periodMode.addEventListener("change", function () {
        state.mode = this.value;
        renderPeriodFields();
        loadRevenueSeries();
    });

    applyFilter?.addEventListener("click", function () {
        var from = reportFrom?.value;
        var to = reportTo?.value;
        if (!from || !to) {
            showMessage("Vui lòng chọn đầy đủ từ ngày và đến ngày.", true);
            return;
        }
        if (from > to) {
            showMessage("Từ ngày không được lớn hơn đến ngày.", true);
            return;
        }
        var query = new URLSearchParams({ from: from, to: to });
        window.location.assign(endpoint + "?" + query.toString());
    });

    resetFilter?.addEventListener("click", function () {
        window.location.assign(endpoint);
    });

    document.getElementById("exportReport")?.addEventListener("click", function () {
        window.print();
    });

    renderPeriodFields();
    loadRevenueSeries();
});
