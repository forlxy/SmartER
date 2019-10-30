/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restws;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kasal
 */
@Entity
@Table(name = "USAGE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usage.findAll", query = "SELECT u FROM Usage u")
    , @NamedQuery(name = "Usage.findByUsageid", query = "SELECT u FROM Usage u WHERE u.usageid = :usageid")
    , @NamedQuery(name = "Usage.findByUsagedate", query = "SELECT u FROM Usage u WHERE u.usagedate = :usagedate")
    , @NamedQuery(name = "Usage.findByHours", query = "SELECT u FROM Usage u WHERE u.hours = :hours")
    , @NamedQuery(name = "Usage.findByFridge", query = "SELECT u FROM Usage u WHERE u.fridge = :fridge")
    , @NamedQuery(name = "Usage.findByAircond", query = "SELECT u FROM Usage u WHERE u.aircond = :aircond")
    , @NamedQuery(name = "Usage.findByWashmach", query = "SELECT u FROM Usage u WHERE u.washmach = :washmach")
    , @NamedQuery(name = "Usage.findByTemperature", query = "SELECT u FROM Usage u WHERE u.temperature = :temperature")
        //Static namedquery defined in usage 
    , @NamedQuery(name = "Usage.findByAddressMobile", query = "SELECT u FROM Usage u WHERE u.resid.address = :address AND u.resid.mobile = :mobile")})

public class Usage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "USAGEID")
    private Integer usageid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "USAGEDATE")
    @Temporal(TemporalType.DATE)
    private Date usagedate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "HOURS")
    private int hours;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FRIDGE")
    private double fridge;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AIRCOND")
    private double aircond;
    @Basic(optional = false)
    @NotNull
    @Column(name = "WASHMACH")
    private double washmach;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "TEMPERATURE")
    private Double temperature;
    @JoinColumn(name = "RESID", referencedColumnName = "RESID")
    @ManyToOne(optional = false)
    private Resident resid;

    public Usage() {
    }

    public Usage(Integer usageid) {
        this.usageid = usageid;
    }

    public Usage(Integer usageid, Date usagedate, int hours, double fridge, double aircond, double washmach) {
        this.usageid = usageid;
        this.usagedate = usagedate;
        this.hours = hours;
        this.fridge = fridge;
        this.aircond = aircond;
        this.washmach = washmach;
    }

    public Integer getUsageid() {
        return usageid;
    }

    public void setUsageid(Integer usageid) {
        this.usageid = usageid;
    }

    public Date getUsagedate() {
        return usagedate;
    }

    public void setUsagedate(Date usagedate) {
        this.usagedate = usagedate;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public double getFridge() {
        return fridge;
    }

    public void setFridge(double fridge) {
        this.fridge = fridge;
    }

    public double getAircond() {
        return aircond;
    }

    public void setAircond(double aircond) {
        this.aircond = aircond;
    }

    public double getWashmach() {
        return washmach;
    }

    public void setWashmach(double washmach) {
        this.washmach = washmach;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Resident getResid() {
        return resid;
    }

    public void setResid(Resident resid) {
        this.resid = resid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usageid != null ? usageid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usage)) {
            return false;
        }
        Usage other = (Usage) object;
        if ((this.usageid == null && other.usageid != null) || (this.usageid != null && !this.usageid.equals(other.usageid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "re.Usage[ usageid=" + usageid + " ]";
    }
    
}
