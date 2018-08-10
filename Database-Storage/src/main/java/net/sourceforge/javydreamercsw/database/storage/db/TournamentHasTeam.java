/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "tournament_has_team")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TournamentHasTeam.findAll", query = "SELECT t FROM TournamentHasTeam t"),
    @NamedQuery(name = "TournamentHasTeam.findByTournamentId", query = "SELECT t FROM TournamentHasTeam t WHERE t.tournamentHasTeamPK.tournamentId = :tournamentId"),
    @NamedQuery(name = "TournamentHasTeam.findByTeamId", query = "SELECT t FROM TournamentHasTeam t WHERE t.tournamentHasTeamPK.teamId = :teamId")})
public class TournamentHasTeam implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TournamentHasTeamPK tournamentHasTeamPK;
    @JoinTable(name = "tournament_has_team_has_record", joinColumns = {
        @JoinColumn(name = "tournament_has_team_tournament_id", referencedColumnName = "tournament_id"),
        @JoinColumn(name = "tournament_has_team_team_id", referencedColumnName = "team_id")}, inverseJoinColumns = {
        @JoinColumn(name = "record_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Record> recordList;
    @JoinColumn(name = "team_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Team team;
    @JoinColumn(name = "tournament_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Tournament tournament;

    public TournamentHasTeam() {
    }

    public TournamentHasTeam(TournamentHasTeamPK tournamentHasTeamPK) {
        this.tournamentHasTeamPK = tournamentHasTeamPK;
    }

    public TournamentHasTeam(int tournamentId, int teamId) {
        this.tournamentHasTeamPK = new TournamentHasTeamPK(tournamentId, teamId);
    }

    public TournamentHasTeamPK getTournamentHasTeamPK() {
        return tournamentHasTeamPK;
    }

    public void setTournamentHasTeamPK(TournamentHasTeamPK tournamentHasTeamPK) {
        this.tournamentHasTeamPK = tournamentHasTeamPK;
    }

    @XmlTransient
    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tournamentHasTeamPK != null ? tournamentHasTeamPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TournamentHasTeam)) {
            return false;
        }
        TournamentHasTeam other = (TournamentHasTeam) object;
        if ((this.tournamentHasTeamPK == null && other.tournamentHasTeamPK != null) || (this.tournamentHasTeamPK != null && !this.tournamentHasTeamPK.equals(other.tournamentHasTeamPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.database.storage.db.TournamentHasTeam[ tournamentHasTeamPK=" + tournamentHasTeamPK + " ]";
    }

}