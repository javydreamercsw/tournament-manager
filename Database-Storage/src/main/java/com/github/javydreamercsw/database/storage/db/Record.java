package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
  @NamedQuery(name = "Record.findById", 
          query = "SELECT r FROM Record r WHERE r.recordPK.id = :id"),
  @NamedQuery(name = "Record.findByWins", 
          query = "SELECT r FROM Record r WHERE r.wins = :wins"),
  @NamedQuery(name = "Record.findByLoses", 
          query = "SELECT r FROM Record r WHERE r.loses = :loses"),
  @NamedQuery(name = "Record.findByDraws", 
          query = "SELECT r FROM Record r WHERE r.draws = :draws"),
  @NamedQuery(name = "Record.findByGameId", 
          query = "SELECT r FROM Record r WHERE r.recordPK.gameId = :gameId")
})
public class Record implements Serializable
{
  private static final long serialVersionUID = -893880954416960217L;
  @EmbeddedId
  protected RecordPK recordPK;
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
  @ManyToMany(mappedBy = "recordList")
  private List<Player> playerList;
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
    playerList = new ArrayList<>();
  }

  public Record(int gameId)
  {
    this.recordPK = new RecordPK(gameId);
  }

  public RecordPK getRecordPK()
  {
    return recordPK;
  }

  public void setRecordPK(RecordPK recordPK)
  {
    this.recordPK = recordPK;
  }


  public Game getGame()
  {
    return game;
  }

  public void setGame(Game game)
  {
    this.game = game;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (recordPK != null ? recordPK.hashCode() : 0);
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
    return !((this.recordPK == null && other.recordPK != null) 
            || (this.recordPK != null && !this.recordPK.equals(other.recordPK)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.Record[ recordPK=" 
            + recordPK + " ]";
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
  public List<Player> getPlayerList()
  {
    return playerList;
  }

  public void setPlayerList(List<Player> playerList)
  {
    this.playerList = playerList;
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
  
}
