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
@Table(name = "tournament")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tournament.findAll", query = "SELECT t FROM Tournament t"),
    @NamedQuery(name = "Tournament.findById", query = "SELECT t FROM Tournament t WHERE t.id = :id"),
    @NamedQuery(name = "Tournament.findByName", query = "SELECT t FROM Tournament t WHERE t.name = :name")})
public class Tournament implements Serializable {

    @Basic(optional = false)
    @Column(name = "winPoints")
    private int winPoints;
    @Basic(optional = false)
    @Column(name = "drawPoints")
    private int drawPoints;
    @Basic(optional = false)
    @Column(name = "lossPoints")
    private int lossPoints;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TournamentGen")
    @TableGenerator(name = "TournamentGen", table = "tm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "tournament",
            allocationSize = 1,
            initialValue = 1)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournament")
    private List<Round> roundList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournament")
    private List<TournamentHasTeam> tournamentHasTeamList;

    public Tournament() {
    }

    public Tournament(String name) {
        this.name = name;
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
    public List<Round> getRoundList() {
        return roundList;
    }

    public void setRoundList(List<Round> roundList) {
        this.roundList = roundList;
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
        if (!(object instanceof Tournament)) {
            return false;
        }
        Tournament other = (Tournament) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.database.storage.db.Tournament[ id=" + id + " ]";
    }

    public int getWinPoints() {
        return winPoints;
    }

    public void setWinPoints(int winPoints) {
        this.winPoints = winPoints;
    }

    public int getDrawPoints() {
        return drawPoints;
    }

    public void setDrawPoints(int drawPoints) {
        this.drawPoints = drawPoints;
    }

    public int getLossPoints() {
        return lossPoints;
    }

    public void setLossPoints(int lossPoints) {
        this.lossPoints = lossPoints;
    }

}
