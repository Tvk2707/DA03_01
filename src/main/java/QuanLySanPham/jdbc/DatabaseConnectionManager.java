package QuanLySanPham.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Quan ly ket noi SQL Server cho module QuanLySanPham.
public class DatabaseConnectionManager {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1433;
    private static final String DEFAULT_DATABASE_NAME = "quan_ly_ban_kinh";
    private static final String DEFAULT_USERNAME = "minh";
    private static final String DEFAULT_PASSWORD = "123456";

    private final String url;
    private final String username;
    private final String password;

    public DatabaseConnectionManager(String databaseName, String username, String password) {
        this(DEFAULT_HOST, DEFAULT_PORT, databaseName, username, password);
    }

    public DatabaseConnectionManager(String host, int port, String databaseName, String username, String password) {
        this.url = "jdbc:sqlserver://" + host + ":" + port
                + ";databaseName=" + databaseName
                + ";encrypt=false;"
                + "trustServerCertificate=true;"
                + "loginTimeout=30;";
        this.username = username;
        this.password = password;
    }

    public static DatabaseConnectionManager fromEnvironment() {
        String host = getSetting("db.host", "DB_HOST", DEFAULT_HOST);
        int port = getIntSetting("db.port", "DB_PORT", DEFAULT_PORT);
        String databaseName = getSetting("db.name", "DB_NAME", DEFAULT_DATABASE_NAME);
        String username = getSetting("db.user", "DB_USER", DEFAULT_USERNAME);
        String password = getSetting("db.password", "DB_PASSWORD", DEFAULT_PASSWORD);
        return new DatabaseConnectionManager(host, port, databaseName, username, password);
    }

    public Connection getConnection() throws SQLException {
        loadSqlServerDriver();
        return DriverManager.getConnection(this.url, this.username, this.password);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    private void loadSqlServerDriver() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Khong tim thay SQL Server JDBC Driver trong WEB-INF/lib.", e);
        }
    }

    private static String getSetting(String propertyName, String environmentName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue;
        }

        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.trim().isEmpty()) {
            return environmentValue;
        }

        return defaultValue;
    }

    private static int getIntSetting(String propertyName, String environmentName, int defaultValue) {
        String value = getSetting(propertyName, environmentName, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
