package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
@Table(name = "round")
@XmlRootElement
@NamedQueries(
{
  @NamedQuery(name = "Round.findAll", query = "SELECT r FROM Round r"),
  @NamedQuery(name = "Round.findById", 
          query = "SELECT r FROM Round r WHERE r.roundPK.id = :id"),
  @NamedQuery(name = "Round.findByTournamentId",
          query = "SELECT r FROM Round r WHERE r.roundPK.tournamentId = :tournamentId")
})
public class Round implements Serializable
{
  private static final long serialVersionUID = 1L;
  @EmbeddedId
  protected RoundPK roundPK;
  @JoinColumn(name = "tournament_id", referencedColumnName = "id", 
          insertable = false, updatable = false)
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Tournament tournament;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "round", fetch = FetchType.LAZY)
  private List<MatchEntry> matchEntryList;

  public Round()
  {
    setMatchEntryList(new ArrayList<>());
  }

  public Round(RoundPK roundPK)
  {
    this.roundPK = roundPK;
  }

  public Round(int tournamentId)
  {
    this.roundPK = new RoundPK(tournamentId);
  }

  public RoundPK getRoundPK()
  {
    return roundPK;
  }

  public void setRoundPK(RoundPK roundPK)
  {
    this.roundPK = roundPK;
  }

  public Tournament getTournament()
  {
    return tournament;
  }

  public void setTournament(Tournament tournament)
  {
    this.tournament = tournament;
  }

  @XmlTransient
  public List<MatchEntry> getMatchEntryList()
  {
    return matchEntryList;
  }

  public final void setMatchEntryList(List<MatchEntry> matchEntryList)
  {
    this.matchEntryList = matchEntryList;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (roundPK != null ? roundPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Round))
    {
      return false;
    }
    Round other = (Round) object;
    return !((this.roundPK == null && other.roundPK != null) 
            || (this.roundPK != null && !this.roundPK.equals(other.roundPK)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.Round[ roundPK="
            + roundPK + " ]";
  }
}
