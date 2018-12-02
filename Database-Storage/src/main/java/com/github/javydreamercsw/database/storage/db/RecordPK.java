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
public class RecordPK implements Serializable
{
  private static final long serialVersionUID = 2778418123591111690L;
  @Basic(optional = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "RecordGen")
  @TableGenerator(name = "RecordGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "record",
          allocationSize = 1,
          initialValue = 1)
  @Column(name = "id")
  private int id;
  @Basic(optional = false)
  @NotNull
  @Column(name = "game_id")
  private int gameId;

  public RecordPK()
  {
  }

  public RecordPK(int gameId)
  {
    this.gameId = gameId;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public int getGameId()
  {
    return gameId;
  }

  public void setGameId(int gameId)
  {
    this.gameId = gameId;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (int) id;
    hash += (int) gameId;
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof RecordPK))
    {
      return false;
    }
    RecordPK other = (RecordPK) object;
    if (this.id != other.id)
    {
      return false;
    }
    return this.gameId == other.gameId;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.RecordPK[ id=" + id 
            + ", gameId=" + gameId + " ]";
  }
}
