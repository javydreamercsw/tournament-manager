/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db;

import net.sourceforge.javydreamercsw.database.storage.db.server.DataBaseManager;
import org.junit.Before;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AbstractServerTest {

    public AbstractServerTest() {
    }

    @Before
    public void setUp() {
        DataBaseManager.setPU("TestTMPU");
    }
}
