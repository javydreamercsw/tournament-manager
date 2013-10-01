/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Player {

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

    public void win() {
        variables.put(Variables.WINS.getDisplayName(),
                ((Integer) get(Variables.WINS.getDisplayName())) + 1);
    }

    public void loss() {
        variables.put(Variables.LOSSES.getDisplayName(),
                ((Integer) get(Variables.LOSSES.getDisplayName())) + 1);
    }

    public void draw() {
        variables.put(Variables.DRAWS.getDisplayName(),
                ((Integer) get(Variables.DRAWS.getDisplayName())) + 1);
    }

    public int getWins() {
        return ((Integer) get(Variables.WINS.getDisplayName()));
    }

    public int getDraws() {
        return ((Integer) get(Variables.DRAWS.getDisplayName()));
    }

    public int getLosses() {
        return ((Integer) get(Variables.LOSSES.getDisplayName()));
    }
}
