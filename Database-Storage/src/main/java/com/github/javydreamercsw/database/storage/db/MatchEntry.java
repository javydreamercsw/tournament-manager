package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
  @Basic(optional = false)
  @Column(name = "match_date")
  private LocalDate matchDate;
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  protected MatchEntryPK matchEntryPK;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumns(
          {
            @JoinColumn(name = "format_id", referencedColumnName = "id",
                    insertable = false, updatable = false),
            @JoinColumn(name = "game_id", referencedColumnName = "id",
                    insertable = false, updatable = false)
          })
  private Format format;

  @ManyToOne(optional = true, fetch = FetchType.LAZY)
  @JoinColumns(
          {
            @JoinColumn(name = "round_id", referencedColumnName = "id",
                    insertable = false, updatable = false),
            @JoinColumn(name = "tournament_id", referencedColumnName = "id",
                    insertable = false, updatable = false)
          })
  private Round round;

  @OneToMany(mappedBy = "matchEntry", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<MatchHasTeam> matchHasTeamList;


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

  public List<MatchHasTeam> getMatchHasTeamList()
  {
    return matchHasTeamList;
  }

  public final void setMatchHasTeamList(List<MatchHasTeam> matchHasTeamList)
  {
    this.matchHasTeamList = matchHasTeamList;
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

  public LocalDate getMatchDate()
  {
    return matchDate;
  }

  public void setMatchDate(LocalDate matchDate)
  {
    this.matchDate = matchDate;
  }
}
