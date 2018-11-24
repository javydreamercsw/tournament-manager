/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
@Entity
@Table(name = "match_has_team")
@XmlRootElement
@NamedQueries(
        {
          @NamedQuery(name = "MatchHasTeam.findAll", query = "SELECT m FROM MatchHasTeam m"),
          @NamedQuery(name = "MatchHasTeam.findByMatchId",
                  query = "SELECT m FROM MatchHasTeam m WHERE m.matchHasTeamPK.matchId = :matchId"),
          @NamedQuery(name = "MatchHasTeam.findByTeamId",
                  query = "SELECT m FROM MatchHasTeam m WHERE m.matchHasTeamPK.teamId = :teamId"),
          @NamedQuery(name = "MatchHasTeam.findByMatchResultId",
                  query = "SELECT m FROM MatchHasTeam m WHERE m.matchHasTeamPK.matchResultId = :matchResultId"),
          @NamedQuery(name = "MatchHasTeam.findByMatchResultMatchResultTypeId",
                  query = "SELECT m FROM MatchHasTeam m WHERE m.matchHasTeamPK.matchResultMatchResultTypeId = :matchResultMatchResultTypeId")
        })
public class MatchHasTeam implements Serializable
{
  private static final long serialVersionUID = 1L;
  @EmbeddedId
  protected MatchHasTeamPK matchHasTeamPK;
  @JoinColumns(
          {
            @JoinColumn(name = "match_id", referencedColumnName = "id", 
                    insertable = false,
                    updatable = false),
            @JoinColumn(name = "round_id", referencedColumnName = "id", 
                    insertable = false,
                    updatable = false),
            @JoinColumn(name = "format_id", referencedColumnName = "id", 
                    insertable = false,
                    updatable = false)
          })
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private MatchEntry matchEntry;
  @JoinColumns(
          {
            @JoinColumn(name = "match_result_id", referencedColumnName = "id",
                    insertable = false, updatable = false),
            @JoinColumn(name = "match_result_match_result_type_id",
                    referencedColumnName = "match_result_type_id", insertable = false,
                    updatable = false)
          })
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private MatchResult matchResult;
  @JoinColumn(name = "team_id", referencedColumnName = "id", insertable = false,
          updatable = false)
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Team team;

  public MatchHasTeam()
  {
  }

  public MatchHasTeam(MatchHasTeamPK matchHasTeamPK)
  {
    this.matchHasTeamPK = matchHasTeamPK;
  }

  public MatchHasTeam(int matchId, int teamId, int matchResultId, int matchResultMatchResultTypeId)
  {
    this.matchHasTeamPK = new MatchHasTeamPK(matchId, teamId, matchResultId, matchResultMatchResultTypeId);
  }

  public MatchHasTeamPK getMatchHasTeamPK()
  {
    return matchHasTeamPK;
  }

  public void setMatchHasTeamPK(MatchHasTeamPK matchHasTeamPK)
  {
    this.matchHasTeamPK = matchHasTeamPK;
  }

  public MatchEntry getMatchEntry()
  {
    return matchEntry;
  }

  public void setMatchEntry(MatchEntry matchEntry)
  {
    this.matchEntry = matchEntry;
  }

  public MatchResult getMatchResult()
  {
    return matchResult;
  }

  public void setMatchResult(MatchResult matchResult)
  {
    this.matchResult = matchResult;
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
    hash += (matchHasTeamPK != null ? matchHasTeamPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof MatchHasTeam))
    {
      return false;
    }
    MatchHasTeam other = (MatchHasTeam) object;
    return !((this.matchHasTeamPK == null && other.matchHasTeamPK != null)
            || (this.matchHasTeamPK != null
            && !this.matchHasTeamPK.equals(other.matchHasTeamPK)));
  }

  @Override
  public String toString()
  {
    return "net.sourceforge.javydreamercsw.database.storage.db.MatchHasTeam[ matchHasTeamPK="
            + matchHasTeamPK + " ]";
  }
}
