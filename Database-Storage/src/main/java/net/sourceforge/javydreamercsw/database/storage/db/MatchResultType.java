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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "match_result_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MatchResultType.findAll", query = "SELECT m FROM MatchResultType m"),
    @NamedQuery(name = "MatchResultType.findById", query = "SELECT m FROM MatchResultType m WHERE m.id = :id"),
    @NamedQuery(name = "MatchResultType.findByType", query = "SELECT m FROM MatchResultType m WHERE m.type = :type")})
public class MatchResultType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MatchResultTypeGen")
    @TableGenerator(name = "MatchResultTypeGen", table = "tm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "match_result_type",
            allocationSize = 1,
            initialValue = 1)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "matchResultType")
    private List<MatchResult> matchResultList;

    public MatchResultType() {
    }

    public MatchResultType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlTransient
    public List<MatchResult> getMatchResultList() {
        return matchResultList;
    }

    public void setMatchResultList(List<MatchResult> matchResultList) {
        this.matchResultList = matchResultList;
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
        if (!(object instanceof MatchResultType)) {
            return false;
        }
        MatchResultType other = (MatchResultType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.database.storage.db.MatchResultType[ id=" + id + " ]";
    }

}
