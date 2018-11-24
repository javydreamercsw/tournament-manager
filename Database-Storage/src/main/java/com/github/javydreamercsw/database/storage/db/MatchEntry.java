/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
@Entity
@Table(name = "match_entry")
@XmlRootElement
@NamedQueries(
        {
          @NamedQuery(name = "MatchEntry.findAll", query = "SELECT m FROM MatchEntry m"),
          @NamedQuery(name = "MatchEntry.findById",
                  query = "SELECT m FROM MatchEntry m WHERE m.matchEntryPK.id = :id"),
          @NamedQuery(name = "MatchEntry.findByRoundId",
                  query = "SELECT m FROM MatchEntry m WHERE m.matchEntryPK.roundId = :roundId"),
          @NamedQuery(name = "MatchEntry.findByFormatId",
                  query = "SELECT m FROM MatchEntry m WHERE m.matchEntryPK.formatId = :formatId")
        })
public class MatchEntry implements Serializable
{
  private static final long serialVersionUID = 1L;
  @EmbeddedId
  protected MatchEntryPK matchEntryPK;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "matchEntry",
          fetch = FetchType.LAZY)
  private List<MatchHasTeam> matchHasTeamList;
  @JoinColumn(name = "format_id", referencedColumnName = "id", insertable = false,
          updatable = false)
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Format format;
  @JoinColumns(
  {
    @JoinColumn(name = "round_id", referencedColumnName = "id", insertable = false,
            updatable = false),
    @JoinColumn(name = "tournament_id", referencedColumnName = "id", insertable = false,
            updatable = false)
  })
  @ManyToOne(optional = true, fetch = FetchType.LAZY)
  private Round round;

  public MatchEntry()
  {
     setMatchHasTeamList(new ArrayList<>());
  }

  public MatchEntry(MatchEntryPK matchEntryPK)
  {
    this.matchEntryPK = matchEntryPK;
  }

  public MatchEntry(int roundId, int formatId)
  {
    this.matchEntryPK = new MatchEntryPK(roundId, formatId);
  }

  public MatchEntryPK getMatchEntryPK()
  {
    return matchEntryPK;
  }

  public void setMatchEntryPK(MatchEntryPK matchEntryPK)
  {
    this.matchEntryPK = matchEntryPK;
  }

  @XmlTransient
  public List<MatchHasTeam> getMatchHasTeamList()
  {
    return matchHasTeamList;
  }

  public final void setMatchHasTeamList(List<MatchHasTeam> matchHasTeamList)
  {
    this.matchHasTeamList = matchHasTeamList;
  }

  public Format getFormat()
  {
    return format;
  }

  public void setFormat(Format format)
  {
    this.format = format;
  }

  public Round getRound()
  {
    return round;
  }

  public void setRound(Round round)
  {
    this.round = round;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (matchEntryPK != null ? matchEntryPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof MatchEntry))
    {
      return false;
    }
    MatchEntry other = (MatchEntry) object;
    return !((this.matchEntryPK == null && other.matchEntryPK != null)
            || (this.matchEntryPK != null
            && !this.matchEntryPK.equals(other.matchEntryPK)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.MatchEntry[ matchEntryPK="
            + matchEntryPK + " ]";
  }
}
