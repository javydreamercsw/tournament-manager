package com.github.javydreamercsw.database.storage.db;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "tournament_has_team")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "TournamentHasTeam.findAll", query = "SELECT t FROM TournamentHasTeam t"),
  @NamedQuery(
      name = "TournamentHasTeam.findByTournamentId",
      query =
          "SELECT t FROM TournamentHasTeam t WHERE t.tournamentHasTeamPK.tournamentId ="
              + " :tournamentId"),
  @NamedQuery(
      name = "TournamentHasTeam.findByTeamId",
      query = "SELECT t FROM TournamentHasTeam t WHERE t.tournamentHasTeamPK.teamId = :teamId")
})
public class TournamentHasTeam implements Serializable {
  private static final long serialVersionUID = 1L;
  @EmbeddedId protected TournamentHasTeamPK tournamentHasTeamPK;

  @ManyToMany(mappedBy = "tournamentHasTeamList")
  private List<Record> recordList;

  @JoinColumn(name = "team_id", referencedColumnName = "id", insertable = false, updatable = false)
  @ManyToOne(optional = false)
  private Team team;

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

  public TournamentHasTeam() {}

  public TournamentHasTeam(TournamentHasTeamPK tournamentHasTeamPK) {
    this.tournamentHasTeamPK = tournamentHasTeamPK;
  }

  public TournamentHasTeam(int tournamentId, int teamId) {
    this.tournamentHasTeamPK = new TournamentHasTeamPK(tournamentId, teamId);
  }

  public TournamentHasTeamPK getTournamentHasTeamPK() {
    return tournamentHasTeamPK;
  }

  public void setTournamentHasTeamPK(TournamentHasTeamPK tournamentHasTeamPK) {
    this.tournamentHasTeamPK = tournamentHasTeamPK;
  }

  @XmlTransient
  public List<Record> getRecordList() {
    return recordList;
  }

  public void setRecordList(List<Record> recordList) {
    this.recordList = recordList;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public Tournament getTournament() {
    return tournament;
  }

  public void setTournament(Tournament tournament) {
    this.tournament = tournament;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (tournamentHasTeamPK != null ? tournamentHasTeamPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof TournamentHasTeam)) {
      return false;
    }
    TournamentHasTeam other = (TournamentHasTeam) object;
    return !((this.tournamentHasTeamPK == null && other.tournamentHasTeamPK != null)
        || (this.tournamentHasTeamPK != null
            && !this.tournamentHasTeamPK.equals(other.tournamentHasTeamPK)));
  }

  @Override
  public String toString() {
    return "com.github.javydreamercsw.database.storage.db.TournamentHasTeam[ tournamentHasTeamPK="
        + tournamentHasTeamPK
        + " ]";
  }
}
