package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class MatchHasTeamPK implements Serializable
{
  private static final long serialVersionUID = 6311823088197369673L;
  @Basic(optional = false)
  @NotNull
  @Column(name = "match_id")
  private int matchId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "match_format_id")
  private int formatId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "match_game_id")
  private int gameId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "team_id")
  private int teamId;
  @Basic(optional = true)
  @Column(name = "match_result_id")
  private int matchResultId;
  @Basic(optional = true)
  @Column(name = "match_result_match_result_type_id")
  private int matchResultMatchResultTypeId;

  public MatchHasTeamPK()
  {
  }

  public MatchHasTeamPK(int matchId, int formatId, int gameId, int teamId)
  {
    this.matchId = matchId;
    this.formatId = formatId;
    this.gameId = gameId;
    this.teamId = teamId;
  }

  public int getMatchId()
  {
    return matchId;
  }

  public void setMatchId(int matchId)
  {
    this.matchId = matchId;
  }

  public int getTeamId()
  {
    return teamId;
  }

  public void setTeamId(int teamId)
  {
    this.teamId = teamId;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (int) matchId;
    hash += (int) teamId;
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
    if (this.matchId != other.matchId)
    {
      return false;
    }
    if (this.teamId != other.teamId)
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.MatchHasTeamPK[ matchId=" + matchId + ", teamId=" + teamId + " ]";
  }

  /**
   * @return the formatId
   */
  public int getFormatId()
  {
    return formatId;
  }

  /**
   * @param formatId the formatId to set
   */
  public void setFormatId(int formatId)
  {
    this.formatId = formatId;
  }

  /**
   * @return the gameId
   */
  public int getGameId()
  {
    return gameId;
  }

  /**
   * @param gameId the gameId to set
   */
  public void setGameId(int gameId)
  {
    this.gameId = gameId;
  }

  /**
   * @return the matchResultId
   */
  public int getMatchResultId()
  {
    return matchResultId;
  }

  /**
   * @param matchResultId the matchResultId to set
   */
  public void setMatchResultId(int matchResultId)
  {
    this.matchResultId = matchResultId;
  }

  /**
   * @return the matchResultMatchResultTypeId
   */
  public int getMatchResultMatchResultTypeId()
  {
    return matchResultMatchResultTypeId;
  }

  /**
   * @param matchResultMatchResultTypeId the matchResultMatchResultTypeId to set
   */
  public void setMatchResultMatchResultTypeId(int matchResultMatchResultTypeId)
  {
    this.matchResultMatchResultTypeId = matchResultMatchResultTypeId;
  }

}
