package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
@Embeddable
public class TeamHasFormatRecordPK implements Serializable
{
  private static final long serialVersionUID = 11371362902792432L;
  @Basic(optional = false)
  @NotNull
  @Column(name = "team_id")
  private int teamId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "format_id")
  private int formatId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "format_game_id")
  private int formatGameId;

  public TeamHasFormatRecordPK()
  {
  }

  public TeamHasFormatRecordPK(int teamId, int formatId, int formatGameId)
  {
    this.teamId = teamId;
    this.formatId = formatId;
    this.formatGameId = formatGameId;
  }

  public int getTeamId()
  {
    return teamId;
  }

  public void setTeamId(int teamId)
  {
    this.teamId = teamId;
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
    hash += (int) teamId;
    hash += (int) formatId;
    hash += (int) formatGameId;
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof TeamHasFormatRecordPK))
    {
      return false;
    }
    TeamHasFormatRecordPK other = (TeamHasFormatRecordPK) object;
    if (this.teamId != other.teamId)
    {
      return false;
    }
    if (this.formatId != other.formatId)
    {
      return false;
    }
    if (this.formatGameId != other.formatGameId)
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.TeamHasFormatRecordPK[ teamId=" + teamId + ", formatId=" + formatId + ", formatGameId=" + formatGameId + " ]";
  }
  
}
