package QuanLySanPham.filter;

import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.controller.LoginServlet;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

@WebFilter(filterName = "PhanQuyenFilter", urlPatterns = "/*")
public class PhanQuyenFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/", "/index.jsp", "/Login", "/Login.jsp", "/Register", "/Register.jsp",
            "/Logout", "/AccessDenied.jsp"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = getPath(request);

        if (isPublic(path) || isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute(LoginServlet.SESSION_KEY);
        if (!(value instanceof NhanVien)) {
            handleUnauthenticated(request, response);
            return;
        }

        NhanVien currentUser = (NhanVien) value;
        if (isManagerOnly(path) && !currentUser.isQuanLy()) {
            handleForbidden(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private String getPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        return uri.substring(contextPath.length());
    }

    private boolean isPublic(String path) {
        return PUBLIC_PATHS.contains(path);
    }

    private boolean isStaticResource(String path) {
        String lowerPath = path.toLowerCase();
        return path.startsWith("/assets/")
                || path.startsWith("/Admin/css/")
                || path.startsWith("/Admin/js/")
                || path.startsWith("/File_Anh/")
                || lowerPath.endsWith(".css")
                || lowerPath.endsWith(".js")
                || lowerPath.endsWith(".png")
                || lowerPath.endsWith(".jpg")
                || lowerPath.endsWith(".jpeg")
                || lowerPath.endsWith(".gif")
                || lowerPath.endsWith(".svg")
                || lowerPath.endsWith(".ico")
                || lowerPath.endsWith(".woff")
                || lowerPath.endsWith(".woff2");
    }

    private boolean isManagerOnly(String path) {
        return path.equals("/NhanVien")
                || path.startsWith("/NhanVien/")
                || path.equals("/admin/thong-ke")
                || path.startsWith("/Admin/QuanLyNhanVien/");
    }

    private void handleUnauthenticated(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (isAjax(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Phiên đăng nhập đã hết hạn\"}");
            return;
        }
        String query = request.getQueryString();
        String requestedUrl = request.getRequestURI() + (query == null ? "" : "?" + query);
        request.getSession(true).setAttribute("redirectAfterLogin", requestedUrl);
        response.sendRedirect(request.getContextPath() + "/Login");
    }

    private void handleForbidden(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (isAjax(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Bạn không có quyền thực hiện thao tác này\"}");
            return;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.setAttribute("error", "Chức năng này chỉ dành cho tài khoản Quản lý.");
        request.getRequestDispatcher("/AccessDenied.jsp").forward(request, response);
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}
