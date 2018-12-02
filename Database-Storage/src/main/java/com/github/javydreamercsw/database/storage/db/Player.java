package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "player")
@XmlRootElement
@NamedQueries(
        {
          @NamedQuery(name = "Player.findAll", query = "SELECT p FROM Player p"),
          @NamedQuery(name = "Player.findById",
                  query = "SELECT p FROM Player p WHERE p.id = :id"),
          @NamedQuery(name = "Player.findByName",
                  query = "SELECT p FROM Player p WHERE p.name = :name")
        })
public class Player implements Serializable
{
  private static final long serialVersionUID = 3254339194105024707L;
  @Id
  @Basic(optional = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "PlayerGen")
  @TableGenerator(name = "PlayerGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "player",
          allocationSize = 1,
          initialValue = 1)
  @Column(name = "id")
  private Integer id;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 245)
  @Column(name = "name")
  private String name;
  @JoinTable(name = "team_has_player", joinColumns =
  {
    @JoinColumn(name = "player_id", referencedColumnName = "id")
  }, inverseJoinColumns =
  {
    @JoinColumn(name = "team_id", referencedColumnName = "id")
  })
  @ManyToMany
  private List<Team> teamList;
  @JoinTable(name = "player_has_record", joinColumns =
  {
    @JoinColumn(name = "player_id", referencedColumnName = "id")
  }, inverseJoinColumns =
  {
    @JoinColumn(name = "record_id", referencedColumnName = "id"),
    @JoinColumn(name = "record_game_id", referencedColumnName = "game_id")
  })
  @ManyToMany
  private List<Record> recordList;

  public Player()
  {
    recordList = new ArrayList<>();
    teamList = new ArrayList<>();
  }

  public Player(String name)
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

  @XmlTransient
  public List<Team> getTeamList()
  {
    return teamList;
  }

  public void setTeamList(List<Team> teamList)
  {
    this.teamList = teamList;
  }

  @XmlTransient
  public List<Record> getRecordList()
  {
    return recordList;
  }

  public void setRecordList(List<Record> recordList)
  {
    this.recordList = recordList;
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
    if (!(object instanceof Player))
    {
      return false;
    }
    Player other = (Player) object;
    return !((this.id == null && other.id != null) 
            || (this.id != null && !this.id.equals(other.id)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.Player[ id=" + 
            id + " ]";
  }
}
