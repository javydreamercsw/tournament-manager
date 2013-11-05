/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
@Table(name = "record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Record.findAll", query = "SELECT r FROM Record r"),
    @NamedQuery(name = "Record.findById", query = "SELECT r FROM Record r WHERE r.id = :id"),
    @NamedQuery(name = "Record.findByWins", query = "SELECT r FROM Record r WHERE r.wins = :wins"),
    @NamedQuery(name = "Record.findByLoses", query = "SELECT r FROM Record r WHERE r.loses = :loses"),
    @NamedQuery(name = "Record.findByDraws", query = "SELECT r FROM Record r WHERE r.draws = :draws")})
public class Record implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "wins")
    private int wins;
    @Basic(optional = false)
    @Column(name = "loses")
    private int loses;
    @Basic(optional = false)
    @Column(name = "draws")
    private int draws;
    @ManyToMany(mappedBy = "recordList")
    private List<TournamentHasTeam> tournamentHasTeamList;
    @ManyToMany(mappedBy = "recordList")
    private List<Player> playerList;

    public Record() {
    }

    public Record(Integer id) {
        this.id = id;
    }

    public Record(Integer id, int wins, int loses, int draws) {
        this.id = id;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    @XmlTransient
    public List<TournamentHasTeam> getTournamentHasTeamList() {
        return tournamentHasTeamList;
    }

    public void setTournamentHasTeamList(List<TournamentHasTeam> tournamentHasTeamList) {
        this.tournamentHasTeamList = tournamentHasTeamList;
    }

    @XmlTransient
    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Record)) {
            return false;
        }
        Record other = (Record) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.database.storage.db.Record[ id=" + id + " ]";
    }

}
