package BE.jdbc;

import BE.Model.ThongKeOverview;
import BE.service.ThongKeService;

public class ThongKeDataCheck {
    public static void main(String[] args) throws Exception {
        ThongKeService service = new ThongKeService();
        ThongKeOverview year = service.getYearOverview();
        System.out.println("Orders: " + year.getOrders());
        System.out.println("Revenue: " + year.getRevenue());
        System.out.println("Products: " + year.getProducts());
        System.out.println("Best sellers: " + service.getBestSellers().size());
        System.out.println("Top customers: " + service.getTopCustomers().size());
        System.out.println("Slow stock: " + service.getSlowStockProducts().size());
    }
}
