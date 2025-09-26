package com.github.javydreamercsw.database.storage.db;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "round")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Round.findAll", query = "SELECT r FROM Round r"),
  @NamedQuery(name = "Round.findById", query = "SELECT r FROM Round r WHERE r.roundPK.id = :id"),
  @NamedQuery(
      name = "Round.findByTournamentId",
      query = "SELECT r FROM Round r WHERE r.roundPK.tournamentId = :tournamentId")
})
public class Round implements Serializable {
  private static final long serialVersionUID = 1L;
  @EmbeddedId protected RoundPK roundPK;

  @JoinColumns({
    @JoinColumn(
        name = "tournament_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "tournament_tournament_format_id",
        referencedColumnName = "tournament_format_id",
        insertable = false,
        updatable = false)
  })
  @ManyToOne(optional = false)
  private Tournament tournament;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "round")
  private List<MatchEntry> matchEntryList;

  @Basic(optional = false)
  @NotNull @Column(name = "roundNumber")
  private int roundNumber;

  public Round() {
    matchEntryList = new ArrayList<>();
  }

  public Round(RoundPK roundPK) {
    this.roundPK = roundPK;
  }

  public Round(int tournamentId) {
    this.roundPK = new RoundPK(tournamentId);
  }

  public RoundPK getRoundPK() {
    return roundPK;
  }

  public void setRoundPK(RoundPK roundPK) {
    this.roundPK = roundPK;
  }

  public Tournament getTournament() {
    return tournament;
  }

  public void setTournament(Tournament tournament) {
    this.tournament = tournament;
  }

  @XmlTransient
  public List<MatchEntry> getMatchEntryList() {
    return matchEntryList;
  }

  public void setMatchEntryList(List<MatchEntry> matchEntryList) {
    this.matchEntryList = matchEntryList;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (roundPK != null ? roundPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Round)) {
      return false;
    }
    Round other = (Round) object;
    return !((this.roundPK == null && other.roundPK != null)
        || (this.roundPK != null && !this.roundPK.equals(other.roundPK)));
  }

  @Override
  public String toString() {
    return "com.github.javydreamercsw.database.storage.db.Round[ roundPK=" + roundPK + " ]";
  }

  public int getRoundNumber() {
    return roundNumber;
  }

  public void setRoundNumber(int roundNumber) {
    this.roundNumber = roundNumber;
  }
}
