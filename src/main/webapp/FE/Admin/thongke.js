document.addEventListener("DOMContentLoaded", function () {
    var config = window.statisticsConfig || {};
    var apiUrl = config.apiUrl || "/admin/thong-ke";
    var periodMode = document.getElementById("periodMode");
    var periodFields = document.getElementById("periodFields");
    var chart = document.getElementById("revenueChart");
    var chartLegend = document.getElementById("chartLegend");
    var chartTotal = document.getElementById("chartTotal");
    var chartDescription = document.getElementById("chartDescription");
    var reportFrom = document.getElementById("reportFrom");
    var reportTo = document.getElementById("reportTo");
    var compareModal = document.getElementById("compareModal");

    if (!periodMode || !periodFields || !chart || !reportFrom || !reportTo) {
        return;
    }

    var fromDate = parseDate(config.reportFrom || reportFrom.value);
    var state = {
        mode: "month",
        year: fromDate.getFullYear(),
        month: fromDate.getMonth() + 1,
        quarter: Math.floor(fromDate.getMonth() / 3) + 1
    };

    var monthNames = [
        "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
        "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
    ];
    var colors = [
        getComputedStyle(document.documentElement).getPropertyValue("--primary-color").trim() || "#b8956a",
        getComputedStyle(document.documentElement).getPropertyValue("--primary-dark").trim() || "#9a7d55"
    ];

    function parseDate(value) {
        var parts = String(value || "").split("-").map(Number);
        return parts.length === 3 && parts.every(function (part) { return Number.isFinite(part); })
            ? new Date(parts[0], parts[1] - 1, parts[2])
            : new Date();
    }

    function isoDate(date) {
        return date.getFullYear() + "-" + String(date.getMonth() + 1).padStart(2, "0") + "-" + String(date.getDate()).padStart(2, "0");
    }

    function addDays(date, amount) {
        var next = new Date(date);
        next.setDate(next.getDate() + amount);
        return next;
    }

    function addMonths(date, amount) {
        var next = new Date(date);
        next.setDate(1);
        next.setMonth(next.getMonth() + amount);
        return next;
    }

    function periodRange() {
        var start;
        var end;
        if (state.mode === "year") {
            start = new Date(state.year, 0, 1);
            end = new Date(state.year, 11, 31);
        } else if (state.mode === "quarter") {
            var firstMonth = (state.quarter - 1) * 3;
            start = new Date(state.year, firstMonth, 1);
            end = new Date(state.year, firstMonth + 3, 0);
        } else {
            start = new Date(state.year, state.month - 1, 1);
            end = new Date(state.year, state.month, 0);
        }
        return { from: isoDate(start), to: isoDate(end) };
    }

    function formatCurrency(value) {
        return new Intl.NumberFormat("vi-VN").format(Number(value) || 0) + " đ";
    }

    function escapeHtml(value) {
        return String(value == null ? "-" : value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function requestJson(api, params) {
        var url = new URL(apiUrl, window.location.origin);
        url.searchParams.set("api", api);
        Object.keys(params || {}).forEach(function (key) {
            url.searchParams.set(key, params[key]);
        });
        return fetch(url.toString(), { headers: { Accept: "application/json" } }).then(function (response) {
            return response.json().then(function (data) {
                if (!response.ok || data.error) {
                    throw new Error(data.error || "Không thể lấy dữ liệu thống kê");
                }
                return data;
            });
        });
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

    function renderPeriodFields() {
        var yearOptions = [];
        for (var year = new Date().getFullYear() - 3; year <= new Date().getFullYear() + 1; year++) {
            yearOptions.push({ value: year, label: String(year) });
        }
        periodFields.innerHTML = "";
        if (state.mode === "month") {
            periodFields.appendChild(createSelect("reportMonth", "Tháng", monthNames.map(function (name, index) {
                return { value: index + 1, label: name };
            }), state.month));
        }
        if (state.mode === "quarter") {
            periodFields.appendChild(createSelect("reportQuarter", "Quý", [1, 2, 3, 4].map(function (quarter) {
                return { value: quarter, label: "Quý " + quarter };
            }), state.quarter));
        }
        periodFields.appendChild(createSelect("reportYear", "Năm", yearOptions, state.year));

        var month = document.getElementById("reportMonth");
        var quarter = document.getElementById("reportQuarter");
        var yearSelect = document.getElementById("reportYear");
        if (month) month.addEventListener("change", function () { state.month = Number(this.value); loadChartForPeriod(); });
        if (quarter) quarter.addEventListener("change", function () { state.quarter = Number(this.value); loadChartForPeriod(); });
        if (yearSelect) yearSelect.addEventListener("change", function () { state.year = Number(this.value); renderPeriodFields(); loadChartForPeriod(); });
    }

    function normalizeSeries(points, from, to, mode) {
        var lookup = {};
        (points || []).forEach(function (point) { lookup[point.key] = Number(point.value) || 0; });
        var result = [];
        if (mode === "month") {
            for (var date = parseDate(from); date <= parseDate(to); date = addDays(date, 1)) {
                var dayKey = isoDate(date);
                result.push({ label: String(date.getDate()), key: dayKey, value: lookup[dayKey] || 0 });
            }
        } else {
            var cursor = new Date(parseDate(from).getFullYear(), parseDate(from).getMonth(), 1);
            var end = new Date(parseDate(to).getFullYear(), parseDate(to).getMonth(), 1);
            while (cursor <= end) {
                var monthKey = cursor.getFullYear() + "-" + String(cursor.getMonth() + 1).padStart(2, "0");
                result.push({ label: "T" + (cursor.getMonth() + 1), key: monthKey, value: lookup[monthKey] || 0 });
                cursor = addMonths(cursor, 1);
            }
        }
        return result;
    }

    function makeSmoothPath(points) {
        if (!points.length) return "";
        if (points.length === 1) return "M " + points[0].x + " " + points[0].y;
        var path = "M " + points[0].x + " " + points[0].y;
        for (var i = 0; i < points.length - 1; i++) {
            var current = points[i];
            var next = points[i + 1];
            var previous = points[i - 1] || current;
            var afterNext = points[i + 2] || next;
            var minY = Math.min(current.y, next.y);
            var maxY = Math.max(current.y, next.y);
            var controlY1 = Math.max(minY, Math.min(maxY, current.y + (next.y - previous.y) / 6));
            var controlY2 = Math.max(minY, Math.min(maxY, next.y - (afterNext.y - current.y) / 6));
            path += " C " + (current.x + (next.x - previous.x) / 6) + " " + controlY1
                + ", " + (next.x - (afterNext.x - current.x) / 6) + " " + controlY2
                + ", " + next.x + " " + next.y;
        }
        return path;
    }

    function renderChart(seriesList) {
        var prepared = seriesList.map(function (series) {
            return { name: series.name, points: normalizeSeries(series.points, series.from, series.to, series.mode) };
        });
        var count = Math.max.apply(null, prepared.map(function (series) { return series.points.length; }).concat([1]));
        var width = 1200;
        var height = 310;
        var padding = { top: 14, right: 16, bottom: 30, left: 42 };
        var plotWidth = width - padding.left - padding.right;
        var plotHeight = height - padding.top - padding.bottom;
        var maxValue = 0;
        prepared.forEach(function (series) { series.points.forEach(function (point) { maxValue = Math.max(maxValue, point.value); }); });
        maxValue = Math.max(1, Math.ceil(maxValue / 5000000) * 5000000);
        var gridLines = [0, 0.2, 0.4, 0.6, 0.8, 1].map(function (ratio) {
            var y = padding.top + plotHeight - ratio * plotHeight;
            return '<line class="grid-line" x1="' + padding.left + '" y1="' + y + '" x2="' + (width - padding.right) + '" y2="' + y + '"></line>'
                + '<text class="axis-label" x="8" y="' + (y + 4) + '">' + (ratio === 0 ? "0" : (maxValue * ratio / 1000000).toFixed(1) + "M") + "</text>";
        }).join("");
        var labels = (prepared[0]?.points || []).map(function (point, index) {
            var x = padding.left + (count === 1 ? plotWidth / 2 : plotWidth * index / (count - 1));
            return '<text class="axis-label" x="' + x + '" y="' + (height - 8) + '" text-anchor="middle">' + point.label + "</text>";
        }).join("");
        var paths = prepared.map(function (series, seriesIndex) {
            var points = series.points.map(function (point, index) {
                return { x: padding.left + (count === 1 ? plotWidth / 2 : plotWidth * index / (count - 1)), y: padding.top + plotHeight - (point.value / maxValue) * plotHeight, value: point.value, label: point.label };
            });
            var color = colors[seriesIndex % colors.length];
            var circles = points.map(function (point) {
                return '<circle class="chart-point" cx="' + point.x + '" cy="' + point.y + '" r="4" fill="' + color + '" data-label="' + escapeHtml(point.label) + '" data-value="' + point.value + '"></circle>';
            }).join("");
            return '<path class="chart-line" d="' + makeSmoothPath(points) + '" stroke="' + color + '"></path>' + circles;
        }).join("");
        chart.innerHTML = '<svg viewBox="0 0 ' + width + " " + height + '" preserveAspectRatio="none" aria-hidden="true">' + gridLines + labels + paths + "</svg>";
        chartLegend.innerHTML = prepared.map(function (series, index) {
            return '<span class="stat-legend-item"><i class="stat-legend-color" style="background:' + colors[index % colors.length] + '"></i>' + escapeHtml(series.name) + "</span>";
        }).join("");
        var totalText = prepared.map(function (series) {
            return series.name + ": " + formatCurrency(series.points.reduce(function (sum, point) { return sum + point.value; }, 0));
        }).join(" | ");
        chartTotal.textContent = totalText;
        chart.querySelectorAll(".chart-point").forEach(function (point) {
            point.addEventListener("mouseenter", function () {
                point.setAttribute("aria-label", point.getAttribute("data-label") + ": " + formatCurrency(point.getAttribute("data-value")));
            });
        });
    }

    function updateTables(data) {
        var bestBody = document.getElementById("bestSellerBody");
        var customerBody = document.getElementById("customerBody");
        var slowBody = document.getElementById("slowStockBody");
        bestBody.innerHTML = (data.bestSellers || []).length ? data.bestSellers.map(function (item) {
            return "<tr><td>" + escapeHtml(item.name) + "</td><td>" + item.sold + "</td><td>" + item.stock + "</td></tr>";
        }).join("") : '<tr><td colspan="3">Chưa có dữ liệu sản phẩm.</td></tr>';
        customerBody.innerHTML = (data.topCustomers || []).length ? data.topCustomers.map(function (item) {
            return "<tr><td>" + escapeHtml(item.name) + "</td><td>" + item.orders + "</td><td>" + formatCurrency(item.spending) + "</td></tr>";
        }).join("") : '<tr><td colspan="3">Chưa có dữ liệu khách hàng.</td></tr>';
        slowBody.innerHTML = (data.slowStockProducts || []).length ? data.slowStockProducts.map(function (item) {
            return "<tr><td>" + escapeHtml(item.name) + "</td><td>" + item.sold + "</td><td>" + item.stock + "</td></tr>";
        }).join("") : '<tr><td colspan="3">Chưa có dữ liệu tồn kho.</td></tr>';
    }

    function updateStatus(data) {
        var overview = data.overview || {};
        document.getElementById("totalOrderBadge").textContent = (overview.orders || 0) + " đơn";
        document.getElementById("completionRate").textContent = (data.completionRate || 0) + "%";
        document.getElementById("completionBar").style.width = (data.completionRate || 0) + "%";
        document.getElementById("orderStatusList").innerHTML =
            '<div class="stat-status-item"><span>Hoàn thành</span><strong>' + (overview.done || 0) + '</strong></div>'
            + '<div class="stat-status-item"><span>Đang xử lý</span><strong>' + (overview.processing || 0) + '</strong></div>'
            + '<div class="stat-status-item"><span>Đã hủy</span><strong>' + (overview.cancelled || 0) + '</strong></div>';
    }

    function showToast(message, isError) {
        var toast = document.getElementById("statToast");
        toast.classList.toggle("is-error", !!isError);
        document.getElementById("statToastMessage").textContent = message;
        toast.classList.add("is-visible");
        window.setTimeout(function () { toast.classList.remove("is-visible"); }, 2800);
    }

    function loadReport() {
        if (!reportFrom.value || !reportTo.value || reportTo.value < reportFrom.value) {
            showToast("Khoảng ngày không hợp lệ", true);
            return;
        }
        requestJson("report", { from: reportFrom.value, to: reportTo.value, mode: "month" })
            .then(function (data) {
                updateTables(data);
                updateStatus(data);
                renderChart([{ name: "Doanh thu theo khoảng lọc", points: data.series, from: reportFrom.value, to: reportTo.value, mode: "month" }]);
                chartDescription.textContent = "Doanh thu từ " + reportFrom.value + " đến " + reportTo.value;
                showToast("Đã cập nhật dữ liệu từ database", false);
            })
            .catch(function (error) { showToast(error.message, true); });
    }

    function loadChartForPeriod() {
        var range = periodRange();
        requestJson("chart", { from: range.from, to: range.to, mode: state.mode })
            .then(function (data) {
                renderChart([{ name: "Doanh thu " + (state.mode === "month" ? monthNames[state.month - 1] : state.mode === "quarter" ? "quý " + state.quarter : "năm") + " " + state.year, points: data.series, from: range.from, to: range.to, mode: state.mode }]);
                chartDescription.textContent = "Doanh thu theo " + (state.mode === "month" ? "ngày" : "tháng") + " trong kỳ đã chọn";
            })
            .catch(function (error) { showToast(error.message, true); });
    }

    function renderCompareFields() {
        var mode = document.getElementById("compareMode").value;
        var range = periodRange();
        var firstFrom = parseDate(range.from);
        var firstTo = parseDate(range.to);
        var duration = Math.round((firstTo - firstFrom) / 86400000) + 1;
        var secondTo = addDays(firstFrom, -1);
        var secondFrom = addDays(secondTo, -duration + 1);
        document.getElementById("compareFields").innerHTML =
            '<label class="stat-compare-field"><span>Kỳ 1 - Từ ngày</span><input class="stat-date-field-input" id="compareFrom1" type="date" value="' + isoDate(firstFrom) + '"><span>Đến ngày</span><input class="stat-date-field-input" id="compareTo1" type="date" value="' + isoDate(firstTo) + '"></label>'
            + '<label class="stat-compare-field"><span>Kỳ 2 - Từ ngày</span><input class="stat-date-field-input" id="compareFrom2" type="date" value="' + isoDate(secondFrom) + '"><span>Đến ngày</span><input class="stat-date-field-input" id="compareTo2" type="date" value="' + isoDate(secondTo) + '"></label>';
        void mode;
    }

    function closeCompare() { compareModal.classList.remove("is-open"); compareModal.setAttribute("aria-hidden", "true"); }

    periodMode.addEventListener("change", function () { state.mode = this.value; renderPeriodFields(); loadChartForPeriod(); });
    document.getElementById("applyReportFilter").addEventListener("click", loadReport);
    document.getElementById("resetReportFilter").addEventListener("click", function () {
        var today = new Date();
        reportFrom.value = isoDate(new Date(today.getFullYear(), today.getMonth(), 1));
        reportTo.value = isoDate(today);
        loadReport();
    });
    document.getElementById("openCompare").addEventListener("click", function () {
        compareModal.classList.add("is-open");
        compareModal.setAttribute("aria-hidden", "false");
        renderCompareFields();
    });
    document.getElementById("compareMode").addEventListener("change", renderCompareFields);
    document.querySelectorAll("[data-close-modal]").forEach(function (element) { element.addEventListener("click", closeCompare); });
    document.getElementById("applyCompare").addEventListener("click", function () {
        var from1 = document.getElementById("compareFrom1").value;
        var to1 = document.getElementById("compareTo1").value;
        var from2 = document.getElementById("compareFrom2").value;
        var to2 = document.getElementById("compareTo2").value;
        var mode = document.getElementById("compareMode").value;
        if (!from1 || !to1 || !from2 || !to2 || to1 < from1 || to2 < from2) {
            showToast("Khoảng so sánh không hợp lệ", true);
            return;
        }
        Promise.all([
            requestJson("chart", { from: from1, to: to1, mode: mode }),
            requestJson("chart", { from: from2, to: to2, mode: mode })
        ]).then(function (values) {
            renderChart([
                { name: "Kỳ 1", points: values[0].series, from: from1, to: to1, mode: mode },
                { name: "Kỳ 2", points: values[1].series, from: from2, to: to2, mode: mode }
            ]);
            chartDescription.textContent = "So sánh doanh thu giữa hai kỳ";
            closeCompare();
            showToast("Đã cập nhật dữ liệu so sánh", false);
        }).catch(function (error) { showToast(error.message, true); });
    });

    renderPeriodFields();
    loadReport();
});
