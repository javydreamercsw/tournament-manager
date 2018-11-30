package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

@Embeddable
public class RoundPK implements Serializable
{
  private static final long serialVersionUID = -8356585559628974067L;
  @Basic(optional = false)
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "RoundGen")
  @TableGenerator(name = "RoundGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "round",
          allocationSize = 1,
          initialValue = 1)
  private int id;
  @Basic(optional = false)
  @NotNull
  @Column(name = "tournament_id")
  private int tournamentId;

  public RoundPK()
  {
  }

  public RoundPK(int tournamentId)
  {
    this.tournamentId = tournamentId;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public int getTournamentId()
  {
    return tournamentId;
  }

  public void setTournamentId(int tournamentId)
  {
    this.tournamentId = tournamentId;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (int) id;
    hash += (int) tournamentId;
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof RoundPK))
    {
      return false;
    }
    RoundPK other = (RoundPK) object;
    if (this.id != other.id)
    {
      return false;
    }
    return this.tournamentId == other.tournamentId;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.RoundPK[ id=" + id
            + ", tournamentId=" + tournamentId + " ]";
  }
}
