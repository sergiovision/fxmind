package com.fxmind.entity;

import javax.persistence.*;

/**
 *
 * <p>Title: Settings</p>
 *
 * @author : Sergei Zhuravlev
 */
@Entity (name="Settings")
@Table (name="settings")
@NamedQueries({
     @NamedQuery(name = "Settings.findByName", query = "SELECT settings FROM Settings settings WHERE settings.name = :name")
})
public class Settings extends AbstractEntity<Integer> {
    private static final long serialVersionUID = 1L;

    // Named queries definitions
    public static final String FIND_BY_NAME = "Settings.findByName";

    // properties definitions

    public static final String BROKER_TIMEZONE = "TimeZone.Broker";

    public static final String MAIL_REG_LINK_PATH = "mail.reglink.base.path";

    public static final String THRIFT_SERVER_HOST = "ThriftServer.Host";
    public static final String THRIFT_SERVER_PORT = "ThriftServer.Port";

    public static final String APPNET_SERVER_HOST = "AppNETServer.Host";
    public static final String APPNET_SERVER_PORT = "AppNETServer.Port";

    public static final String THRIFT_JAVA_SERVER_PORT = "FXMindServerJava.Port";

    public static final String SOLR_SERVER_URL = "Solr.ServerURL";
    public static final String SOLR_TYPE = "Solr.ServerType";
    public static final String SOLR_LOCAL_HOME_PATH = "Solr.LocalHomePath";
    public static final String SOLR_QUERY_FIELDS_GENERIC = "Solr.QueryFieldsGeneric";

    public static final String NEWS_PARSE_HISTORY = "NewsEvent.ParseHistory";
    public static final String NEWS_HISTORY_STARTDATE = "NewsEvent.StartHistoryDate";
    public static final String NEWS_HISTORY_ENDDATE = "NewsEvent.EndHistoryDate";

    @Column(name="PropertyName" ,length=255)
    private String name;

    @Column(name="Value"   , nullable=true , unique=false, columnDefinition = "longtext")
    private String value;

    @Column(name="Description"   , nullable=true , unique=false, columnDefinition = "longtext")
    private String description;

    /**
    * Default constructor
    */
    public Settings() {
    }

    public String getName() {

        return name;
    }
	
    public void setName (String name) {
        this.name =  name;
    }
    
    public String getValue() {
        return value;
    }
	
    public void setValue (String value) {
        this.value =  value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription (String description) {
        this.description =  description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Settings settings = (Settings) o;

        if (description != null ? !description.equals(settings.description) : settings.description != null)
            return false;
        if (name != null ? !name.equals(settings.name) : settings.name != null) return false;
        if (value != null ? !value.equals(settings.value) : settings.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
