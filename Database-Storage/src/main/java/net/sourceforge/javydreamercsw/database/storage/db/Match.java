/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "match")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Match.findAll", query = "SELECT m FROM Match m"),
    @NamedQuery(name = "Match.findById", query = "SELECT m FROM Match m WHERE m.matchPK.id = :id"),
    @NamedQuery(name = "Match.findByRoundId", query = "SELECT m FROM Match m WHERE m.matchPK.roundId = :roundId")})
public class Match implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "match")
    private List<MatchHasTeam> matchHasTeamList;

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MatchPK matchPK;
    @JoinTable(name = "match_has_team", joinColumns = {
        @JoinColumn(name = "match_id", referencedColumnName = "id"),
        @JoinColumn(name = "round_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "team_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Team> teamList;
    @JoinColumns({
        @JoinColumn(name = "round_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "tournament_id", referencedColumnName = "id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Round round;

    public Match() {
    }

    public Match(MatchPK matchPK) {
        this.matchPK = matchPK;
    }

    public Match(int roundId) {
        this.matchPK = new MatchPK(roundId);
    }

    public MatchPK getMatchPK() {
        return matchPK;
    }

    public void setMatchPK(MatchPK matchPK) {
        this.matchPK = matchPK;
    }

    @XmlTransient
    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (matchPK != null ? matchPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Match)) {
            return false;
        }
        Match other = (Match) object;
        if ((this.matchPK == null && other.matchPK != null) || (this.matchPK != null && !this.matchPK.equals(other.matchPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.database.storage.db.Match[ matchPK=" + matchPK + " ]";
    }

    @XmlTransient
    public List<MatchHasTeam> getMatchHasTeamList() {
        return matchHasTeamList;
    }

    public void setMatchHasTeamList(List<MatchHasTeam> matchHasTeamList) {
        this.matchHasTeamList = matchHasTeamList;
    }
}
