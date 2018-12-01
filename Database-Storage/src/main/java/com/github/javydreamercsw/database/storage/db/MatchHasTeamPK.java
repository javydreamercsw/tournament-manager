package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class MatchHasTeamPK implements Serializable
{
  private static final long serialVersionUID = 7694489413464173357L;
  @Basic(optional = false)
  @NotNull
  @Column(name = "team_id")
  private int teamId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "match_entry_id")
  private int matchEntryId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "match_entry_format_id")
  private int matchEntryFormatId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "match_entry_format_game_id")
  private int matchEntryFormatGameId;

  public MatchHasTeamPK()
  {
  }

  public MatchHasTeamPK(int teamId, int matchEntryId, int matchEntryFormatId, 
          int matchEntryFormatGameId)
  {
    this.teamId = teamId;
    this.matchEntryId = matchEntryId;
    this.matchEntryFormatId = matchEntryFormatId;
    this.matchEntryFormatGameId = matchEntryFormatGameId;
  }

  public int getTeamId()
  {
    return teamId;
  }

  public void setTeamId(int teamId)
  {
    this.teamId = teamId;
  }

  public int getMatchEntryId()
  {
    return matchEntryId;
  }

  public void setMatchEntryId(int matchEntryId)
  {
    this.matchEntryId = matchEntryId;
  }

  public int getMatchEntryFormatId()
  {
    return matchEntryFormatId;
  }

  public void setMatchEntryFormatId(int matchEntryFormatId)
  {
    this.matchEntryFormatId = matchEntryFormatId;
  }

  public int getMatchEntryFormatGameId()
  {
    return matchEntryFormatGameId;
  }

  public void setMatchEntryFormatGameId(int matchEntryFormatGameId)
  {
    this.matchEntryFormatGameId = matchEntryFormatGameId;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (int) teamId;
    hash += (int) matchEntryId;
    hash += (int) matchEntryFormatId;
    hash += (int) matchEntryFormatGameId;
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof MatchHasTeamPK))
    {
      return false;
    }
    MatchHasTeamPK other = (MatchHasTeamPK) object;
    if (this.teamId != other.teamId)
    {
      return false;
    }
    if (this.matchEntryId != other.matchEntryId)
    {
      return false;
    }
    if (this.matchEntryFormatId != other.matchEntryFormatId)
    {
      return false;
    }
    return this.matchEntryFormatGameId == other.matchEntryFormatGameId;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.MatchHasTeamPK[ teamId=" 
            + teamId + ", matchEntryId=" + matchEntryId + ", matchEntryFormatId="
            + matchEntryFormatId + ", matchEntryFormatGameId="
            + matchEntryFormatGameId + " ]";
  }
}
