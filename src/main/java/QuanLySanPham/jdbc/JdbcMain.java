package main.QuanLySanPham.BE.jdbc;

import main.QuanLySanPham.BE.Utils.EntityManagerUtlis;

public class JdbcMain {

    public static void main(String[] args) {

        DatabaseConnectionManager dcm = new DatabaseConnectionManager("quan_ly_ban_kinh", "sa", "123");

        //test connection
        //try (Connection connection = dcm.getConnection()) {
        //
        //    System.out.println("Connected...");
        //
        //} catch (SQLException e) {
        //    System.out.println("Failed to connect to database");
        //    e.printStackTrace();
        //}

        //creating EM => auto create tables in DB
        EntityManagerUtlis EntityManagerUtils = new EntityManagerUtlis();
        try (var em = EntityManagerUtils.getEntityManager()) {

            System.out.println("Created tables...");

        } catch (Exception e) {
            System.out.println("Failed to create Entity Manager");
            e.printStackTrace();
        }
    }

}
