package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "match_has_team")
@XmlRootElement
@NamedQueries(
{
  @NamedQuery(name = "MatchHasTeam.findAll", query = "SELECT m FROM MatchHasTeam m"),
  @NamedQuery(name = "MatchHasTeam.findByTeamId", 
          query = "SELECT m FROM MatchHasTeam m WHERE m.matchHasTeamPK.teamId = :teamId"),
  @NamedQuery(name = "MatchHasTeam.findByMatchEntryId", 
          query = "SELECT m FROM MatchHasTeam m WHERE m.matchHasTeamPK.matchEntryId = :matchEntryId"),
  @NamedQuery(name = "MatchHasTeam.findByMatchEntryFormatId", 
          query = "SELECT m FROM MatchHasTeam m WHERE m.matchHasTeamPK.matchEntryFormatId = :matchEntryFormatId"),
  @NamedQuery(name = "MatchHasTeam.findByMatchEntryFormatGameId", 
          query = "SELECT m FROM MatchHasTeam m WHERE m.matchHasTeamPK.matchEntryFormatGameId = :matchEntryFormatGameId")
})
public class MatchHasTeam implements Serializable
{
  private static final long serialVersionUID = 1L;
  @EmbeddedId
  protected MatchHasTeamPK matchHasTeamPK;
  @JoinColumns(
  {
    @JoinColumn(name = "match_entry_id", referencedColumnName = "id", 
            insertable = false, updatable = false),
    @JoinColumn(name = "match_entry_format_id", 
            referencedColumnName = "format_id", insertable = false, 
            updatable = false),
    @JoinColumn(name = "match_entry_format_game_id", 
            referencedColumnName = "format_game_id", insertable = false, 
            updatable = false)
  })
  @ManyToOne(optional = false)
  private MatchEntry matchEntry;
  @JoinColumns(
  {
    @JoinColumn(name = "match_result_id", referencedColumnName = "id"),
    @JoinColumn(name = "match_result_match_result_type_id", 
            referencedColumnName = "match_result_type_id")
  })
  @ManyToOne
  private MatchResult matchResult;
  @JoinColumn(name = "team_id", referencedColumnName = "id", insertable = false, 
          updatable = false)
  @ManyToOne(optional = false)
  private Team team;

  public MatchHasTeam()
  {
  }

  public MatchHasTeam(MatchHasTeamPK matchHasTeamPK)
  {
    this.matchHasTeamPK = matchHasTeamPK;
  }

  public MatchHasTeam(int teamId, int matchEntryId, int matchEntryFormatId, int matchEntryFormatGameId)
  {
    this.matchHasTeamPK = new MatchHasTeamPK(teamId, matchEntryId, matchEntryFormatId, matchEntryFormatGameId);
  }

  public MatchHasTeamPK getMatchHasTeamPK()
  {
    return matchHasTeamPK;
  }

  public void setMatchHasTeamPK(MatchHasTeamPK matchHasTeamPK)
  {
    this.matchHasTeamPK = matchHasTeamPK;
  }

  public MatchEntry getMatchEntry()
  {
    return matchEntry;
  }

  public void setMatchEntry(MatchEntry matchEntry)
  {
    this.matchEntry = matchEntry;
  }

  public MatchResult getMatchResult()
  {
    return matchResult;
  }

  public void setMatchResult(MatchResult matchResult)
  {
    this.matchResult = matchResult;
  }

  public Team getTeam()
  {
    return team;
  }

  public void setTeam(Team team)
  {
    this.team = team;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (matchHasTeamPK != null ? matchHasTeamPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof MatchHasTeam))
    {
      return false;
    }
    MatchHasTeam other = (MatchHasTeam) object;
    return !((this.matchHasTeamPK == null && other.matchHasTeamPK != null) 
            || (this.matchHasTeamPK != null 
            && !this.matchHasTeamPK.equals(other.matchHasTeamPK)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.MatchHasTeam[ matchHasTeamPK=" 
            + matchHasTeamPK + " ]";
  }
}
