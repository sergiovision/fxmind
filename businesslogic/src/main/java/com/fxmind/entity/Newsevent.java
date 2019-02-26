package com.fxmind.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 *
 * <p>Title: Newsevent</p>
 *
 * <p>Description: Domain Object describing a Newsevent entity</p>
 *
 */
@Entity (name="Newsevent")
@Table (name="newsevent")
@NamedQueries ({
	 @NamedQuery(name="Newsevent.findAll", query="SELECT newsevent FROM Newsevent newsevent")
	,@NamedQuery(name="Newsevent.findByHappentime", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.happentime = :happentime")

	,@NamedQuery(name="Newsevent.findByName", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.name = :name")
	,@NamedQuery(name="Newsevent.findByNameContaining", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.name like :name")

	,@NamedQuery(name="Newsevent.findByImportance", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.importance = :importance")

	,@NamedQuery(name="Newsevent.findByActualval", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.actualval = :actualval")
	,@NamedQuery(name="Newsevent.findByActualvalContaining", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.actualval like :actualval")

	,@NamedQuery(name="Newsevent.findByForecastval", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.forecastval = :forecastval")
	,@NamedQuery(name="Newsevent.findByForecastvalContaining", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.forecastval like :forecastval")

	,@NamedQuery(name="Newsevent.findByPreviousval", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.previousval = :previousval")
	,@NamedQuery(name="Newsevent.findByPreviousvalContaining", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.previousval like :previousval")

	,@NamedQuery(name="Newsevent.findByParsetime", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.parsetime = :parsetime")

	,@NamedQuery(name="Newsevent.findByRaised", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.raised = :raised")

	,@NamedQuery(name="Newsevent.findNextEvent", query="SELECT newsevent " +
        "            FROM Newsevent newsevent, Currency C " +
        "            JOIN newsevent.currencyid " +
        "            WHERE (C.name = :c1 OR C.name = :c2) AND (newsevent.happentime BETWEEN :fr_dt AND :to_dt) AND (newsevent.importance >= :imp) ORDER BY newsevent.happentime ASC, newsevent.importance DESC")

    ,@NamedQuery(name="Newsevent.findUpcomingEvents", query="SELECT newsevent FROM Newsevent newsevent WHERE newsevent.happentime >= :curdate ORDER BY newsevent.happentime ASC, newsevent.importance DESC")
})

public class Newsevent extends AbstractEntity<Integer> implements Comparable {
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Newsevent.findAll";
    public static final String FIND_BY_HAPPENTIME = "Newsevent.findByHappentime";


    public static final String FIND_BY_NAME = "Newsevent.findByName";
    public static final String FIND_BY_NAME_CONTAINING ="Newsevent.findByNameContaining";
    public static final String FIND_BY_IMPORTANCE = "Newsevent.findByImportance";
    public static final String FIND_BY_ACTUALVAL = "Newsevent.findByActualval";
    public static final String FIND_BY_ACTUALVAL_CONTAINING ="Newsevent.findByActualvalContaining";
    public static final String FIND_BY_FORECASTVAL = "Newsevent.findByForecastval";
    public static final String FIND_BY_FORECASTVAL_CONTAINING ="Newsevent.findByForecastvalContaining";
    public static final String FIND_BY_PREVIOUSVAL = "Newsevent.findByPreviousval";
    public static final String FIND_BY_PREVIOUSVAL_CONTAINING ="Newsevent.findByPreviousvalContaining";
    public static final String FIND_BY_PARSETIME = "Newsevent.findByParsetime";
    public static final String FIND_BY_RAISED = "Newsevent.findByRaised";
    public static final String FIND_NEXTEVENT = "Newsevent.findNextEvent";
    public static final String FIND_UPCOMINGEVENTS = "Newsevent.findUpcomingEvents";

    @Column(name="HappenTime"   , nullable=false , unique=true)
    private Timestamp happentime; 

    @Column(name="Name"  , length=500 , nullable=false , unique=false)
    private String name; 

    @Column(name="Importance"   , nullable=false , unique=true)
    private Byte importance;

    @Column(name="ActualVal"  , length=127 , nullable=true , unique=false)
    private String actualval; 

    @Column(name="ForecastVal"  , length=127 , nullable=true , unique=false)
    private String forecastval; 

    @Column(name="PreviousVal"  , length=127 , nullable=true , unique=false)
    private String previousval; 

    @Column(name="ParseTime"   , nullable=false , unique=false)
    private Timestamp parsetime; 

    @Column(name="Raised"   , nullable=true , unique=false)
    private Boolean raised; 

    @Column(name="IndicatorValue"   , nullable=true , unique=false)
    private Double indicatorvalue;

    @ManyToOne (fetch=FetchType.EAGER , optional=false)
    @JoinColumn(name="CurrencyId", referencedColumnName = "ID" , nullable=false , unique=false , insertable=true, updatable=true) 
    private Currency currencyid;  

    /**
    * Default constructor
    */
    public Newsevent() {
    }

    public Timestamp getHappentime() {
        return happentime;
    }
	
    public void setHappentime (Timestamp happentime) {
        this.happentime =  happentime;
    }

    public String getName() {
        return name;
    }
	
    public void setName (String name) {
        this.name =  name;
    }

    public Byte getImportance() {
        return importance;
    }
	
    public void setImportance (Byte importance) {
        this.importance =  importance;
    }

    public String getActualval() {
        return actualval;
    }
	
    public void setActualval (String actualval) {
        this.actualval =  actualval;
    }

    public String getForecastval() {
        return forecastval;
    }
	
    public void setForecastval (String forecastval) {
        this.forecastval =  forecastval;
    }
	
    public String getPreviousval() {
        return previousval;
    }
	
    public void setPreviousval (String previousval) {
        this.previousval =  previousval;
    }
	
    public Timestamp getParsetime() {
        return parsetime;
    }
	
    public void setParsetime (Timestamp parsetime) {
        this.parsetime =  parsetime;
    }
	
    public Boolean getRaised() {
        return raised;
    }
	
    public void setRaised (Boolean raised) {
        this.raised =  raised;
    }

    public Double getIndicatorvalue() {
        return indicatorvalue;
    }
	
    public void setIndicatorvalue (Double indicatorvalue) {
        this.indicatorvalue =  indicatorvalue;
    }
	
    public Currency getCurrencyid () {
    	return currencyid;
    }
	
    public void setCurrencyid (Currency currencyid) {
    	this.currencyid = currencyid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Newsevent newsevent = (Newsevent) o;

        if (!happentime.equals(newsevent.happentime)) return false;
        if (!name.equals(newsevent.name)) return false;
        if (!importance.equals(newsevent.importance)) return false;
        if (forecastval != null ? !forecastval.equals(newsevent.forecastval) : newsevent.forecastval != null)
            return false;
        if (previousval != null ? !previousval.equals(newsevent.previousval) : newsevent.previousval != null)
            return false;
        if (!parsetime.equals(newsevent.parsetime)) return false;
        if (raised != null ? !raised.equals(newsevent.raised) : newsevent.raised != null) return false;
        return !(currencyid != null ? !currencyid.equals(newsevent.currencyid) : newsevent.currencyid != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + happentime.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + importance.hashCode();
        result = 31 * result + (forecastval != null ? forecastval.hashCode() : 0);
        result = 31 * result + (previousval != null ? previousval.hashCode() : 0);
        result = 31 * result + parsetime.hashCode();
        result = 31 * result + (raised != null ? raised.hashCode() : 0);
        result = 31 * result + (currencyid != null ? currencyid.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        Newsevent event = (Newsevent) o;
        return happentime.compareTo(event.getHappentime());
    }
}
