package com.fxmind.entity;

import javax.persistence.*;

/**
 *
 * <p>Title: Techindicator</p>
 *
 * <p>Description: Domain Object describing a Techindicator entity</p>
 *
 */
@Entity (name="Techindicator")
@Table (name="techindicator")
@NamedQueries ({
	 @NamedQuery(name="Techindicator.findAll", query="SELECT techindicator FROM Techindicator techindicator")
	,@NamedQuery(name="Techindicator.findByIndicatorname", query="SELECT techindicator FROM Techindicator techindicator WHERE techindicator.indicatorname = :indicatorname")
	,@NamedQuery(name="Techindicator.findByIndicatornameContaining", query="SELECT techindicator FROM Techindicator techindicator WHERE techindicator.indicatorname like :indicatorname")
	,@NamedQuery(name="Techindicator.findByEnabled", query="SELECT techindicator FROM Techindicator techindicator WHERE techindicator.enabled = :enabled")
})
public class Techindicator extends AbstractEntity<Short> {
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Techindicator.findAll";
    public static final String FIND_BY_INDICATORNAME = "Techindicator.findByIndicatorname";
    public static final String FIND_BY_INDICATORNAME_CONTAINING ="Techindicator.findByIndicatornameContaining";
    public static final String FIND_BY_ENABLED = "Techindicator.findByEnabled";
	
    @Column(name="IndicatorName"  , length=50 , nullable=false , unique=false)
    private String indicatorname;

    @Column(name="Enabled"   , nullable=true , unique=false)
    private Boolean enabled; 

//    @OneToMany (targetEntity=com.fxmind.entity.Techdetail.class, fetch=FetchType.LAZY, mappedBy="indicatorid", cascade=CascadeType.REMOVE)//, cascade=CascadeType.ALL)
//    private Set <Techdetail> techdetailTechindicatorViaIndicatorid = new HashSet<Techdetail>();

    public Techindicator()
    {

    }

    public String getIndicatorname() {
        return indicatorname;
    }
	
    public void setIndicatorname (String indicatorname) {
        this.indicatorname =  indicatorname;
    }

    public Boolean getEnabled() {
        return enabled;
    }
	
    public void setEnabled (Boolean enabled) {
        this.enabled =  enabled;
    }
}

