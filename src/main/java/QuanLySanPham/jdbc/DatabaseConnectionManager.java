package QuanLySanPham.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {

    private final String url;

    public DatabaseConnectionManager(String databaseName, String username, String password) {

        this.url = "jdbc:sqlserver://localhost:1433;database=" + databaseName
                + ";user=" + username
                + ";password=" + password
                + ";encrypt=true;" + "trustServerCertificate=true;" + "loginTimeout=30;";
    }

    public static DatabaseConnectionManager fromEnvironment() {
        String dbName = "quan_ly_ban_kinh"; // Ví dụ: "QuanLySanPham"
        String user = "sa";                  // Tài khoản SQL Server
        String pass = "123";
        DatabaseConnectionManager manager = new DatabaseConnectionManager(dbName, user, pass);
        return manager;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Failed to load SQL Server driver.", e);
        }
        return DriverManager.getConnection(this.url);
    }
}