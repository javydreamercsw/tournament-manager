package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "record")
@XmlRootElement
@NamedQueries(
{
  @NamedQuery(name = "Record.findAll", query = "SELECT r FROM Record r"),
  @NamedQuery(name = "Record.findByWins", 
          query = "SELECT r FROM Record r WHERE r.wins = :wins"),
  @NamedQuery(name = "Record.findByLoses", 
          query = "SELECT r FROM Record r WHERE r.loses = :loses"),
  @NamedQuery(name = "Record.findByDraws", 
          query = "SELECT r FROM Record r WHERE r.draws = :draws"),
})
public class Record implements Serializable
{
  private static final long serialVersionUID = -587143815558105193L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Basic(optional = false)
  @NotNull
  @Column(name = "wins")
  private int wins;
  @Basic(optional = false)
  @NotNull
  @Column(name = "loses")
  private int loses;
  @Basic(optional = false)
  @NotNull
  @Column(name = "draws")
  private int draws;
  @JoinTable(name = "team_has_record", joinColumns =
  {
    @JoinColumn(name = "record_id", referencedColumnName = "id")
  }, inverseJoinColumns =
  {
    @JoinColumn(name = "team_id", referencedColumnName = "id")
  })
  @ManyToMany
  private List<Team> teamList;
  @JoinTable(name = "tournament_has_team_has_record", joinColumns =
  {
    @JoinColumn(name = "record_id", referencedColumnName = "id"),
    @JoinColumn(name = "record_game_id", referencedColumnName = "game_id")
  }, inverseJoinColumns =
  {
    @JoinColumn(name = "tournament_has_team_tournament_id", 
            referencedColumnName = "tournament_id"),
    @JoinColumn(name = "tournament_has_team_team_id", 
            referencedColumnName = "team_id")
  })
  @ManyToMany
  private List<TournamentHasTeam> tournamentHasTeamList;
  @JoinColumn(name = "game_id", referencedColumnName = "id", insertable = false, 
          updatable = false)
  @ManyToOne(optional = false)
  private Game game;

  public Record()
  {
    this(0, 0, 0);
  }

  public Record(int wins, int loses, int draws)
  {
    this.wins = wins;
    this.loses = loses;
    this.draws = draws;
    tournamentHasTeamList = new ArrayList<>();
    teamList = new ArrayList<>();
  }

  public Game getGame()
  {
    return game;
  }

  public void setGame(Game game)
  {
    this.game = game;
  }

  @XmlTransient
  public List<TournamentHasTeam> getTournamentHasTeamList()
  {
    return tournamentHasTeamList;
  }

  public void setTournamentHasTeamList(List<TournamentHasTeam> tournamentHasTeamList)
  {
    this.tournamentHasTeamList = tournamentHasTeamList;
  }

  public Record(Integer id)
  {
    this.id = id;
  }

  public Record(Integer id, int wins, int loses, int draws)
  {
    this.id = id;
    this.wins = wins;
    this.loses = loses;
    this.draws = draws;
  }

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public int getWins()
  {
    return wins;
  }

  public void setWins(int wins)
  {
    this.wins = wins;
  }

  public int getLoses()
  {
    return loses;
  }

  public void setLoses(int loses)
  {
    this.loses = loses;
  }

  public int getDraws()
  {
    return draws;
  }

  public void setDraws(int draws)
  {
    this.draws = draws;
  }

  @XmlTransient
  public List<Team> getTeamList()
  {
    return teamList;
  }

  public void setTeamList(List<Team> teamList)
  {
    this.teamList = teamList;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Record))
    {
      return false;
    }
    Record other = (Record) object;
    return !((this.id == null && other.id != null) || (this.id != null 
            && !this.id.equals(other.id)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.Record[ id=" + id + " ]";
  }
}
