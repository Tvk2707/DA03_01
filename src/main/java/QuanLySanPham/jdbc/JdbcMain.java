package QuanLySanPham.jdbc;

import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcMain {
    public static void main(String[] args) {
        DatabaseConnectionManager dcm = DatabaseConnectionManager.fromEnvironment();

        try (Connection connection = dcm.getConnection()) {
            System.out.println("Connected to database: " + connection.getCatalog());
        } catch (SQLException e) {
            System.out.println("Failed to connect to database");
            e.printStackTrace();
            return;
        }

        try (EntityManager em = EntityManagerUtlis.getEntityManager()) {
            em.createNativeQuery("SELECT 1").getSingleResult();
            System.out.println("EntityManager connected...");
        } catch (Exception e) {
            System.out.println("Failed to create Entity Manager");
            e.printStackTrace();
        }
    }
}
