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
public class TournamentPK implements Serializable
{
  private static final long serialVersionUID = -5852922586504031795L;
  @Basic(optional = false)
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "TournamentGen")
  @TableGenerator(name = "TournamentGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "tournament",
          allocationSize = 1,
          initialValue = 1)
  private int id;
  @Basic(optional = false)
  @NotNull
  @Column(name = "tournament_format_id")
  private int tournamentFormatId;

  public TournamentPK()
  {

  }

  public TournamentPK(int tournamentFormatId)
  {
    this.tournamentFormatId = tournamentFormatId;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public int getTournamentFormatId()
  {
    return tournamentFormatId;
  }

  public void setTournamentFormatId(int tournamentFormatId)
  {
    this.tournamentFormatId = tournamentFormatId;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (int) id;
    hash += (int) tournamentFormatId;
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof TournamentPK))
    {
      return false;
    }
    TournamentPK other = (TournamentPK) object;
    if (this.id != other.id)
    {
      return false;
    }
    return this.tournamentFormatId == other.tournamentFormatId;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.TournamentPK[ id="
            + id + ", tournamentFormatId=" + tournamentFormatId + " ]";
  }
}
