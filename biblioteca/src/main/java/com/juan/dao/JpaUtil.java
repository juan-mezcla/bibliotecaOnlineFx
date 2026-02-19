package com.juan.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaUtil {

    // La fábrica es estática y final (Singleton)
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY;

    // Bloque estático: Se ejecuta una sola vez al cargar la clase
    static {
        try {
            // "miUnidadPersistencia" debe coincidir con tu persistence.xml
            ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("miUnidadPersistencia");
        } catch (Throwable ex) {
            System.err.println("Fallo al iniciar EntityManagerFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Constructor privado para evitar que se instancie la clase utilitaria
    private JpaUtil() {}

    /**
     * Obtiene una nueva instancia de EntityManager.
     * Úsalo y ciérralo en cada operación.
     */
    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    /**
     * Cierra la fábrica.
     * Se debe llamar solo al detener la aplicación completa.
     */
    public static void shutdown() {
        if (ENTITY_MANAGER_FACTORY != null && ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}
