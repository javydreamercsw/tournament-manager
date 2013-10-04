/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.tournament.manager;

import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.javydreamercsw.tournament.manager.api.Variables;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Player implements TournamentPlayerInterface {

    private final Map<String, Object> variables = new HashMap<String, Object>();

    public Player(String name) {
        variables.put(Variables.PLAYER_NAME.getDisplayName(), name);
        variables.put(Variables.WINS.getDisplayName(), 0);
        variables.put(Variables.DRAWS.getDisplayName(), 0);
        variables.put(Variables.LOSSES.getDisplayName(), 0);
        //TODO: Allow player modification
    }

    /**
     * Get the value for the specified key.
     *
     * @param key Key to look for
     * @return value for the key provided or null if not found.
     */
    @Override
    public Object get(String key) {
        return variables.get(key);
    }

    @Override
    public String toString() {
        return get(Variables.PLAYER_NAME.getDisplayName()) + " ("
                + get(Variables.WINS.getDisplayName()) + "-"
                + get(Variables.LOSSES.getDisplayName()) + "-"
                + get(Variables.DRAWS.getDisplayName()) + ")";
    }

    @Override
    public void win() {
        variables.put(Variables.WINS.getDisplayName(),
                ((Integer) get(Variables.WINS.getDisplayName())) + 1);
    }

    @Override
    public void loss() {
        variables.put(Variables.LOSSES.getDisplayName(),
                ((Integer) get(Variables.LOSSES.getDisplayName())) + 1);
    }

    @Override
    public void draw() {
        variables.put(Variables.DRAWS.getDisplayName(),
                ((Integer) get(Variables.DRAWS.getDisplayName())) + 1);
    }

    @Override
    public int getWins() {
        return ((Integer) get(Variables.WINS.getDisplayName()));
    }

    @Override
    public int getDraws() {
        return ((Integer) get(Variables.DRAWS.getDisplayName()));
    }

    @Override
    public int getLosses() {
        return ((Integer) get(Variables.LOSSES.getDisplayName()));
    }

    @Override
    public String getName() {
        return ((String) get(Variables.PLAYER_NAME.getDisplayName()));
    }
}
