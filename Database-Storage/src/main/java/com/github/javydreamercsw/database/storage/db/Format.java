package com.github.javydreamercsw.database.storage.db;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "format")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Format.findAll", query = "SELECT f FROM Format f"),
  @NamedQuery(name = "Format.findById", query = "SELECT f FROM Format f WHERE f.formatPK.id = :id"),
  @NamedQuery(name = "Format.findByName", query = "SELECT f FROM Format f WHERE f.name = :name"),
  @NamedQuery(
      name = "Format.findByDescription",
      query = "SELECT f FROM Format f WHERE f.description = :description"),
  @NamedQuery(
      name = "Format.findByGameId",
      query = "SELECT f FROM Format f WHERE f.formatPK.gameId = :gameId")
})
public class Format implements Serializable {
  @Basic(optional = false)
  @NotNull @Size(min = 1, max = 45) @Column(name = "name")
  private String name;

  @Size(max = 255) @Column(name = "description")
  private String description;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "format")
  private List<TeamHasFormatRecord> teamHasFormatRecordList;

  @Serial
  private static final long serialVersionUID = -6764484082819653225L;
  @EmbeddedId protected FormatPK formatPK;

  @JoinColumn(name = "game_id", referencedColumnName = "id", insertable = false, updatable = false)
  @ManyToOne(optional = false)
  private Game game;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "format")
  private List<MatchEntry> matchEntryList;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "format")
  private List<Tournament> tournamentList;

  public Format() {}

  public Format(FormatPK formatPK) {
    this.formatPK = formatPK;
  }

  public Format(String name) {
    this.name = name;
  }

  public Format(int gameId) {
    this.formatPK = new FormatPK(gameId);
  }

  public FormatPK getFormatPK() {
    return formatPK;
  }

  public void setFormatPK(FormatPK formatPK) {
    this.formatPK = formatPK;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  @XmlTransient
  public List<MatchEntry> getMatchEntryList() {
    return matchEntryList;
  }

  public void setMatchEntryList(List<MatchEntry> matchEntryList) {
    this.matchEntryList = matchEntryList;
  }

  @XmlTransient
  public List<Tournament> getTournamentList() {
    return tournamentList;
  }

  public void setTournamentList(List<Tournament> tournamentList) {
    this.tournamentList = tournamentList;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (formatPK != null ? formatPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Format)) {
      return false;
    }
    Format other = (Format) object;
    return !((this.formatPK == null && other.formatPK != null)
        || (this.formatPK != null && !this.formatPK.equals(other.formatPK)));
  }

  @Override
  public String toString() {
    return "com.github.javydreamercsw.database.storage.db.Format[ formatPK=" + formatPK + " ]";
  }

  @XmlTransient
  public List<TeamHasFormatRecord> getTeamHasFormatRecordList() {
    return teamHasFormatRecordList;
  }

  public void setTeamHasFormatRecordList(List<TeamHasFormatRecord> teamHasFormatRecordList) {
    this.teamHasFormatRecordList = teamHasFormatRecordList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
