package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class TournamentHasTeamPK implements Serializable
{
  private static final long serialVersionUID = 2563324495027629204L;
  @Basic(optional = false)
  @NotNull
  @Column(name = "tournament_id")
  private int tournamentId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "team_id")
  private int teamId;

  public TournamentHasTeamPK()
  {
  }

  public TournamentHasTeamPK(int tournamentId, int teamId)
  {
    this.tournamentId = tournamentId;
    this.teamId = teamId;
  }

  public int getTournamentId()
  {
    return tournamentId;
  }

  public void setTournamentId(int tournamentId)
  {
    this.tournamentId = tournamentId;
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
    hash += (int) tournamentId;
    hash += (int) teamId;
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof TournamentHasTeamPK))
    {
      return false;
    }
    TournamentHasTeamPK other = (TournamentHasTeamPK) object;
    if (this.tournamentId != other.tournamentId)
    {
      return false;
    }
    return this.teamId == other.teamId;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.TournamentHasTeamPK[ tournamentId="
            + tournamentId + ", teamId=" + teamId + " ]";
  }
}
