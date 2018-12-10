package com.github.javydreamercsw.database.storage.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "tournament_format")
@XmlRootElement
@NamedQueries(
{
  @NamedQuery(name = "TournamentFormat.findAll", 
          query = "SELECT t FROM TournamentFormat t"),
  @NamedQuery(name = "TournamentFormat.findById", 
          query = "SELECT t FROM TournamentFormat t WHERE t.id = :id"),
  @NamedQuery(name = "TournamentFormat.findByFormatName", 
          query = "SELECT t FROM TournamentFormat t WHERE t.formatName = :formatName"),
  @NamedQuery(name = "TournamentFormat.findByImplementationClass", 
          query = "SELECT t FROM TournamentFormat t WHERE t.implementationClass = :implementationClass")
})
public class TournamentFormat implements Serializable
{
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 45)
  @Column(name = "format_name")
  private String formatName;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "implementation_class")
  private String implementationClass;
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "TournamentFormatGen")
  @TableGenerator(name = "TournamentFormatGen", table = "tm_id",
          pkColumnName = "table_name",
          valueColumnName = "last_id",
          pkColumnValue = "tournament_format",
          allocationSize = 1,
          initialValue = 1)
  private Integer id;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournamentFormat")
  private List<Tournament> tournamentList;

  public TournamentFormat()
  {
    tournamentList = new ArrayList<>();
  }

  public TournamentFormat(String formatName, String implementationClass)
  {
    this();
    this.formatName = formatName;
    this.implementationClass = implementationClass;
  }

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public String getFormatName()
  {
    return formatName;
  }

  public void setFormatName(String formatName)
  {
    this.formatName = formatName;
  }

  public String getImplementationClass()
  {
    return implementationClass;
  }

  public void setImplementationClass(String implementationClass)
  {
    this.implementationClass = implementationClass;
  }

  @XmlTransient
  public List<Tournament> getTournamentList()
  {
    return tournamentList;
  }

  public void setTournamentList(List<Tournament> tournamentList)
  {
    this.tournamentList = tournamentList;
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
    if (!(object instanceof TournamentFormat))
    {
      return false;
    }
    TournamentFormat other = (TournamentFormat) object;
    return !((this.id == null && other.id != null) 
            || (this.id != null && !this.id.equals(other.id)));
  }

  @Override
  public String toString()
  {
    return "com.github.javydreamercsw.database.storage.db.TournamentFormat[ id=" + id + " ]";
  }
}
