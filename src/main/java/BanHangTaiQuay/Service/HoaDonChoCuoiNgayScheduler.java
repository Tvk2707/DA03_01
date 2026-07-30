package BanHangTaiQuay.Service;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class HoaDonChoCuoiNgayScheduler implements ServletContextListener {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final LocalTime END_OF_DAY_RUN_TIME = LocalTime.of(23, 59, 59);
    private static final String AUTO_CANCEL_REASON_PREFIX = "AUTO_CANCEL_PENDING_INVOICES_EOD";

    private ScheduledExecutorService scheduler;
    private final BanHangService banHangService = new BanHangServiceImpl();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "hoa-don-cho-cuoi-ngay-scheduler");
            thread.setDaemon(true);
            return thread;
        });
        cancelMissedPendingInvoices(sce);
        scheduleNextRun();
        sce.getServletContext().log("Da khoi dong lich tu dong huy hoa don cho luc 23:59:59 moi ngay.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdownNow();
            sce.getServletContext().log("Da dung lich tu dong huy hoa don cho cuoi ngay.");
        }
    }

    private void scheduleNextRun() {
        if (scheduler == null || scheduler.isShutdown()) {
            return;
        }

        ZonedDateTime now = ZonedDateTime.now(BUSINESS_ZONE);
        LocalDate businessDate = now.toLocalDate();
        ZonedDateTime runAt = businessDate.atTime(END_OF_DAY_RUN_TIME).atZone(BUSINESS_ZONE);
        if (!runAt.isAfter(now)) {
            businessDate = businessDate.plusDays(1);
            runAt = businessDate.atTime(END_OF_DAY_RUN_TIME).atZone(BUSINESS_ZONE);
        }

        long delayMillis = Math.max(0, Duration.between(now, runAt).toMillis());
        LocalDate targetBusinessDate = businessDate;
        scheduler.schedule(() -> runAndScheduleNext(targetBusinessDate), delayMillis, TimeUnit.MILLISECONDS);
    }

    private void cancelMissedPendingInvoices(ServletContextEvent sce) {
        LocalDate previousBusinessDate = ZonedDateTime.now(BUSINESS_ZONE).toLocalDate().minusDays(1);
        try {
            int canceledCount = banHangService.huyHoaDonChoCuoiNgay(previousBusinessDate);
            if (canceledCount > 0) {
                sce.getServletContext().log("Da huy bu " + canceledCount
                        + " hoa don cho cua cac ngay truoc " + previousBusinessDate.plusDays(1) + ".");
            }
        } catch (Exception error) {
            sce.getServletContext().log("Loi khi huy bu hoa don cho cuoi ngay.", error);
        }
    }

    private void runAndScheduleNext(LocalDate businessDate) {
        try {
            int canceledCount = banHangService.huyHoaDonChoCuoiNgay(businessDate);
            System.out.printf("%s %s: da huy %d hoa don cho.%n",
                    AUTO_CANCEL_REASON_PREFIX, businessDate, canceledCount);
        } catch (Exception error) {
            System.err.printf("%s %s: loi khi huy hoa don cho: %s%n",
                    AUTO_CANCEL_REASON_PREFIX, businessDate, error.getMessage());
            error.printStackTrace(System.err);
        } finally {
            scheduleNextRun();
        }
    }
}
