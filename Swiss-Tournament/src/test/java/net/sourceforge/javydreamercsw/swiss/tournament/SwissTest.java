/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.swiss.tournament;

import static junit.framework.Assert.assertEquals;
import net.sourceforge.javydreamercsw.tournament.manager.AbstractTournamentTester;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SwissTest extends AbstractTournamentTester {

    public SwissTest() {
        super(new Swiss());
    }

    @Override
    public TournamentInterface generateRandomTournament() {
        return new Swiss();
    }
}
