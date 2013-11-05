/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db.server;

import net.sourceforge.javydreamercsw.tournament.manager.Player;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class PlayerServerTest {

    public PlayerServerTest() {
    }

    /**
     * Test of write2DB method, of class PlayerServer.
     */
    @Test
    public void testWrite2DB() {
        System.out.println("write2DB");
        PlayerServer instance = new PlayerServer(new Player("Test"));
        assertTrue(instance.write2DB() > 0);

    }
}
