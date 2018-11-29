package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "format")
@XmlRootElement
@NamedQueries(
        {
          @NamedQuery(name = "Format.findAll", query = "SELECT f FROM Format f"),
          @NamedQuery(name = "Format.findById", 
                  query = "SELECT f FROM Format f WHERE f.formatPK.id = :id"),
          @NamedQuery(name = "Format.findByName", 
                  query = "SELECT f FROM Format f WHERE f.name = :name"),
          @NamedQuery(name = "Format.findByDescription", 
                  query = "SELECT f FROM Format f WHERE f.description = :description"),
          @NamedQuery(name = "Format.findByGameId", 
                  query = "SELECT f FROM Format f WHERE f.formatPK.gameId = :gameId")
        })
public class Format implements Serializable
{
  private static final long serialVersionUID = 1L;
  @EmbeddedId
  protected FormatPK formatPK;
  @Basic(optional = false)
  @Column(name = "name")
  private String name;
  @Lob 
  @Column(name = "description")
  private String description;
  @JoinColumn(name = "game_id", referencedColumnName = "id", insertable = false, 
          updatable = false)
  @ManyToOne(optional = false)
  private Game game;

  public Format()
  {
  }

  public Format(FormatPK formatPK)
  {
    this.formatPK = formatPK;
  }

  public Format(String name)
  {
    this.name = name;
  }

  public Format(int gameId)
  {
    this.formatPK = new FormatPK(gameId);
  }

  public FormatPK getFormatPK()
  {
    return formatPK;
  }

  public void setFormatPK(FormatPK formatPK)
  {
    this.formatPK = formatPK;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public Game getGame()
  {
    return game;
  }

  public void setGame(Game game)
  {
    this.game = game;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (formatPK != null ? formatPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Format))
    {
      return false;
    }
    Format other = (Format) object;
    return !((this.formatPK == null && other.formatPK != null) 
            || (this.formatPK != null && !this.formatPK.equals(other.formatPK)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.Format[ formatPK=" + 
            formatPK + " ]";
  }

}
