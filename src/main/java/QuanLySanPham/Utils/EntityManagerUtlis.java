package main.QuanLySanPham.BE.Utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerUtlis {
    private static final EntityManagerFactory em = Persistence.createEntityManagerFactory("default");
            public static final EntityManager getEntityManager(){
        return em.createEntityManager();
            }
}
