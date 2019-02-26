package com.fxmind.entity;

import javax.persistence.*;

/**
 *
 * <p>Title: Site</p>
 *
 * <p>Description: Domain Object describing a Site entity</p>
 *
 */
@Entity (name="Site")
@Table (name="site")
@NamedQueries ({
	 @NamedQuery(name="Site.findAll", query="SELECT site FROM Site site")
	,@NamedQuery(name="Site.findByName", query="SELECT site FROM Site site WHERE site.name = :name")
	,@NamedQuery(name="Site.findByNameContaining", query="SELECT site FROM Site site WHERE site.name like :name")

	,@NamedQuery(name="Site.findByDescription", query="SELECT site FROM Site site WHERE site.description = :description")
	,@NamedQuery(name="Site.findByDescriptionContaining", query="SELECT site FROM Site site WHERE site.description like :description")

})

public class Site extends AbstractEntity<Integer> {
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Site.findAll";
    public static final String FIND_BY_NAME = "Site.findByName";
    public static final String FIND_BY_NAME_CONTAINING ="Site.findByNameContaining";
    public static final String FIND_BY_DESCRIPTION = "Site.findByDescription";
    public static final String FIND_BY_DESCRIPTION_CONTAINING ="Site.findByDescriptionContaining";

    @Column(name="Name"  , length=50 , nullable=true , unique=false)
    private String name; 

    @Column(name="Description"  , length=255 , nullable=true , unique=false)
    private String description; 

    public Site() {
    }


    public String getName() {
        return name;
    }
	
    public void setName (String name) {
        this.name =  name;
    }

    public String getDescription() {
        return description;
    }
	
    public void setDescription (String description) {
        this.description =  description;
    }


}
