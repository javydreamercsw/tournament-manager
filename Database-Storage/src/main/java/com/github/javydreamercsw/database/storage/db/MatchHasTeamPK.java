package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MatchHasTeamPK implements Serializable
{
  private static final long serialVersionUID = 1692252424439773610L;
  @Basic(optional = false)
  @Column(name = "match_id")
  private int matchId;
  @Basic(optional = false)
  @Column(name = "team_id")
  private int teamId;
  @Basic(optional = false)
  @Column(name = "match_result_id")
  private int matchResultId;
  @Basic(optional = false)
  @Column(name = "match_result_match_result_type_id")
  private int matchResultMatchResultTypeId;

  public MatchHasTeamPK()
  {
  }

  public MatchHasTeamPK(int matchId, int teamId, int matchResultId, int matchResultMatchResultTypeId)
  {
    this.matchId = matchId;
    this.teamId = teamId;
    this.matchResultId = matchResultId;
    this.matchResultMatchResultTypeId = matchResultMatchResultTypeId;
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

  public int getMatchResultId()
  {
    return matchResultId;
  }

  public void setMatchResultId(int matchResultId)
  {
    this.matchResultId = matchResultId;
  }

  public int getMatchResultMatchResultTypeId()
  {
    return matchResultMatchResultTypeId;
  }

  public void setMatchResultMatchResultTypeId(int matchResultMatchResultTypeId)
  {
    this.matchResultMatchResultTypeId = matchResultMatchResultTypeId;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (int) matchId;
    hash += (int) teamId;
    hash += (int) matchResultId;
    hash += (int) matchResultMatchResultTypeId;
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
    if (this.matchResultId != other.matchResultId)
    {
      return false;
    }
    return this.matchResultMatchResultTypeId == other.matchResultMatchResultTypeId;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.MatchHasTeamPK[ matchId="
            + matchId + ", teamId=" + teamId + ", matchResultId=" + matchResultId
            + ", matchResultMatchResultTypeId=" + matchResultMatchResultTypeId + " ]";
  }

}
