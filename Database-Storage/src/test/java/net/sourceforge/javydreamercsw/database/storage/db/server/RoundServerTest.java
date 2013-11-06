/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db.server;

import net.sourceforge.javydreamercsw.database.storage.db.AbstractServerTest;
import net.sourceforge.javydreamercsw.database.storage.db.TestHelper;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RoundServerTest extends AbstractServerTest {

    public RoundServerTest() {
    }

    /**
     * Test of write2DB method, of class RoundServer.
     */
    @Test
    public void testWrite2DB() {
        System.out.println("write2DB");
        TournamentServer tm = TestHelper.createTournament("Test");
        tm.write2DB();
        RoundServer instance = new RoundServer(tm.getEntity());
        assertTrue(instance.write2DB() > 0);
    }
}
