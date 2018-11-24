package com.github.javydreamercsw.tournament.manager;

import com.github.javydreamercsw.tournament.manager.api.standing.RecordInterface;

import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = RecordInterface.class)
public class DefaultRecord implements RecordInterface {

    private int draws = 0;
    private int loses = 0;
    private int wins = 0;

    @Override
    public void draw() {
        draws++;
    }

    @Override
    public int getDraws() {
        return draws;
    }

    @Override
    public int getLosses() {
        return loses;
    }

    @Override
    public int getWins() {
        return wins;
    }

    @Override
    public void loss() {
        loses++;
    }

    @Override
    public void win() {
        wins++;
    }

    @Override
    public RecordInterface getNewInstance() {
        return new DefaultRecord();
    }

    @Override
    public void setDraws(int draws) {
        this.draws = draws;
    }

    @Override
    public void setLosses(int losses) {
        this.loses = losses;
    }

    @Override
    public void setWins(int wins) {
        this.wins = wins;
    }
}
