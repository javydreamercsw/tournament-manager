/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "team")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Team.findAll", query = "SELECT t FROM Team t"),
    @NamedQuery(name = "Team.findById", query = "SELECT t FROM Team t WHERE t.id = :id"),
    @NamedQuery(name = "Team.findByName", query = "SELECT t FROM Team t WHERE t.name = :name")})
public class Team implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
    private List<MatchHasTeam> matchHasTeamList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TeamGen")
    @TableGenerator(name = "TeamGen", table = "tm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "team",
            allocationSize = 1,
            initialValue = 1)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @JoinTable(name = "team_has_player", joinColumns = {
        @JoinColumn(name = "team_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "player_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Player> playerList;
    @ManyToMany(mappedBy = "teamList")
    private List<Match> matchList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
    private List<TournamentHasTeam> tournamentHasTeamList;

    public Team() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    @XmlTransient
    public List<Match> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<Match> matchList) {
        this.matchList = matchList;
    }

    @XmlTransient
    public List<TournamentHasTeam> getTournamentHasTeamList() {
        return tournamentHasTeamList;
    }

    public void setTournamentHasTeamList(List<TournamentHasTeam> tournamentHasTeamList) {
        this.tournamentHasTeamList = tournamentHasTeamList;
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
        if (!(object instanceof Team)) {
            return false;
        }
        Team other = (Team) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.database.storage.db.Team[ id=" + id + " ]";
    }

    @XmlTransient
    public List<MatchHasTeam> getMatchHasTeamList() {
        return matchHasTeamList;
    }

    public void setMatchHasTeamList(List<MatchHasTeam> matchHasTeamList) {
        this.matchHasTeamList = matchHasTeamList;
    }

}
