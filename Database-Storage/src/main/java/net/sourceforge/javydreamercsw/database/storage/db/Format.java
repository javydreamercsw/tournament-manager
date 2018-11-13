/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db;

import java.io.Serializable;
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

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
@Entity
@Table(name = "format")
@XmlRootElement
@NamedQueries(
{
  @NamedQuery(name = "Format.findAll", query = "SELECT f FROM Format f"),
  @NamedQuery(name = "Format.findById", query = "SELECT f FROM Format f WHERE f.id = :id"),
  @NamedQuery(name = "Format.findByName", query = "SELECT f FROM Format f WHERE f.name = :name"),
  @NamedQuery(name = "Format.findByDescription", query = "SELECT f FROM Format f WHERE f.description = :description")
})
public class Format implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "FormatGen")
  @TableGenerator(name = "FormatGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "format",
          allocationSize = 1,
          initialValue = 1)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Basic(optional = false)
  @Column(name = "name")
  private String name;
  @Column(name = "description")
  private String description;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "format", fetch = FetchType.LAZY)
  private List<MatchEntry> matchEntryList;

  public Format()
  {
  }

  public Format(String name)
  {
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

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  @XmlTransient
  public List<MatchEntry> getMatchEntryList()
  {
    return matchEntryList;
  }

  public void setMatchEntryList(List<MatchEntry> matchEntryList)
  {
    this.matchEntryList = matchEntryList;
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
    if (!(object instanceof Format))
    {
      return false;
    }
    Format other = (Format) object;
    return !((this.id == null && other.id != null) 
            || (this.id != null && !this.id.equals(other.id)));
  }

  @Override
  public String toString()
  {
    return "net.sourceforge.javydreamercsw.database.storage.db.Format[ id=" 
            + id + " ]";
  }
}
