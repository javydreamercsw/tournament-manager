package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "team_has_format_record")
@XmlRootElement
@NamedQueries(
        {
          @NamedQuery(name = "TeamHasFormatRecord.findAll",
                  query = "SELECT t FROM TeamHasFormatRecord t"),
          @NamedQuery(name = "TeamHasFormatRecord.findByTeamId",
                  query = "SELECT t FROM TeamHasFormatRecord t WHERE t.teamHasFormatRecordPK.teamId = :teamId"),
          @NamedQuery(name = "TeamHasFormatRecord.findByFormatId",
                  query = "SELECT t FROM TeamHasFormatRecord t WHERE t.teamHasFormatRecordPK.formatId = :formatId"),
          @NamedQuery(name = "TeamHasFormatRecord.findByFormatGameId",
                  query = "SELECT t FROM TeamHasFormatRecord t WHERE t.teamHasFormatRecordPK.formatGameId = :formatGameId"),
          @NamedQuery(name = "TeamHasFormatRecord.findByMean",
                  query = "SELECT t FROM TeamHasFormatRecord t WHERE t.mean = :mean"),
          @NamedQuery(name = "TeamHasFormatRecord.findByStandardDeviation",
                  query = "SELECT t FROM TeamHasFormatRecord t WHERE t.standardDeviation = :standardDeviation")
        })
public class TeamHasFormatRecord implements Serializable
{
  private static final long serialVersionUID = 1517758449111184848L;
  @Basic(optional = false)
  @NotNull
  @Column(name = "mean")
  private double mean;
  @Basic(optional = false)
  @NotNull
  @Column(name = "standard_deviation")
  private double standardDeviation;
  @Basic(optional = false)
  @NotNull
  @Column(name = "points")
  private int points;
  @EmbeddedId
  protected TeamHasFormatRecordPK teamHasFormatRecordPK;
  @JoinColumns(
          {
            @JoinColumn(name = "format_id", referencedColumnName = "id",
                    insertable = false, updatable = false),
            @JoinColumn(name = "format_game_id", referencedColumnName = "game_id",
                    insertable = false, updatable = false)
          })
  @ManyToOne(optional = false)
  private Format format;
  @JoinColumn(name = "team_id", referencedColumnName = "id",
          insertable = false, updatable = false)
  @ManyToOne(optional = false)
  private Team team;

  public TeamHasFormatRecord()
  {
  }

  public TeamHasFormatRecord(TeamHasFormatRecordPK teamHasFormatRecordPK)
  {
    this.teamHasFormatRecordPK = teamHasFormatRecordPK;
  }

  public TeamHasFormatRecord(TeamHasFormatRecordPK teamHasFormatRecordPK,
          double mean, double standardDeviation)
  {
    this.teamHasFormatRecordPK = teamHasFormatRecordPK;
    this.mean = mean;
    this.standardDeviation = standardDeviation;
  }

  public TeamHasFormatRecord(int teamId, int formatId, int formatGameId)
  {
    this.teamHasFormatRecordPK = new TeamHasFormatRecordPK(teamId, formatId, formatGameId);
  }

  public TeamHasFormatRecordPK getTeamHasFormatRecordPK()
  {
    return teamHasFormatRecordPK;
  }

  public void setTeamHasFormatRecordPK(TeamHasFormatRecordPK teamHasFormatRecordPK)
  {
    this.teamHasFormatRecordPK = teamHasFormatRecordPK;
  }

  public Format getFormat()
  {
    return format;
  }

  public void setFormat(Format format)
  {
    this.format = format;
  }

  public Team getTeam()
  {
    return team;
  }

  public void setTeam(Team team)
  {
    this.team = team;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (teamHasFormatRecordPK != null ? teamHasFormatRecordPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof TeamHasFormatRecord))
    {
      return false;
    }
    TeamHasFormatRecord other = (TeamHasFormatRecord) object;
    return !((this.teamHasFormatRecordPK == null
            && other.teamHasFormatRecordPK != null)
            || (this.teamHasFormatRecordPK != null
            && !this.teamHasFormatRecordPK.equals(other.teamHasFormatRecordPK)));
  }

  @Override
  public String toString()
  {
    return "Points: " + getPoints() + "\nMean: " + getMean() + "\nSD: " 
            + getStandardDeviation();
  }

  public double getMean()
  {
    return mean;
  }

  public void setMean(double mean)
  {
    this.mean = mean;
  }

  public double getStandardDeviation()
  {
    return standardDeviation;
  }

  public void setStandardDeviation(double standardDeviation)
  {
    this.standardDeviation = standardDeviation;
  }

  public int getPoints()
  {
    return points;
  }

  public void setPoints(int points)
  {
    this.points = points;
  }
}
