package net.sourceforge.javydreamercsw.database.storage.db.server;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataBaseManager {

    public static EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("TMPU");
    }
}
