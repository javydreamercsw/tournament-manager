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
@Table(name = "round")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Round.findAll", query = "SELECT r FROM Round r"),
    @NamedQuery(name = "Round.findById", query = "SELECT r FROM Round r WHERE r.roundPK.id = :id"),
    @NamedQuery(name = "Round.findByTournamentId", query = "SELECT r FROM Round r WHERE r.roundPK.tournamentId = :tournamentId")})
public class Round implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RoundPK roundPK;
    @JoinColumn(name = "tournament_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Tournament tournament;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "round")
    private List<Match> matchList;

    public Round() {
    }

    public Round(RoundPK roundPK) {
        this.roundPK = roundPK;
    }

    public Round(int tournamentId) {
        this.roundPK = new RoundPK(tournamentId);
    }

    public RoundPK getRoundPK() {
        return roundPK;
    }

    public void setRoundPK(RoundPK roundPK) {
        this.roundPK = roundPK;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    @XmlTransient
    public List<Match> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<Match> matchList) {
        this.matchList = matchList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roundPK != null ? roundPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Round)) {
            return false;
        }
        Round other = (Round) object;
        if ((this.roundPK == null && other.roundPK != null) || (this.roundPK != null && !this.roundPK.equals(other.roundPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.database.storage.db.Round[ roundPK=" + roundPK + " ]";
    }

}