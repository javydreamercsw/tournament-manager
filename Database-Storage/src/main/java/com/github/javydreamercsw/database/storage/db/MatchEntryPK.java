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
public class MatchEntryPK implements Serializable
{
  private static final long serialVersionUID = 5122243953512635878L;
  @Basic(optional = false)
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "MatchEntryGen")
  @TableGenerator(name = "MatchEntryGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "match_entry",
          allocationSize = 1,
          initialValue = 1)
  private int id;
  @Basic(optional = false)
  @NotNull
  @Column(name = "format_id")
  private int formatId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "format_game_id")
  private int formatGameId;

  public MatchEntryPK()
  {
  }

  public MatchEntryPK(int formatId, int formatGameId)
  {
    this.formatId = formatId;
    this.formatGameId = formatGameId;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public int getFormatId()
  {
    return formatId;
  }

  public void setFormatId(int formatId)
  {
    this.formatId = formatId;
  }

  public int getFormatGameId()
  {
    return formatGameId;
  }

  public void setFormatGameId(int formatGameId)
  {
    this.formatGameId = formatGameId;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (int) id;
    hash += (int) formatId;
    hash += (int) formatGameId;
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof MatchEntryPK))
    {
      return false;
    }
    MatchEntryPK other = (MatchEntryPK) object;
    if (this.id != other.id)
    {
      return false;
    }
    if (this.formatId != other.formatId)
    {
      return false;
    }
    return this.formatGameId == other.formatGameId;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.MatchEntryPK[ id="
            + id + ", formatId=" + formatId + ", formatGameId=" + formatGameId 
            + " ]";
  }
}
