/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class TournamentHasTeamPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "tournament_id")
    private int tournamentId;
    @Basic(optional = false)
    @Column(name = "team_id")
    private int teamId;

    public TournamentHasTeamPK() {
    }

    public TournamentHasTeamPK(int tournamentId, int teamId) {
        this.tournamentId = tournamentId;
        this.teamId = teamId;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) tournamentId;
        hash += (int) teamId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TournamentHasTeamPK)) {
            return false;
        }
        TournamentHasTeamPK other = (TournamentHasTeamPK) object;
        if (this.tournamentId != other.tournamentId) {
            return false;
        }
        if (this.teamId != other.teamId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.database.storage.db.TournamentHasTeamPK[ tournamentId=" + tournamentId + ", teamId=" + teamId + " ]";
    }

}
