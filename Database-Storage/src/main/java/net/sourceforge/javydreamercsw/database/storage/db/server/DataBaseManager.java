package net.sourceforge.javydreamercsw.database.storage.db.server;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataBaseManager {

    private static String PU = "TMPU";

    public static EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PU);
    }

    /**
     * @param aPU the PU to set
     */
    public static void setPU(String aPU) {
        PU = aPU;
    }
}
