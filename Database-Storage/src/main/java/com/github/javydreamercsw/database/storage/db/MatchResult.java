package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "match_result")
@XmlRootElement
@NamedQueries(
{
  @NamedQuery(name = "MatchResult.findAll", query = "SELECT m FROM MatchResult m"),
  @NamedQuery(name = "MatchResult.findById", 
          query = "SELECT m FROM MatchResult m WHERE m.matchResultPK.id = :id"),
  @NamedQuery(name = "MatchResult.findByMatchResultTypeId", 
          query = "SELECT m FROM MatchResult m WHERE m.matchResultPK.matchResultTypeId = :matchResultTypeId")
})
public class MatchResult implements Serializable
{
  @Basic(optional = false)
  @NotNull
  @Column(name = "locked")
  private boolean locked;
  @Basic(optional = false)
  @NotNull
  @Column(name = "ranked")
  private boolean ranked;
  private static final long serialVersionUID = 1L;
  @EmbeddedId
  protected MatchResultPK matchResultPK;
  @JoinColumn(name = "match_result_type_id", referencedColumnName = "id", 
          insertable = false, updatable = false)
  @ManyToOne(optional = false)
  private MatchResultType matchResultType;
  @OneToMany(mappedBy = "matchResult")
  private List<MatchHasTeam> matchHasTeamList;

  public MatchResult()
  {
    matchHasTeamList = new ArrayList<>();
  }

  public MatchResult(MatchResultPK matchResultPK)
  {
    this();
    this.matchResultPK = matchResultPK;
  }

  public MatchResult(int matchResultTypeId)
  {
    this();
    this.matchResultPK = new MatchResultPK(matchResultTypeId);
  }

  public MatchResultPK getMatchResultPK()
  {
    return matchResultPK;
  }

  public void setMatchResultPK(MatchResultPK matchResultPK)
  {
    this.matchResultPK = matchResultPK;
  }

  public MatchResultType getMatchResultType()
  {
    return matchResultType;
  }

  public void setMatchResultType(MatchResultType matchResultType)
  {
    this.matchResultType = matchResultType;
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
  
  

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (matchResultPK != null ? matchResultPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof MatchResult))
    {
      return false;
    }
    MatchResult other = (MatchResult) object;
    return !((this.matchResultPK == null && other.matchResultPK != null) 
            || (this.matchResultPK != null 
            && !this.matchResultPK.equals(other.matchResultPK)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.MatchResult[ matchResultPK=" 
            + matchResultPK + " ]";
  }

  public boolean getRanked()
  {
    return ranked;
  }

  public void setRanked(boolean ranked)
  {
    this.ranked = ranked;
  }

  public boolean getLocked()
  {
    return locked;
  }

  public void setLocked(boolean locked)
  {
    this.locked = locked;
  }
}
