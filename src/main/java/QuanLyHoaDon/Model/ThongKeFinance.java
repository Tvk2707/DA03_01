package QuanLyHoaDon.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ThongKeFinance {
    private BigDecimal revenue = BigDecimal.ZERO;
    private BigDecimal cost = BigDecimal.ZERO;
    private BigDecimal profit = BigDecimal.ZERO;
    private BigDecimal marginPercent = BigDecimal.ZERO;

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue == null ? BigDecimal.ZERO : revenue;
        recalculate();
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost == null ? BigDecimal.ZERO : cost;
        recalculate();
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public BigDecimal getMarginPercent() {
        return marginPercent;
    }

    private void recalculate() {
        profit = revenue.subtract(cost);
        if (revenue.signum() <= 0) {
            marginPercent = BigDecimal.ZERO;
            return;
        }
        marginPercent = profit.multiply(BigDecimal.valueOf(100))
                .divide(revenue, 1, RoundingMode.HALF_UP);
    }
}
