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
public class MatchResultPK implements Serializable
{
  private static final long serialVersionUID = -4935072391952815681L;
  @Basic(optional = false)
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "MatchResultGen")
  @TableGenerator(name = "MatchResultGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "match_result",
          allocationSize = 1,
          initialValue = 1)
  private int id;
  @Basic(optional = false)
  @NotNull
  @Column(name = "match_result_type_id")
  private int matchResultTypeId;

  public MatchResultPK()
  {
  }

  public MatchResultPK(int matchResultTypeId)
  {
    this.matchResultTypeId = matchResultTypeId;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public int getMatchResultTypeId()
  {
    return matchResultTypeId;
  }

  public void setMatchResultTypeId(int matchResultTypeId)
  {
    this.matchResultTypeId = matchResultTypeId;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (int) id;
    hash += (int) matchResultTypeId;
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof MatchResultPK))
    {
      return false;
    }
    MatchResultPK other = (MatchResultPK) object;
    if (this.id != other.id)
    {
      return false;
    }
    return this.matchResultTypeId == other.matchResultTypeId;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.MatchResultPK[ id="
            + id + ", matchResultTypeId=" + matchResultTypeId + " ]";
  }
}
