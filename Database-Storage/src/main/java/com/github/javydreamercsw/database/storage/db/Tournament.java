package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "tournament")
@XmlRootElement
@NamedQueries(
{
  @NamedQuery(name = "Tournament.findAll", 
          query = "SELECT t FROM Tournament t"),
  @NamedQuery(name = "Tournament.findById", 
          query = "SELECT t FROM Tournament t WHERE t.tournamentPK.id = :id"),
  @NamedQuery(name = "Tournament.findByTournamentFormatId", 
          query = "SELECT t FROM Tournament t WHERE t.tournamentPK.tournamentFormatId = :tournamentFormatId"),
  @NamedQuery(name = "Tournament.findByName", 
          query = "SELECT t FROM Tournament t WHERE t.name = :name"),
  @NamedQuery(name = "Tournament.findByWinPoints", 
          query = "SELECT t FROM Tournament t WHERE t.winPoints = :winPoints"),
  @NamedQuery(name = "Tournament.findByDrawPoints", 
          query = "SELECT t FROM Tournament t WHERE t.drawPoints = :drawPoints"),
  @NamedQuery(name = "Tournament.findByLossPoints", 
          query = "SELECT t FROM Tournament t WHERE t.lossPoints = :lossPoints"),
  @NamedQuery(name = "Tournament.findByStartDate", 
          query = "SELECT t FROM Tournament t WHERE t.startDate = :startDate"),
  @NamedQuery(name = "Tournament.findByEndDate", 
          query = "SELECT t FROM Tournament t WHERE t.endDate = :endDate"),
  @NamedQuery(name = "Tournament.findBySignupDate", 
          query = "SELECT t FROM Tournament t WHERE t.signupDate = :signupDate"),
  @NamedQuery(name = "Tournament.findBySignupTimeLimit", 
          query = "SELECT t FROM Tournament t WHERE t.signupTimeLimit = :signupTimeLimit"),
  @NamedQuery(name = "Tournament.findByRoundTimeLimit", 
          query = "SELECT t FROM Tournament t WHERE t.roundTimeLimit = :roundTimeLimit"),
  @NamedQuery(name = "Tournament.findByNoShowTimeLimit", 
          query = "SELECT t FROM Tournament t WHERE t.noShowTimeLimit = :noShowTimeLimit")
})
public class Tournament implements Serializable
{
  private static final long serialVersionUID = -6583090940281361773L;
  @EmbeddedId
  protected TournamentPK tournamentPK;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 245)
  @Column(name = "name")
  private String name;
  @Basic(optional = false)
  @NotNull
  @Column(name = "winPoints")
  private int winPoints;
  @Basic(optional = false)
  @NotNull
  @Column(name = "drawPoints")
  private int drawPoints;
  @Basic(optional = false)
  @NotNull
  @Column(name = "lossPoints")
  private int lossPoints;
  @Column(name = "startDate")
  private LocalDateTime startDate;
  @Column(name = "endDate")
  private LocalDateTime endDate;
  @Column(name = "signupDate")
  private LocalDate signupDate;
  @Column(name = "signupTimeLimit")
  private Integer signupTimeLimit;
  @Column(name = "roundTimeLimit")
  private Integer roundTimeLimit;
  @Column(name = "noShowTimeLimit")
  private Integer noShowTimeLimit;
  @JoinColumns(
  {
    @JoinColumn(name = "format_id", referencedColumnName = "id"),
    @JoinColumn(name = "format_game_id", referencedColumnName = "game_id")
  })
  @ManyToOne(optional = false)
  private Format format;
  @JoinColumn(name = "tournament_format_id", referencedColumnName = "id", 
          insertable = false, updatable = false)
  @ManyToOne(optional = false)
  private TournamentFormat tournamentFormat;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournament")
  private List<TournamentHasTeam> tournamentHasTeamList;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournament")
  private List<Round> roundList;

  public Tournament()
  {
    this("TBD", 0, 0, 0);
  }

  public Tournament(String name)
  {
    this(name, 0, 0, 0);
  }

  public Tournament(String name, int winPoints, int drawPoints, int lossPoints)
  {
    this.name = name;
    this.winPoints = winPoints;
    this.drawPoints = drawPoints;
    this.lossPoints = lossPoints;
    roundList = new ArrayList<>();
    tournamentHasTeamList = new ArrayList<>();
  }

  public Tournament(int tournamentFormatId)
  {
    this.tournamentPK = new TournamentPK(tournamentFormatId);
  }

  public TournamentPK getTournamentPK()
  {
    return tournamentPK;
  }

  public void setTournamentPK(TournamentPK tournamentPK)
  {
    this.tournamentPK = tournamentPK;
  }

  public Format getFormat()
  {
    return format;
  }

  public void setFormat(Format format)
  {
    this.format = format;
  }

  public TournamentFormat getTournamentFormat()
  {
    return tournamentFormat;
  }

  public void setTournamentFormat(TournamentFormat tournamentFormat)
  {
    this.tournamentFormat = tournamentFormat;
  }

  @XmlTransient
  public List<TournamentHasTeam> getTournamentHasTeamList()
  {
    return tournamentHasTeamList;
  }

  public void setTournamentHasTeamList(List<TournamentHasTeam> tournamentHasTeamList)
  {
    this.tournamentHasTeamList = tournamentHasTeamList;
  }

  @XmlTransient
  public List<Round> getRoundList()
  {
    return roundList;
  }

  public void setRoundList(List<Round> roundList)
  {
    this.roundList = roundList;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (tournamentPK != null ? tournamentPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Tournament))
    {
      return false;
    }
    Tournament other = (Tournament) object;
    return !((this.tournamentPK == null && other.tournamentPK != null) 
            || (this.tournamentPK != null 
            && !this.tournamentPK.equals(other.tournamentPK)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.Tournament[ tournamentPK=" 
            + tournamentPK + " ]";
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getWinPoints()
  {
    return winPoints;
  }

  public void setWinPoints(int winPoints)
  {
    this.winPoints = winPoints;
  }

  public int getDrawPoints()
  {
    return drawPoints;
  }

  public void setDrawPoints(int drawPoints)
  {
    this.drawPoints = drawPoints;
  }

  public int getLossPoints()
  {
    return lossPoints;
  }

  public void setLossPoints(int lossPoints)
  {
    this.lossPoints = lossPoints;
  }

  public LocalDateTime getStartDate()
  {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate)
  {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate()
  {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate)
  {
    this.endDate = endDate;
  }

  public LocalDate getSignupDate()
  {
    return signupDate;
  }

  public void setSignupDate(LocalDate signupDate)
  {
    this.signupDate = signupDate;
  }

  public Integer getSignupTimeLimit()
  {
    return signupTimeLimit;
  }

  public void setSignupTimeLimit(Integer signupTimeLimit)
  {
    this.signupTimeLimit = signupTimeLimit;
  }

  public Integer getRoundTimeLimit()
  {
    return roundTimeLimit;
  }

  public void setRoundTimeLimit(Integer roundTimeLimit)
  {
    this.roundTimeLimit = roundTimeLimit;
  }

  public Integer getNoShowTimeLimit()
  {
    return noShowTimeLimit;
  }

  public void setNoShowTimeLimit(Integer noShowTimeLimit)
  {
    this.noShowTimeLimit = noShowTimeLimit;
  }
}
