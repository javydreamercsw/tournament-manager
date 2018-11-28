package com.github.javydreamercsw.database.storage.db;


import org.testng.annotations.BeforeClass;

import com.github.javydreamercsw.database.storage.db.server.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AbstractServerTest {

    public AbstractServerTest() {
    }

    @BeforeClass
    public void setup() {
        DataBaseManager.setPersistenceUnitName("TestTMPU");
    }
}
