package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;

@Embeddable
public class MatchEntryPK implements Serializable
{
  private static final long serialVersionUID = 299333646360169920L;
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
  @Column(name = "round_id")
  private int roundId;
  @Basic(optional = false)
  @Column(name = "format_id")
  private int formatId;

  public MatchEntryPK()
  {
  }

  public MatchEntryPK(int roundId, int formatId)
  {
    this.roundId = roundId;
    this.formatId = formatId;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public int getRoundId()
  {
    return roundId;
  }

  public void setRoundId(int roundId)
  {
    this.roundId = roundId;
  }

  public int getFormatId()
  {
    return formatId;
  }

  public void setFormatId(int formatId)
  {
    this.formatId = formatId;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    if (!Objects.equals(getClass(), obj.getClass()))
    {
      return false;
    }
    final MatchEntryPK other = (MatchEntryPK) obj;
    if (this.getId() != other.getId())
    {
      return false;
    }
    if (this.getRoundId() != other.getRoundId())
    {
      return false;
    }
    return this.getFormatId() == other.getFormatId();
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 31 * hash + this.getId();
    hash = 31 * hash + this.getRoundId();
    hash = 31 * hash + this.getFormatId();
    return hash;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.MatchEntryPK[ id=" + id 
            + ", roundId=" + roundId + ", formatId=" + formatId + " ]";
  }
}