package QuanLyHoaDon.Model;

import java.math.BigDecimal;

public class ThongKeBreakdown {
    private String label;
    private BigDecimal value = BigDecimal.ZERO;
    private BigDecimal previousValue = BigDecimal.ZERO;
    private int count;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value == null ? BigDecimal.ZERO : value;
    }

    public BigDecimal getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(BigDecimal previousValue) {
        this.previousValue = previousValue == null ? BigDecimal.ZERO : previousValue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
