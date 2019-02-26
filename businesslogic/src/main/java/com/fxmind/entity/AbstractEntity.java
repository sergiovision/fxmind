package com.fxmind.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Andrey.Charnamys on 2/7/14.
 */
@MappedSuperclass
public abstract class AbstractEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object){
            return true;
        }

        if (!(object instanceof AbstractEntity)){
            return false;
        }

        AbstractEntity other = (AbstractEntity) object;

        if (id == null) {
            if (other.getId() != null){
                return false;
            }
        } else if (!id.equals(other.getId())){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}