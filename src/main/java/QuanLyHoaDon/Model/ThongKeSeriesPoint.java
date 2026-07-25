package QuanLyHoaDon.Model;

import java.math.BigDecimal;

public class ThongKeSeriesPoint {
    private String label;
    private BigDecimal value = BigDecimal.ZERO;

    public ThongKeSeriesPoint() {
    }

    public ThongKeSeriesPoint(String label, BigDecimal value) {
        this.label = label;
        setValue(value);
    }

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
}
