/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class MatchPK implements Serializable
{

  @Basic(optional = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "MatchGen")
  @TableGenerator(name = "MatchGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "match",
          allocationSize = 1,
          initialValue = 1)
  @Column(name = "id")
  private int id;
  @Basic(optional = false)
  @Column(name = "round_id")
  private int roundId;

  public MatchPK()
  {
  }

  public MatchPK(int roundId)
  {
    this.roundId = roundId;
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

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (int) id;
    hash += (int) roundId;
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof MatchPK))
    {
      return false;
    }
    MatchPK other = (MatchPK) object;
    if (this.id != other.id)
    {
      return false;
    }
    return this.roundId == other.roundId;
  }

  @Override
  public String toString()
  {
    return "net.sourceforge.javydreamercsw.database.storage.db.MatchPK[ id="
            + id + ", roundId=" + roundId + " ]";
  }
}
