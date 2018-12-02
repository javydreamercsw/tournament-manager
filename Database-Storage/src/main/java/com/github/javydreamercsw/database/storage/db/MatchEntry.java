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
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "match_entry")
@XmlRootElement
@NamedQueries(
        {
          @NamedQuery(name = "MatchEntry.findAll", query = "SELECT m FROM MatchEntry m"),
          @NamedQuery(name = "MatchEntry.findById",
                  query = "SELECT m FROM MatchEntry m WHERE m.matchEntryPK.id = :id"),
          @NamedQuery(name = "MatchEntry.findByFormatId",
                  query = "SELECT m FROM MatchEntry m WHERE m.matchEntryPK.formatId = :formatId"),
          @NamedQuery(name = "MatchEntry.findByFormatGameId",
                  query = "SELECT m FROM MatchEntry m WHERE m.matchEntryPK.formatGameId = :formatGameId"),
          @NamedQuery(name = "MatchEntry.findByMatchDate",
                  query = "SELECT m FROM MatchEntry m WHERE m.matchDate = :matchDate")
        })
public class MatchEntry implements Serializable
{
  private static final long serialVersionUID = 3610712802330920081L;
  @EmbeddedId
  protected MatchEntryPK matchEntryPK;
  @Basic(optional = false)
  @NotNull
  @Column(name = "match_date")
  private LocalDate matchDate;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "matchEntry")
  private List<MatchHasTeam> matchHasTeamList;
  @JoinColumns(
          {
            @JoinColumn(name = "format_id", referencedColumnName = "id",
                    insertable = false, updatable = false),
            @JoinColumn(name = "format_game_id", referencedColumnName = "game_id",
                    insertable = false, updatable = false)
          })
  @ManyToOne(optional = false)
  private Format format;
  @JoinColumns(
          {
            @JoinColumn(name = "round_id", referencedColumnName = "id"),
            @JoinColumn(name = "round_tournament_id", referencedColumnName = "tournament_id")
          })
  @ManyToOne(optional = false)
  private Round round;

  public MatchEntry()
  {
    matchHasTeamList = new ArrayList<>();
  }

  public MatchEntry(MatchEntryPK matchEntryPK)
  {
    this();
    this.matchEntryPK = matchEntryPK;
  }

  public MatchEntry(MatchEntryPK matchEntryPK, LocalDate matchDate)
  {
    this();
    this.matchEntryPK = matchEntryPK;
    this.matchDate = matchDate;
  }

  public MatchEntry(int formatId, int formatGameId)
  {
    this();
    this.matchEntryPK = new MatchEntryPK(formatId, formatGameId);
  }

  public MatchEntryPK getMatchEntryPK()
  {
    return matchEntryPK;
  }

  public void setMatchEntryPK(MatchEntryPK matchEntryPK)
  {
    this.matchEntryPK = matchEntryPK;
  }

  public LocalDate getMatchDate()
  {
    return matchDate;
  }

  public void setMatchDate(LocalDate matchDate)
  {
    this.matchDate = matchDate;
  }

  @XmlTransient
  public List<MatchHasTeam> getMatchHasTeamList()
  {
    return matchHasTeamList;
  }

  public void setMatchHasTeamList(List<MatchHasTeam> matchHasTeamList)
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
