package com.github.javydreamercsw.database.storage.db;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "game")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Game.findAll", query = "SELECT g FROM Game g"),
  @NamedQuery(name = "Game.findById", query = "SELECT g FROM Game g WHERE g.id = :id"),
  @NamedQuery(name = "Game.findByName", query = "SELECT g FROM Game g WHERE g.name = :name"),
  @NamedQuery(
      name = "Game.findByDescription",
      query = "SELECT g FROM Game g WHERE g.description = :description")
})
public class Game implements Serializable {
  @Basic(optional = false)
  @NotNull @Size(min = 1, max = 45) @Column(name = "name")
  private String name;

  @Size(max = 255) @Column(name = "description")
  private String description;

  private static final long serialVersionUID = -6267533299417173163L;

  @Id
  @Basic(optional = false)
  @NotNull @GeneratedValue(strategy = GenerationType.TABLE, generator = "GameGen")
  @TableGenerator(
      name = "GameGen",
      table = "tm_id",
      pkColumnName = "table_name",
      valueColumnName = "last_id",
      pkColumnValue = "game",
      allocationSize = 1,
      initialValue = 1)
  @Column(name = "id")
  private Integer id;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
  private List<Format> formatList;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
  private List<Record> recordList;

  public Game() {}

  public Game(String name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @XmlTransient
  public List<Format> getFormatList() {
    return formatList;
  }

  public void setFormatList(List<Format> formatList) {
    this.formatList = formatList;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Game)) {
      return false;
    }
    Game other = (Game) object;
    return !((this.id == null && other.id != null)
        || (this.id != null && !this.id.equals(other.id)));
  }

  @Override
  public String toString() {
    return "com.github.javydreamercsw.database.storage.db.Game[ id=" + id + " ]";
  }

  @XmlTransient
  public List<Record> getRecordList() {
    return recordList;
  }

  public void setRecordList(List<Record> recordList) {
    this.recordList = recordList;
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
