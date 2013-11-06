/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db.server;

import net.sourceforge.javydreamercsw.database.storage.db.Record;
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import net.sourceforge.javydreamercsw.tournament.manager.Player;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class PlayerServerTest {

    public PlayerServerTest() {
    }

    @Before
    public void setUp() {
        DataBaseManager.setPU("TestTMPU");
    }

    /**
     * Test of write2DB method, of class PlayerServer.
     */
    @Test
    public void testWrite2DB() {
        System.out.println("write2DB");
        DataBaseManager.setPU("TestTMPU");
        PlayerServer instance = new PlayerServer(new Player("Test"));
        assertTrue(instance.write2DB() > 0);
        assertEquals(0, instance.getRecordList().size());
        assertEquals(0, instance.getTeamList().size());
        instance.getRecordList().add(new Record());
        instance.write2DB();
        assertEquals(1, instance.getRecordList().size());
        instance.getTeamList().add(new Team());
        instance.write2DB();
        assertEquals(1, instance.getTeamList().size());
    }
}
