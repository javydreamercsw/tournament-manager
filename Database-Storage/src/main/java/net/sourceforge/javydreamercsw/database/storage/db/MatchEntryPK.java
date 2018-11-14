package net.sourceforge.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
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
  public int hashCode()
  {
    int hash = 0;
    hash += (int) id;
    hash += (int) roundId;
    hash += (int) formatId;
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
    if (this.roundId != other.roundId)
    {
      return false;
    }
    if (this.formatId != other.formatId)
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "net.sourceforge.javydreamercsw.database.storage.db.MatchEntryPK[ id=" + id + ", roundId=" + roundId + ", formatId=" + formatId + " ]";
  }

}