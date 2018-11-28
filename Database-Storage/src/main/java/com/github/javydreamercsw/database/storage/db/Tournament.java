package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "tournament")
@XmlRootElement
@NamedQueries(
        {
          @NamedQuery(name = "Tournament.findAll", query = "SELECT t FROM Tournament t"),
          @NamedQuery(name = "Tournament.findById",
                  query = "SELECT t FROM Tournament t WHERE t.id = :id"),
          @NamedQuery(name = "Tournament.findByName",
                  query = "SELECT t FROM Tournament t WHERE t.name = :name"),
          @NamedQuery(name = "Tournament.findByWinPoints",
                  query = "SELECT t FROM Tournament t WHERE t.winPoints = :winPoints"),
          @NamedQuery(name = "Tournament.findByDrawPoints",
                  query = "SELECT t FROM Tournament t WHERE t.drawPoints = :drawPoints"),
          @NamedQuery(name = "Tournament.findByLossPoints",
                  query = "SELECT t FROM Tournament t WHERE t.lossPoints = :lossPoints")
        })
public class Tournament implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "TournamentGen")
  @TableGenerator(name = "TournamentGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "tournament",
          allocationSize = 1,
          initialValue = 1)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Basic(optional = false)
  @Column(name = "name")
  private String name;
  @Basic(optional = false)
  @Column(name = "winPoints")
  private int winPoints;
  @Basic(optional = false)
  @Column(name = "drawPoints")
  private int drawPoints;
  @Basic(optional = false)
  @Column(name = "lossPoints")
  private int lossPoints;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournament",
          fetch = FetchType.LAZY)
  private List<Round> roundList;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournament",
          fetch = FetchType.LAZY)
  private List<TournamentHasTeam> tournamentHasTeamList;

  public Tournament()
  {
    setRoundList(new ArrayList<>());
    setTournamentHasTeamList(new ArrayList<>());
  }

  public Tournament(String name)
  {
    this();
    this.name = name;
  }

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
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

  @XmlTransient
  public List<Round> getRoundList()
  {
    return roundList;
  }

  public final void setRoundList(List<Round> roundList)
  {
    this.roundList = roundList;
  }

  @XmlTransient
  public List<TournamentHasTeam> getTournamentHasTeamList()
  {
    return tournamentHasTeamList;
  }

  public final void setTournamentHasTeamList(List<TournamentHasTeam> tournamentHasTeamList)
  {
    this.tournamentHasTeamList = tournamentHasTeamList;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
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
    return !((this.id == null && other.id != null)
            || (this.id != null && !this.id.equals(other.id)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.Tournament[ id="
            + id + " ]";
  }
}