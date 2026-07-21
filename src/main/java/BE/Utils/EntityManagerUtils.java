package BE.Utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import BE.jdbc.DatabaseConnectionManager;

public class EntityManagerUtils {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory(
                    "default",
                    DatabaseConnectionManager.fromEnvironment().toJpaProperties()
            );

    public EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }
}
