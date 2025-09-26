package com.github.javydreamercsw.database.storage.db;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Team.findAll", query = "SELECT t FROM Team t"),
  @NamedQuery(name = "Team.findById", query = "SELECT t FROM Team t WHERE t.id = :id"),
  @NamedQuery(name = "Team.findByName", query = "SELECT t FROM Team t WHERE t.name = :name")
})
public class Team implements Serializable {
  @Size(max = 245) @Column(name = "name")
  private String name;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
  private List<TeamHasFormatRecord> teamHasFormatRecordList;

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "TeamGen")
  @TableGenerator(
      name = "TeamGen",
      table = "tm_id",
      pkColumnName = "table_name",
      valueColumnName = "last_id",
      pkColumnValue = "team",
      allocationSize = 1,
      initialValue = 1)
  @Column(name = "id")
  private Integer id;

  @ManyToMany(mappedBy = "teamList")
  private List<Player> playerList;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
  private List<MatchHasTeam> matchHasTeamList;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
  private List<TournamentHasTeam> tournamentHasTeamList;

  public Team() {
    matchHasTeamList = new ArrayList<>();
    playerList = new ArrayList<>();
    tournamentHasTeamList = new ArrayList<>();
    teamHasFormatRecordList = new ArrayList<>();
  }

  public Team(String name) {
    this();
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @XmlTransient
  public List<Player> getPlayerList() {
    return playerList;
  }

  public void setPlayerList(List<Player> playerList) {
    this.playerList = playerList;
  }

  @XmlTransient
  public List<MatchHasTeam> getMatchHasTeamList() {
    return matchHasTeamList;
  }

  public void setMatchHasTeamList(List<MatchHasTeam> matchHasTeamList) {
    this.matchHasTeamList = matchHasTeamList;
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
    return !((this.id == null && other.id != null)
        || (this.id != null && !this.id.equals(other.id)));
  }

  @Override
  public String toString() {
    return "com.github.javydreamercsw.database.storage.db.Team[ id=" + id + " ]";
  }

  @XmlTransient
  public List<TeamHasFormatRecord> getTeamHasFormatRecordList() {
    return teamHasFormatRecordList;
  }

  public void setTeamHasFormatRecordList(List<TeamHasFormatRecord> teamHasFormatRecordList) {
    this.teamHasFormatRecordList = teamHasFormatRecordList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
