(function () {
    "use strict";

    const PRODUCTS = [
        { id: "SP01", name: "Gọng Titanium Flex Amber", category: "Gọng kính", stock: 18 },
        { id: "SP02", name: "Gọng Acetate Café", category: "Gọng kính", stock: 15 },
        { id: "SP03", name: "Kính râm Polarized Aviator", category: "Kính râm", stock: 9 },
        { id: "SP04", name: "Tròng kính Blue Control", category: "Tròng kính", stock: 32 },
        { id: "SP05", name: "Gọng Oval Champagne", category: "Gọng kính", stock: 22 },
        { id: "SP06", name: "Kính râm Urban Smoke", category: "Kính râm", stock: 11 },
        { id: "SP07", name: "Gọng Titanium Đen", category: "Gọng kính", stock: 20 },
        { id: "SP08", name: "Tròng kính Chống tia UV", category: "Tròng kính", stock: 26 }
    ];

    const DEMO_ORDERS = [
        { id: "HD26042981918", date: "2026-04-29", customer: "Duy Quyết", phone: "0868219136", product: "SP01", qty: 2, total: 6000000, status: "Hoàn thành" },
        { id: "HD26042925243", date: "2026-04-29", customer: "Duy Quyết", phone: "0868219136", product: "SP02", qty: 1, total: 2800000, status: "Hoàn thành" },
        { id: "HD26042908574", date: "2026-04-29", customer: "Minh Anh", phone: "0912456780", product: "SP03", qty: 1, total: 2800000, status: "Đã giao" },
        { id: "HD26042972295", date: "2026-04-29", customer: "Khách lẻ", phone: "-", product: "SP05", qty: 2, total: 4700000, status: "Hoàn thành" },
        { id: "HD26042977687", date: "2026-04-29", customer: "Phạm Thu Dung", phone: "0981000004", product: "SP04", qty: 1, total: 1375000, status: "Đang xử lý" },
        { id: "HD26042916162", date: "2026-04-29", customer: "Phạm Thu Dung", phone: "0981000004", product: "SP07", qty: 1, total: 1375000, status: "Chờ xác nhận" },
        { id: "HD26042862402", date: "2026-04-28", customer: "Nguyễn Thị Lan", phone: "0903555123", product: "SP04", qty: 2, total: 3300000, status: "Hoàn thành" },
        { id: "HD26042879311", date: "2026-04-28", customer: "Trần Văn Minh", phone: "0938111222", product: "SP06", qty: 1, total: 2450000, status: "Đang giao" },
        { id: "HD26042748016", date: "2026-04-27", customer: "Nguyễn Thị Lan", phone: "0903555123", product: "SP01", qty: 1, total: 3100000, status: "Đã xác nhận" },
        { id: "HD26042537542", date: "2026-04-25", customer: "Lê Phương Thảo", phone: "0976444333", product: "SP08", qty: 2, total: 2700000, status: "Hoàn thành" },
        { id: "HD26042359812", date: "2026-04-23", customer: "Duy Quyết", phone: "0868219136", product: "SP04", qty: 2, total: 3100000, status: "Hoàn thành" },
        { id: "HD26042040887", date: "2026-04-20", customer: "Hoàng Gia Bảo", phone: "0918765432", product: "SP02", qty: 1, total: 2600000, status: "Đã hủy" },
        { id: "HD26041810674", date: "2026-04-18", customer: "Minh Anh", phone: "0912456780", product: "SP03", qty: 1, total: 2800000, status: "Đã giao" },
        { id: "HD26041593970", date: "2026-04-15", customer: "Duy Quyết", phone: "0868219136", product: "SP01", qty: 2, total: 5900000, status: "Hoàn thành" },
        { id: "HD26041213968", date: "2026-04-12", customer: "Phạm Thu Dung", phone: "0981000004", product: "SP05", qty: 1, total: 2350000, status: "Yêu cầu hoàn" },
        { id: "HD26040937162", date: "2026-04-09", customer: "Lê Phương Thảo", phone: "0976444333", product: "SP08", qty: 1, total: 1400000, status: "Hoàn thành" },
        { id: "HD26040591620", date: "2026-04-05", customer: "Nguyễn Thị Lan", phone: "0903555123", product: "SP04", qty: 1, total: 1650000, status: "Đã hoàn tiền" },
        { id: "HD26040223049", date: "2026-04-02", customer: "Trần Văn Minh", phone: "0938111222", product: "SP07", qty: 1, total: 2900000, status: "Hoàn thành" },
        { id: "HD26032710938", date: "2026-03-27", customer: "Duy Quyết", phone: "0868219136", product: "SP02", qty: 2, total: 5200000, status: "Hoàn thành" },
        { id: "HD26031977210", date: "2026-03-19", customer: "Nguyễn Thị Lan", phone: "0903555123", product: "SP03", qty: 1, total: 2800000, status: "Hoàn thành" },
        { id: "HD26031086226", date: "2026-03-10", customer: "Phạm Thu Dung", phone: "0981000004", product: "SP04", qty: 2, total: 3200000, status: "Đã giao" },
        { id: "HD26022666814", date: "2026-02-26", customer: "Minh Anh", phone: "0912456780", product: "SP06", qty: 1, total: 2500000, status: "Hoàn thành" },
        { id: "HD26021488193", date: "2026-02-14", customer: "Duy Quyết", phone: "0868219136", product: "SP01", qty: 1, total: 3050000, status: "Hoàn thành" },
        { id: "HD26012867514", date: "2026-01-28", customer: "Lê Phương Thảo", phone: "0976444333", product: "SP05", qty: 2, total: 4600000, status: "Hoàn thành" },
        { id: "HD26010937467", date: "2026-01-09", customer: "Nguyễn Thị Lan", phone: "0903555123", product: "SP08", qty: 2, total: 2700000, status: "Đã giao" },
        { id: "HD25121957021", date: "2025-12-19", customer: "Duy Quyết", phone: "0868219136", product: "SP01", qty: 2, total: 5800000, status: "Hoàn thành" },
        { id: "HD25111680043", date: "2025-11-16", customer: "Trần Văn Minh", phone: "0938111222", product: "SP03", qty: 1, total: 2750000, status: "Hoàn thành" },
        { id: "HD25102324765", date: "2025-10-23", customer: "Minh Anh", phone: "0912456780", product: "SP06", qty: 2, total: 4800000, status: "Đã giao" },
        { id: "HD25091457337", date: "2025-09-14", customer: "Nguyễn Thị Lan", phone: "0903555123", product: "SP04", qty: 2, total: 3100000, status: "Hoàn thành" },
        { id: "HD25081595372", date: "2025-08-15", customer: "Lê Phương Thảo", phone: "0976444333", product: "SP05", qty: 1, total: 2250000, status: "Hoàn thành" },
        { id: "HD25070446928", date: "2025-07-04", customer: "Duy Quyết", phone: "0868219136", product: "SP02", qty: 2, total: 5000000, status: "Hoàn thành" },
        { id: "HD25062289240", date: "2025-06-22", customer: "Phạm Thu Dung", phone: "0981000004", product: "SP07", qty: 1, total: 2850000, status: "Hoàn thành" },
        { id: "HD25051799651", date: "2025-05-17", customer: "Minh Anh", phone: "0912456780", product: "SP08", qty: 2, total: 2600000, status: "Đã giao" },
        { id: "HD25041137145", date: "2025-04-11", customer: "Duy Quyết", phone: "0868219136", product: "SP01", qty: 1, total: 2900000, status: "Hoàn thành" },
        { id: "HD25032869320", date: "2025-03-28", customer: "Nguyễn Thị Lan", phone: "0903555123", product: "SP03", qty: 1, total: 2700000, status: "Hoàn thành" },
        { id: "HD25021490234", date: "2025-02-14", customer: "Trần Văn Minh", phone: "0938111222", product: "SP02", qty: 1, total: 2400000, status: "Hoàn thành" },
        { id: "HD25011038264", date: "2025-01-10", customer: "Lê Phương Thảo", phone: "0976444333", product: "SP04", qty: 1, total: 1500000, status: "Hoàn thành" }
    ];

    // Có thể gán window.RIOR_STATISTICS_DATA.orders từ API/JSP trước khi nạp file này.
    const injectedOrders = window.RIOR_STATISTICS_DATA && window.RIOR_STATISTICS_DATA.orders;
    const orders = Array.isArray(injectedOrders) ? injectedOrders : DEMO_ORDERS;
    const productMap = PRODUCTS.reduce(function (map, product) {
        map[product.id] = product;
        return map;
    }, {});
    const REALIZED_STATUSES = ["Hoàn thành", "Đã giao"];
    const REPORT_DEFAULT = { from: "2026-04-01", to: "2026-04-30" };
    const REFERENCE_DATE = new Date("2026-04-29T12:00:00");
    const MONTH_NAMES = ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"];
    const YEARS = [2026, 2025, 2024];
    const CHART_COLORS = ["#b8956a", "#657589"];
    const STATUS_META = {
        "Chờ xác nhận": { icon: "fa-clock", color: "#a8792a", bg: "#fff7df" },
        "Đã xác nhận": { icon: "fa-check", color: "#4e73a8", bg: "#edf3ff" },
        "Đang xử lý": { icon: "fa-gears", color: "#6f62a8", bg: "#f2efff" },
        "Đang giao": { icon: "fa-truck", color: "#477aa5", bg: "#ebf6ff" },
        "Đã giao": { icon: "fa-box", color: "#607080", bg: "#eff2f4" },
        "Hoàn thành": { icon: "fa-circle-check", color: "#377b58", bg: "#eaf6ef" },
        "Đã hủy": { icon: "fa-circle-xmark", color: "#a84d68", bg: "#fff0f3" },
        "Yêu cầu hoàn": { icon: "fa-rotate-left", color: "#a8792a", bg: "#fff7df" },
        "Đã hoàn tiền": { icon: "fa-money-bill-transfer", color: "#4c8aa2", bg: "#ebf8fc" },
        "Yêu cầu hủy": { icon: "fa-ban", color: "#77717b", bg: "#f2f0f3" }
    };

    let reportOrders = [];
    let selectedMode = "month";
    let selectedPeriod = { month: 4, quarter: 2, year: 2026 };
    let toastTimer = null;

    function currency(value) {
        return new Intl.NumberFormat("vi-VN").format(Math.round(value || 0)) + " đ";
    }

    function compactMoney(value) {
        if (value >= 1000000000) return (value / 1000000000).toFixed(value % 1000000000 ? 1 : 0) + "B";
        if (value >= 1000000) return (value / 1000000).toFixed(value % 1000000 ? 1 : 0) + "M";
        if (value >= 1000) return (value / 1000).toFixed(value % 1000 ? 1 : 0) + "K";
        return String(value);
    }

    function parseDate(value) {
        return new Date(value + "T12:00:00");
    }

    function dateKey(date) {
        return date.getFullYear() + "-" + String(date.getMonth() + 1).padStart(2, "0") + "-" + String(date.getDate()).padStart(2, "0");
    }

    function realized(order) {
        return REALIZED_STATUSES.indexOf(order.status) !== -1;
    }

    function initials(name) {
        if (name === "Khách lẻ") return "KL";
        return name.split(" ").filter(Boolean).slice(-2).map(function (word) { return word.charAt(0); }).join("").toUpperCase();
    }

    function summarize(list) {
        const cancelled = ["Đã hủy", "Yêu cầu hủy", "Đã hoàn tiền"];
        return {
            revenue: list.filter(realized).reduce(function (sum, item) { return sum + Number(item.total || 0); }, 0),
            products: list.reduce(function (sum, item) { return sum + Number(item.qty || 0); }, 0),
            orders: list.length,
            done: list.filter(function (item) { return REALIZED_STATUSES.indexOf(item.status) !== -1; }).length,
            cancelled: list.filter(function (item) { return cancelled.indexOf(item.status) !== -1; }).length,
            processing: list.filter(function (item) { return REALIZED_STATUSES.indexOf(item.status) === -1 && cancelled.indexOf(item.status) === -1; }).length
        };
    }

    function rangeOrders(from, to) {
        return orders.filter(function (order) { return order.date >= from && order.date <= to; });
    }

    function renderOverview() {
        const current = new Date(REFERENCE_DATE);
        const monday = new Date(current);
        monday.setDate(current.getDate() - ((current.getDay() + 6) % 7));
        const ranges = {
            today: [dateKey(current), dateKey(current)],
            week: [dateKey(monday), dateKey(current)],
            month: [current.getFullYear() + "-" + String(current.getMonth() + 1).padStart(2, "0") + "-01", dateKey(current)],
            year: [current.getFullYear() + "-01-01", dateKey(current)]
        };

        document.querySelectorAll(".stat-overview-card").forEach(function (card) {
            const range = ranges[card.dataset.range];
            const summary = summarize(rangeOrders(range[0], range[1]));
            card.querySelector('[data-field="revenue"]').textContent = currency(summary.revenue);
            card.querySelector('[data-field="products"]').textContent = summary.products;
            card.querySelector('[data-field="orders"]').textContent = summary.orders;
            card.querySelector('[data-field="done"]').textContent = summary.done;
            card.querySelector('[data-field="cancelled"]').textContent = summary.cancelled;
            card.querySelector('[data-field="processing"]').textContent = summary.processing;
        });
    }

    function options(items, selected) {
        return items.map(function (item) {
            const value = String(item.value);
            return '<option value="' + value + '"' + (value === String(selected) ? " selected" : "") + ">" + item.label + "</option>";
        }).join("");
    }

    function yearOptions(selected) {
        return options(YEARS.map(function (year) { return { value: year, label: year }; }), selected);
    }

    function renderPeriodFields() {
        const container = document.getElementById("periodFields");
        if (selectedMode === "month") {
            container.innerHTML = '<select id="periodMonth" aria-label="Chọn tháng">' + options(MONTH_NAMES.map(function (label, index) { return { value: index + 1, label: label }; }), selectedPeriod.month) + '</select>' +
                '<select id="periodYear" aria-label="Chọn năm">' + yearOptions(selectedPeriod.year) + '</select>';
        } else if (selectedMode === "quarter") {
            container.innerHTML = '<select id="periodQuarter" aria-label="Chọn quý">' + options([1, 2, 3, 4].map(function (q) { return { value: q, label: "Quý " + q }; }), selectedPeriod.quarter) + '</select>' +
                '<select id="periodYear" aria-label="Chọn năm">' + yearOptions(selectedPeriod.year) + '</select>';
        } else {
            container.innerHTML = '<select id="periodYear" aria-label="Chọn năm">' + yearOptions(selectedPeriod.year) + '</select>';
        }

        container.querySelectorAll("select").forEach(function (select) {
            select.addEventListener("change", function () {
                if (select.id === "periodMonth") selectedPeriod.month = Number(select.value);
                if (select.id === "periodQuarter") selectedPeriod.quarter = Number(select.value);
                if (select.id === "periodYear") selectedPeriod.year = Number(select.value);
                renderCurrentChart();
            });
        });
    }

    function daysInMonth(year, month) {
        return new Date(year, month, 0).getDate();
    }

    function periodSeries(mode, period) {
        let labels = [];
        let title = "";
        let filter;

        if (mode === "month") {
            const count = daysInMonth(period.year, period.month);
            labels = Array.from({ length: count }, function (_, index) { return String(index + 1); });
            const prefix = period.year + "-" + String(period.month).padStart(2, "0") + "-";
            filter = function (order) { return order.date.indexOf(prefix) === 0; };
            title = "Tháng " + String(period.month).padStart(2, "0") + "/" + period.year;
        } else if (mode === "quarter") {
            const startMonth = (period.quarter - 1) * 3 + 1;
            labels = [startMonth, startMonth + 1, startMonth + 2].map(function (month) { return "T" + month; });
            filter = function (order) {
                const year = Number(order.date.slice(0, 4));
                const month = Number(order.date.slice(5, 7));
                return year === period.year && month >= startMonth && month < startMonth + 3;
            };
            title = "Quý " + period.quarter + "/" + period.year;
        } else {
            labels = MONTH_NAMES.map(function (_, index) { return "T" + (index + 1); });
            filter = function (order) { return Number(order.date.slice(0, 4)) === period.year; };
            title = "Năm " + period.year;
        }

        const values = labels.map(function () { return 0; });
        orders.filter(filter).filter(realized).forEach(function (order) {
            let index;
            if (mode === "month") index = Number(order.date.slice(8, 10)) - 1;
            else if (mode === "quarter") index = Number(order.date.slice(5, 7)) - ((period.quarter - 1) * 3 + 1);
            else index = Number(order.date.slice(5, 7)) - 1;
            values[index] += Number(order.total || 0);
        });

        return { labels: labels, values: values, title: title };
    }

    function renderCurrentChart() {
        const series = periodSeries(selectedMode, selectedPeriod);
        let description;
        if (selectedMode === "month") description = "Doanh thu theo ngày trong " + series.title.toLowerCase();
        else if (selectedMode === "quarter") description = "Doanh thu theo tháng trong " + series.title.toLowerCase();
        else description = "Doanh thu theo tháng trong " + series.title.toLowerCase();
        document.getElementById("chartDescription").textContent = description;
        drawChart([{ label: series.title, values: series.values, color: CHART_COLORS[0] }], series.labels);
    }

    function createSmoothPath(points, top, bottom) {
        if (!points.length) return "";
        if (points.length === 1) return "M " + points[0].x + " " + points[0].y;

        const clampY = function (value) { return Math.max(top, Math.min(bottom, value)); };
        let path = "M " + points[0].x + " " + points[0].y;

        for (let index = 0; index < points.length - 1; index += 1) {
            const current = points[index];
            const next = points[index + 1];
            const horizontalHandle = (next.x - current.x) * 0.44;
            const controlOneX = current.x + horizontalHandle;
            const controlOneY = clampY(current.y);
            const controlTwoX = next.x - horizontalHandle;
            const controlTwoY = clampY(next.y);
            path += " C " + controlOneX + " " + controlOneY + ", " + controlTwoX + " " + controlTwoY + ", " + next.x + " " + next.y;
        }
        return path;
    }

    function drawChart(datasets, labels) {
        const chart = document.getElementById("revenueChart");
        const allValues = datasets.reduce(function (result, dataset) { return result.concat(dataset.values); }, []);
        const maxValue = Math.max.apply(null, allValues.concat([0]));
        const legend = document.getElementById("chartLegend");
        legend.innerHTML = datasets.map(function (dataset) {
            return '<span class="stat-legend-item"><i class="stat-legend-color" style="background:' + dataset.color + '"></i>' + dataset.label + "</span>";
        }).join("");

        document.getElementById("chartTotal").textContent = datasets.map(function (dataset) {
            const total = dataset.values.reduce(function (sum, value) { return sum + value; }, 0);
            return datasets.length > 1 ? dataset.label + ": " + currency(total) : currency(total);
        }).join(" · ");

        if (!labels.length) {
            chart.innerHTML = '<div class="stat-chart-empty"><i class="fas fa-chart-line"></i>Không có dữ liệu biểu đồ</div>';
            return;
        }

        const width = 760;
        const height = 270;
        const margin = { top: 12, right: 18, bottom: 34, left: 58 };
        const plotWidth = width - margin.left - margin.right;
        const plotHeight = height - margin.top - margin.bottom;
        const ceiling = maxValue > 0 ? Math.ceil(maxValue / 5000000) * 5000000 : 5000000;
        let svg = '<svg viewBox="0 0 ' + width + " " + height + '" preserveAspectRatio="none" aria-hidden="true"><defs>';
        datasets.forEach(function (dataset, index) {
            svg += '<linearGradient id="statGradient' + index + '" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="' + dataset.color + '" stop-opacity=".07"/><stop offset="100%" stop-color="' + dataset.color + '" stop-opacity="0"/></linearGradient>';
        });
        svg += "</defs>";

        for (let tick = 0; tick <= 4; tick += 1) {
            const y = margin.top + (plotHeight * tick / 4);
            const value = ceiling * (1 - tick / 4);
            svg += '<line class="grid-line" x1="' + margin.left + '" y1="' + y + '" x2="' + (width - margin.right) + '" y2="' + y + '"/>';
            svg += '<text class="axis-label" x="' + (margin.left - 9) + '" y="' + (y + 3) + '" text-anchor="end">' + compactMoney(value) + "</text>";
        }

        const labelStep = labels.length > 16 ? 3 : (labels.length > 8 ? 2 : 1);
        labels.forEach(function (label, index) {
            if (index % labelStep !== 0 && index !== labels.length - 1) return;
            const x = labels.length === 1 ? margin.left + plotWidth / 2 : margin.left + plotWidth * index / (labels.length - 1);
            svg += '<text class="axis-label" x="' + x + '" y="' + (height - 10) + '" text-anchor="middle">' + label + "</text>";
        });

        datasets.forEach(function (dataset, datasetIndex) {
            const points = dataset.values.map(function (value, index) {
                const x = labels.length === 1 ? margin.left + plotWidth / 2 : margin.left + plotWidth * index / (labels.length - 1);
                const y = margin.top + plotHeight - (value / ceiling * plotHeight);
                return { x: x, y: y, value: value };
            });
            const baseline = margin.top + plotHeight;
            const smoothPath = createSmoothPath(points, margin.top, baseline);
            if (datasets.length === 1 && points.length > 1) {
                const areaPath = "M " + points[0].x + " " + baseline + " L " + points[0].x + " " + points[0].y + " " + smoothPath.replace(/^M [^C]+/, "") + " L " + points[points.length - 1].x + " " + baseline + " Z";
                svg += '<path class="chart-area" d="' + areaPath + '" fill="url(#statGradient' + datasetIndex + ')"/>';
            }
            svg += '<path class="chart-line" d="' + smoothPath + '" stroke="' + dataset.color + '"/>';
            points.forEach(function (point, index) {
                if (point.value === 0 && labels.length > 12) return;
                svg += '<circle class="chart-point" cx="' + point.x + '" cy="' + point.y + '" r="2.4" fill="' + dataset.color + '"><title>' + dataset.label + " · " + labels[index] + ": " + currency(point.value) + "</title></circle>";
            });
        });
        svg += "</svg>";
        chart.innerHTML = svg;
    }

    function aggregateProducts(list) {
        const aggregates = {};
        list.forEach(function (order) {
            if (!realized(order)) return;
            const product = productMap[order.product] || { id: order.product, name: order.product, category: "Sản phẩm", stock: 0 };
            if (!aggregates[product.id]) aggregates[product.id] = { product: product, qty: 0, revenue: 0 };
            aggregates[product.id].qty += Number(order.qty || 0);
            aggregates[product.id].revenue += Number(order.total || 0);
        });
        return aggregates;
    }

    function emptyRow(columns, message) {
        return '<tr class="stat-empty-row"><td colspan="' + columns + '"><i class="far fa-folder-open"></i> ' + message + "</td></tr>";
    }

    function renderBestSellers(list) {
        const rows = Object.values(aggregateProducts(list)).sort(function (a, b) { return b.qty - a.qty || b.revenue - a.revenue; }).slice(0, 5);
        document.getElementById("bestSellerBody").innerHTML = rows.length ? rows.map(function (row, index) {
            return "<tr><td><div class=\"stat-product-cell\"><span class=\"stat-rank" + (index === 0 ? " is-top" : "") + "\">#" + (index + 1) + "</span><span><b class=\"stat-cell-main\">" + row.product.name + "</b><small class=\"stat-cell-sub\">" + row.product.category + "</small></span></div></td><td><span class=\"stat-number-pill\">" + row.qty + "</span></td><td><span class=\"stat-number-pill stat-stock-pill\">" + row.product.stock + "</span></td></tr>";
        }).join("") : emptyRow(3, "Không có sản phẩm đã bán trong kỳ");
    }

    function renderCustomers(list) {
        const customers = {};
        list.forEach(function (order) {
            if (!realized(order) || order.customer === "Khách lẻ") return;
            const key = order.customer + order.phone;
            if (!customers[key]) customers[key] = { name: order.customer, phone: order.phone, orders: 0, spent: 0 };
            customers[key].orders += 1;
            customers[key].spent += Number(order.total || 0);
        });
        const rows = Object.values(customers).sort(function (a, b) { return b.spent - a.spent; }).slice(0, 5);
        document.getElementById("customerBody").innerHTML = rows.length ? rows.map(function (row) {
            return '<tr><td><div class="stat-customer-cell"><span class="stat-customer-avatar">' + initials(row.name) + '</span><span><b class="stat-cell-main">' + row.name + '</b><small class="stat-cell-sub">' + row.phone + '</small></span></div></td><td><span class="stat-number-pill">' + row.orders + "</span></td><td><strong>" + currency(row.spent) + "</strong></td></tr>";
        }).join("") : emptyRow(3, "Chưa có dữ liệu khách hàng");
    }

    function renderSlowStock(list) {
        const aggregates = aggregateProducts(list);
        const rows = PRODUCTS.map(function (product) {
            return { product: product, qty: aggregates[product.id] ? aggregates[product.id].qty : 0 };
        }).sort(function (a, b) { return a.qty - b.qty || b.product.stock - a.product.stock; }).slice(0, 6);
        document.getElementById("slowStockBody").innerHTML = rows.map(function (row) {
            return '<tr><td><div class="stat-product-cell"><span><b class="stat-cell-main">' + row.product.name + '</b><small class="stat-cell-sub">' + row.product.category + '</small></span></div></td><td><span class="stat-number-pill">' + row.qty + '</span></td><td><span class="stat-stock-low">' + row.product.stock + "</span></td></tr>";
        }).join("");
    }

    function renderOrderStatuses(list) {
        const counts = {};
        Object.keys(STATUS_META).forEach(function (status) { counts[status] = 0; });
        list.forEach(function (order) { counts[order.status] = (counts[order.status] || 0) + 1; });
        const done = list.filter(realized).length;
        const rate = list.length ? Math.round(done / list.length * 100) : 0;
        document.getElementById("totalOrderBadge").textContent = list.length + " đơn";
        document.getElementById("completionRate").textContent = rate + "%";
        document.getElementById("completionBar").style.width = rate + "%";
        document.getElementById("orderStatusList").innerHTML = Object.keys(STATUS_META).map(function (status) {
            const meta = STATUS_META[status];
            return '<div class="stat-status-item"><span class="stat-status-icon" style="--status-color:' + meta.color + ";--status-bg:" + meta.bg + '"><i class="fas ' + meta.icon + '"></i></span><span>' + status + "</span><strong>" + counts[status] + "</strong></div>";
        }).join("");
    }

    function renderReport() {
        renderBestSellers(reportOrders);
        renderCustomers(reportOrders);
        renderSlowStock(reportOrders);
        renderOrderStatuses(reportOrders);
    }

    function showToast(message) {
        const toast = document.getElementById("statToast");
        document.getElementById("statToastMessage").textContent = message;
        toast.classList.add("is-visible");
        window.clearTimeout(toastTimer);
        toastTimer = window.setTimeout(function () { toast.classList.remove("is-visible"); }, 3000);
    }

    function openModal() {
        const modal = document.getElementById("compareModal");
        document.getElementById("compareMode").value = selectedMode;
        renderCompareFields(selectedMode);
        modal.classList.add("is-open");
        modal.setAttribute("aria-hidden", "false");
    }

    function closeModal() {
        const modal = document.getElementById("compareModal");
        modal.classList.remove("is-open");
        modal.setAttribute("aria-hidden", "true");
    }

    function periodChoices(mode) {
        const choices = [];
        YEARS.forEach(function (year) {
            if (mode === "month") {
                MONTH_NAMES.forEach(function (month, index) { choices.push({ value: year + "-" + (index + 1), label: month + "/" + year }); });
            } else if (mode === "quarter") {
                [1, 2, 3, 4].forEach(function (quarter) { choices.push({ value: year + "-" + quarter, label: "Quý " + quarter + "/" + year }); });
            } else {
                choices.push({ value: String(year), label: "Năm " + year });
            }
        });
        return choices;
    }

    function renderCompareFields(mode) {
        const choices = periodChoices(mode);
        let first;
        let second;
        if (mode === "month") {
            first = selectedPeriod.year + "-" + selectedPeriod.month;
            second = selectedPeriod.year + "-" + Math.max(1, selectedPeriod.month - 1);
        } else if (mode === "quarter") {
            first = selectedPeriod.year + "-" + selectedPeriod.quarter;
            second = selectedPeriod.year + "-" + Math.max(1, selectedPeriod.quarter - 1);
        } else {
            first = String(selectedPeriod.year);
            second = String(selectedPeriod.year - 1);
        }
        document.getElementById("compareFields").innerHTML = '<label class="stat-compare-field"><span>Giai đoạn A</span><select id="compareA">' + options(choices, first) + '</select></label><label class="stat-compare-field"><span>Giai đoạn B</span><select id="compareB">' + options(choices, second) + "</select></label>";
    }

    function keyToPeriod(mode, key) {
        const parts = key.split("-");
        if (mode === "month") return { year: Number(parts[0]), month: Number(parts[1]) };
        if (mode === "quarter") return { year: Number(parts[0]), quarter: Number(parts[1]) };
        return { year: Number(parts[0]) };
    }

    function applyComparison() {
        const mode = document.getElementById("compareMode").value;
        const keyA = document.getElementById("compareA").value;
        const keyB = document.getElementById("compareB").value;
        if (keyA === keyB) {
            showToast("Vui lòng chọn hai giai đoạn khác nhau.");
            return;
        }
        const seriesA = periodSeries(mode, keyToPeriod(mode, keyA));
        const seriesB = periodSeries(mode, keyToPeriod(mode, keyB));
        let comparisonLabels = seriesA.labels;
        if (mode === "month" && seriesA.labels.length !== seriesB.labels.length) {
            const comparisonLength = Math.max(seriesA.labels.length, seriesB.labels.length);
            comparisonLabels = Array.from({ length: comparisonLength }, function (_, index) { return String(index + 1); });
            while (seriesA.values.length < comparisonLength) seriesA.values.push(0);
            while (seriesB.values.length < comparisonLength) seriesB.values.push(0);
        }
        selectedMode = mode;
        document.getElementById("periodMode").value = mode;
        const firstPeriod = keyToPeriod(mode, keyA);
        selectedPeriod = Object.assign(selectedPeriod, firstPeriod);
        renderPeriodFields();
        document.getElementById("chartDescription").textContent = "So sánh " + seriesA.title.toLowerCase() + " và " + seriesB.title.toLowerCase();
        drawChart([
            { label: seriesA.title, values: seriesA.values, color: CHART_COLORS[0] },
            { label: seriesB.title, values: seriesB.values, color: CHART_COLORS[1] }
        ], comparisonLabels);
        closeModal();
        showToast("Đã cập nhật biểu đồ so sánh doanh thu.");
    }

    function applyReportFilter(showMessage) {
        const from = document.getElementById("reportFrom").value;
        const to = document.getElementById("reportTo").value;
        if (!from || !to || parseDate(from) > parseDate(to)) {
            showToast("Khoảng ngày chưa hợp lệ. Vui lòng kiểm tra lại.");
            return false;
        }
        reportOrders = rangeOrders(from, to);
        renderReport();
        if (showMessage) showToast("Đã lọc " + reportOrders.length + " đơn hàng phù hợp.");
        return true;
    }

    function exportCsv() {
        const headers = ["Mã hóa đơn", "Ngày", "Khách hàng", "Số điện thoại", "Sản phẩm", "Số lượng", "Tổng tiền", "Trạng thái"];
        const rows = reportOrders.map(function (order) {
            const product = productMap[order.product];
            return [order.id, order.date, order.customer, order.phone, product ? product.name : order.product, order.qty, order.total, order.status];
        });
        const quote = function (value) { return '"' + String(value).replace(/"/g, '""') + '"'; };
        const csv = "\uFEFF" + [headers].concat(rows).map(function (row) { return row.map(quote).join(","); }).join("\r\n");
        const blob = new Blob([csv], { type: "text/csv;charset=utf-8" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = "bao-cao-thong-ke.csv";
        document.body.appendChild(link);
        link.click();
        link.remove();
        URL.revokeObjectURL(url);
        showToast("Đã xuất báo cáo CSV theo bộ lọc hiện tại.");
    }

    function bindEvents() {
        document.getElementById("periodMode").addEventListener("change", function (event) {
            selectedMode = event.target.value;
            renderPeriodFields();
            renderCurrentChart();
        });
        document.getElementById("openCompare").addEventListener("click", openModal);
        document.querySelectorAll("[data-close-modal]").forEach(function (button) { button.addEventListener("click", closeModal); });
        document.getElementById("compareMode").addEventListener("change", function (event) { renderCompareFields(event.target.value); });
        document.getElementById("applyCompare").addEventListener("click", applyComparison);
        document.getElementById("applyReportFilter").addEventListener("click", function () { applyReportFilter(true); });
        document.getElementById("resetReportFilter").addEventListener("click", function () {
            document.getElementById("reportFrom").value = REPORT_DEFAULT.from;
            document.getElementById("reportTo").value = REPORT_DEFAULT.to;
            applyReportFilter(false);
            showToast("Đã khôi phục bộ lọc mặc định.");
        });
        document.getElementById("exportReport").addEventListener("click", exportCsv);
        document.addEventListener("keydown", function (event) { if (event.key === "Escape") closeModal(); });
    }

    function init() {
        renderOverview();
        renderPeriodFields();
        renderCurrentChart();
        applyReportFilter(false);
        bindEvents();
    }

    if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", init);
    else init();
}());
