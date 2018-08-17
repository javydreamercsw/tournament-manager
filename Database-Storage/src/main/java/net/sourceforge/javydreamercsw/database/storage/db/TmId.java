/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "tm_id", uniqueConstraints =
{
  @UniqueConstraint(columnNames =
  {
    "table_name"
  })
})
@XmlRootElement
@NamedQueries(
        {
          @NamedQuery(name = "TmId.findAll", query = "SELECT v FROM TmId v"),
  @NamedQuery(name = "TmId.findById",
          query = "SELECT v FROM TmId v WHERE v.id = :id"),
  @NamedQuery(name = "TmId.findByLastId",
          query = "SELECT v FROM TmId v WHERE v.lastId = :lastId"),
  @NamedQuery(name = "TmId.findByTableName",
          query = "SELECT v FROM TmId v WHERE v.tableName = :tableName")
})
public class TmId implements Serializable
{

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Column(name = "last_id")
  private Integer lastId;
  @Column(name = "table_name")
  private String tableName;

  public TmId()
  {
  }

  public TmId(String tableName, int lastId)
  {
    this.tableName = tableName;
    this.lastId = lastId;
  }

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public Integer getLastId()
  {
    return lastId;
  }

  public void setLastId(Integer lastId)
  {
    this.lastId = lastId;
  }

  public String getTableName()
  {
    return tableName;
  }

  public void setTableName(String tableName)
  {
    this.tableName = tableName;
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
    if (!(object instanceof TmId))
    {
      return false;
    }
    TmId other = (TmId) object;
    return (this.id != null || other.id == null)
            && (this.id == null || this.id.equals(other.id));
  }

  @Override
  public String toString()
  {
    return "net.sourceforge.javydreamercsw.database.storage.db.TmId[ id=" + id + " ]";
  }
}
