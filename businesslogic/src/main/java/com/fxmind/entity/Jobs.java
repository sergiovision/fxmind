package com.fxmind.entity;

import com.fxmind.quartz.JobDescription;

import javax.persistence.*;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Title: Jobs</p>
 * <p/>
 * <p>Description: Domain Object describing a Jobs entity</p>
 *
 * @author : Sergei Zhuravlev
 */

@Entity (name="Jobs")
@Table (name="jobs")
@NamedQueries({
        @NamedQuery(name="Jobs.findActive", query="SELECT jobs FROM Jobs jobs WHERE jobs.disabled = FALSE "),
        @NamedQuery(name = "Jobs.findByGroupName", query = "SELECT jobs FROM Jobs jobs WHERE jobs.group = :grp AND jobs.name = :name ")
})
public class Jobs extends AbstractEntity<Integer> implements JobDescription {
    private static final long serialVersionUID = 1565341L;

    public static final String FIND_ACTIVE = "Jobs.findActive";
    public static final String FIND_BY_GROUP_NAME = "Jobs.findByGroupName";

    @Column(name="CLASSPATH"  , length=255 , nullable=false , unique=false)
    private String classpath;

    @Column(name="GRP"  , length=255 , nullable=false , unique=false)
    private String group;

    @Column(name="NAME"  , length=255 , nullable=false , unique=false)
    private String name;

    @Column(name="CRON"  , length=128 , nullable=false , unique=false)
    private String cron;

    @Column(name="DESCRIPTION"  , length=1000 , nullable=true , unique=false)
    private String description;

    @Column(name="STATMESSAGE", nullable=true , unique=false, columnDefinition = "longtext")
    private String statMessage;

    //@Temporal(TemporalType.TIMESTAMP)
    @Column(name="PREVDATE"   , nullable=false , unique=false)
    private Timestamp previousDate;

    //@Temporal(TemporalType.TIMESTAMP)
    @Column(name="NEXTDATE"   , nullable=false , unique=false)
    private Timestamp nextDate;

    @Column(name="PARAMS"   , nullable=true , unique=false, columnDefinition = "longtext")
    private String params;

    @Column(name="DISABLED"   , nullable=false , unique=false, columnDefinition = "BIT")
    private Boolean disabled;

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatMessage() {
        return statMessage;
    }

    public void setStatMessage(String statMessage) {
        this.statMessage = statMessage;
    }

    public Timestamp getPreviousDate() {
        return previousDate;
    }

    public void setPreviousDate(Date previousDate) {
        this.previousDate = new Timestamp(previousDate.getTime());
    }

    public Timestamp getNextDate() {
        return nextDate;
    }

    public void setNextDate(Date nextDate) {
        this.nextDate = new Timestamp(nextDate.getTime());
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    // Implementation of methods interface @JobDescription
    @Override
    public String name() {
        return name;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public String cronExpression() {
        return cron;
    }

    @Override
    public String jobDescription() {
        return description;
    }

    @Override
    public String classPath() {
        return classpath;
    }

    @Override
    public Timestamp lastExecutionDate() {
        return previousDate;//new Timestamp(previousDate);
    }

    @Override
    public void setLastExecutionDate(Timestamp lastExecutionDate) {
        previousDate = lastExecutionDate;//new Timestamp(lastExecutionDate.toDate().getTime());
    }

    @Override
    public Timestamp firstExecutionDate() {
        return lastExecutionDate();
    }

    @Override
    public void setFirstExecutionDate(Timestamp firstExecutionDate) {
        previousDate = firstExecutionDate;//new Timestamp(firstExecutionDate.toDate().getTime());
    }

    @Override
    public void setNextExecutionDate(Timestamp nextExecutionDate) {
        nextDate = nextExecutionDate;//new Timestamp(nextExecutionDate.toDate().getTime());
    }

    @Override
    public Map<String, Object> jobParams()
    {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        try {

            String strParams = params;
            if ((strParams != null) && (strParams.length()>0)) {
                Properties props = new Properties();
                props.load(new StringReader(strParams));
                for(String key : props.stringPropertyNames()) {
                    String value = props.getProperty(key);
                    paramsMap.put(key, value);
                }
            }
        } catch (Exception e)
        {
            System.err.println("Error get jobs params: " + e.toString());
        }
        paramsMap.put("STATMESSAGE", this.getStatMessage());
        return paramsMap;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Jobs)) return false;
        if (!super.equals(object)) return false;

        Jobs jobs = (Jobs) object;

        if (classpath != null ? !classpath.equals(jobs.getClasspath()) : jobs.getClasspath() != null) return false;
        if (cron != null ? !cron.equals(jobs.getCron()) : jobs.getCron() != null) return false;
        if (description != null ? !description.equals(jobs.getDescription()) : jobs.getDescription() != null) return false;
        if (disabled != null ? !disabled.equals(jobs.isDisabled()) : jobs.isDisabled() != null) return false;
        if (group != null ? !group.equals(jobs.getGroup()) : jobs.getGroup() != null) return false;
        if (name != null ? !name.equals(jobs.getName()) : jobs.getName() != null) return false;
        if (nextDate != null ? !nextDate.equals(jobs.getNextDate()) : jobs.getNextDate() != null) return false;
        if (params != null ? !params.equals(jobs.getParams()) : jobs.getParams() != null) return false;
        if (previousDate != null ? !previousDate.equals(jobs.getPreviousDate()) : jobs.getPreviousDate() != null) return false;
        if (statMessage != null ? !statMessage.equals(jobs.getStatMessage()) : jobs.getStatMessage() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (classpath != null ? classpath.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (cron != null ? cron.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (statMessage != null ? statMessage.hashCode() : 0);
        result = 31 * result + (previousDate != null ? previousDate.hashCode() : 0);
        result = 31 * result + (nextDate != null ? nextDate.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (disabled != null ? disabled.hashCode() : 0);
        return result;
    }
}
