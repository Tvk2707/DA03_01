package QuanLyHoaDon.Model;

import java.util.ArrayList;
import java.util.List;

public class ThongKeReport {
    private ThongKeOverview overview = new ThongKeOverview();
    private ThongKeOverview previousOverview = new ThongKeOverview();
    private ThongKeFinance finance = new ThongKeFinance();
    private List<ThongKeProduct> bestSellers = new ArrayList<>();
    private List<ThongKeProduct> slowStockProducts = new ArrayList<>();
    private List<ThongKeProduct> lowStockProducts = new ArrayList<>();
    private List<ThongKeCustomer> topCustomers = new ArrayList<>();
    private List<ThongKeEmployee> employeeStats = new ArrayList<>();
    private List<ThongKeVoucher> voucherStats = new ArrayList<>();
    private List<ThongKeBreakdown> paymentStats = new ArrayList<>();
    private List<ThongKeBreakdown> categoryStats = new ArrayList<>();

    public ThongKeOverview getOverview() {
        return overview;
    }

    public void setOverview(ThongKeOverview overview) {
        this.overview = overview == null ? new ThongKeOverview() : overview;
    }

    public ThongKeOverview getPreviousOverview() {
        return previousOverview;
    }

    public void setPreviousOverview(ThongKeOverview previousOverview) {
        this.previousOverview = previousOverview == null ? new ThongKeOverview() : previousOverview;
    }

    public ThongKeFinance getFinance() {
        return finance;
    }

    public void setFinance(ThongKeFinance finance) {
        this.finance = finance == null ? new ThongKeFinance() : finance;
    }

    public List<ThongKeProduct> getBestSellers() {
        return bestSellers;
    }

    public void setBestSellers(List<ThongKeProduct> bestSellers) {
        this.bestSellers = bestSellers == null ? new ArrayList<>() : bestSellers;
    }

    public List<ThongKeProduct> getSlowStockProducts() {
        return slowStockProducts;
    }

    public void setSlowStockProducts(List<ThongKeProduct> slowStockProducts) {
        this.slowStockProducts = slowStockProducts == null ? new ArrayList<>() : slowStockProducts;
    }

    public List<ThongKeProduct> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<ThongKeProduct> lowStockProducts) {
        this.lowStockProducts = lowStockProducts == null ? new ArrayList<>() : lowStockProducts;
    }

    public List<ThongKeCustomer> getTopCustomers() {
        return topCustomers;
    }

    public void setTopCustomers(List<ThongKeCustomer> topCustomers) {
        this.topCustomers = topCustomers == null ? new ArrayList<>() : topCustomers;
    }

    public List<ThongKeEmployee> getEmployeeStats() {
        return employeeStats;
    }

    public void setEmployeeStats(List<ThongKeEmployee> employeeStats) {
        this.employeeStats = employeeStats == null ? new ArrayList<>() : employeeStats;
    }

    public List<ThongKeVoucher> getVoucherStats() {
        return voucherStats;
    }

    public void setVoucherStats(List<ThongKeVoucher> voucherStats) {
        this.voucherStats = voucherStats == null ? new ArrayList<>() : voucherStats;
    }

    public List<ThongKeBreakdown> getPaymentStats() {
        return paymentStats;
    }

    public void setPaymentStats(List<ThongKeBreakdown> paymentStats) {
        this.paymentStats = paymentStats == null ? new ArrayList<>() : paymentStats;
    }

    public List<ThongKeBreakdown> getCategoryStats() {
        return categoryStats;
    }

    public void setCategoryStats(List<ThongKeBreakdown> categoryStats) {
        this.categoryStats = categoryStats == null ? new ArrayList<>() : categoryStats;
    }
}
