<<<<<<< HEAD:src/main/java/BE/jdbc/JdbcMain.java
package BE.jdbc;

import BE.Utils.EntityManagerUtils;

import jakarta.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
=======
package QuanLySanPham.jdbc;

import QuanLySanPham.Utils.EntityManagerUtlis;
>>>>>>> master:src/main/java/QuanLySanPham/jdbc/JdbcMain.java

public class JdbcMain {

    public static void main(String[] args) {

<<<<<<< HEAD:src/main/java/BE/jdbc/JdbcMain.java
        DatabaseConnectionManager dcm = DatabaseConnectionManager.fromEnvironment();
=======
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("quan_ly_ban_kinh", "sa", "123");
>>>>>>> master:src/main/java/QuanLySanPham/jdbc/JdbcMain.java

        try (Connection connection = dcm.getConnection()) {
            System.out.println("Connected to database: " + connection.getCatalog());
        } catch (SQLException e) {
            System.out.println("Failed to connect to database");
            System.out.println("URL: " + dcm.getUrl());
            System.out.println("User: " + dcm.getUsername());
            System.out.println("Override with -Ddb.user=... -Ddb.password=... if these credentials are not correct.");
            e.printStackTrace();
            return;
        }

        EntityManagerUtils entityManagerUtils = new EntityManagerUtils();
        try (EntityManager em = entityManagerUtils.getEntityManager()) {
            em.createNativeQuery("SELECT 1").getSingleResult();
            System.out.println("EntityManager connected...");
        } catch (Exception e) {
            System.out.println("Failed to create Entity Manager");
            e.printStackTrace();
        }
    }

}
