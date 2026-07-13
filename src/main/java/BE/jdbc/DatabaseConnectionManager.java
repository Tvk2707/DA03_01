package BE.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Quan ly ket noi SQL Server cho toan bo tang DAO.
public class DatabaseConnectionManager {

    // Cau hinh mac dinh, co the ghi de bang system property hoac environment variable.
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1433;
    private static final String DEFAULT_DATABASE_NAME = "quan_ly_ban_kinh";
    private static final String DEFAULT_USERNAME = "minh";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final boolean DEFAULT_INTEGRATED_SECURITY = false;

    private final String url;
    private final String username;
    private final String password;
    private final boolean integratedSecurity;

    public DatabaseConnectionManager(String databaseName, String username, String password) {
        this(DEFAULT_HOST, DEFAULT_PORT, databaseName, username, password, false);
    }

    public DatabaseConnectionManager(String host, int port, String databaseName, String username, String password) {
        this(host, port, databaseName, username, password, false);
    }

    public DatabaseConnectionManager(String host, int port, String databaseName, String username, String password,
            boolean integratedSecurity) {
        this.url = "jdbc:sqlserver://" + host + ":" + port
                + ";databaseName=" + databaseName
                + ";encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=30;"
                + (integratedSecurity ? "integratedSecurity=true;" : "");
        this.username = username;
        this.password = password;
        this.integratedSecurity = integratedSecurity;
    }

    public static DatabaseConnectionManager fromEnvironment() {
        // Uu tien doc cau hinh tu bien moi truong, neu khong co thi dung gia tri mac dinh.
        String host = getSetting("db.host", "DB_HOST", DEFAULT_HOST);
        int port = getIntSetting("db.port", "DB_PORT", DEFAULT_PORT);
        String databaseName = getSetting("db.name", "DB_NAME", DEFAULT_DATABASE_NAME);
        String username = getSetting("db.user", "DB_USER", DEFAULT_USERNAME);
        String password = getSetting("db.password", "DB_PASSWORD", DEFAULT_PASSWORD);
        boolean integratedSecurity = getBooleanSetting(
                "db.integratedSecurity",
                "DB_INTEGRATED_SECURITY",
                DEFAULT_INTEGRATED_SECURITY
        );

        return new DatabaseConnectionManager(host, port, databaseName, username, password, integratedSecurity);
    }

    public Connection getConnection() throws SQLException {
        // DAO goi ham nay de lay Connection moi moi khi can truy van database.
        loadSqlServerDriver();

        if (this.integratedSecurity) {
            return DriverManager.getConnection(this.url);
        }
        return DriverManager.getConnection(this.url, this.username, this.password);
    }

    public Map<String, String> toJpaProperties() {
        // Ham nay phuc vu JPA neu can dung EntityManager trong cac phan khac.
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        properties.put("jakarta.persistence.jdbc.url", this.url);
        if (!this.integratedSecurity) {
            properties.put("jakarta.persistence.jdbc.user", this.username);
            properties.put("jakarta.persistence.jdbc.password", this.password);
        }
        return properties;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        if (this.integratedSecurity) {
            return "Windows Authentication";
        }
        return username;
    }

    private void loadSqlServerDriver() throws SQLException {
        // Nap driver JDBC de DriverManager biet cach ket noi SQL Server.
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy SQL Server JDBC Driver trong WEB-INF/lib.", e);
        }
    }

    private static String getSetting(String propertyName, String environmentName, String defaultValue) {
        // Doc gia tri theo thu tu: JVM property -> bien moi truong -> mac dinh.
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

    private static boolean getBooleanSetting(String propertyName, String environmentName, boolean defaultValue) {
        String value = getSetting(propertyName, environmentName, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
}
