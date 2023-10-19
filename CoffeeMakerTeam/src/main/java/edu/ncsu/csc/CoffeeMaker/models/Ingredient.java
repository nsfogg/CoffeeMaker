package edu.ncsu.csc.CoffeeMaker.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Ingredient Model class to implement the Ingredient functionality within our
 * system. This class extends DomainObject
 */
@Entity
public class Ingredient extends DomainObject {

    /**
     * The unique id of the ingredient
     */
    @Id
    @GeneratedValue
    private Long   id = 0L;

    /**
     * The name of this recipe. It must be a string to support dynamic
     */
    private String name;

    /**
     * Constructor of the ingredient that takes in no params
     */
    public Ingredient () {
        super();
    }

    /**
     * The Ingredient constructor that takes parameters
     *
     * @param name
     *            the name of this ingredient
     */
    public Ingredient ( final String name ) {
        this();
        this.name = name;
    }

    /**
     * Retrieves the id of this ingredient
     *
     * @return Long the id of this ingredient
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of this ingredient
     *
     * @param id
     *            the id to which this id should be set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Retrieves the name of this ingredient
     *
     * @return String the name of this ingredient
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of this ingredient
     *
     * @param name
     *            name to be set
     */
    public void setName ( final String name ) {
        if ( name != null && !name.isEmpty() ) {
            this.name = name;
        }
        else {
            throw new IllegalArgumentException( "The name of an Ingredient is required" );
        }
    }

    /**
     * The string representation of this ingredient
     *
     * @return String of this ingredient containing all important fields
     */
    @Override
    public String toString () {
        return "Ingredient [id=" + id + ", name=" + name + "]";
    }

    /**
     * Eclipse generated hashcode method, modified to only consider the name and
     * not the id.
     */
    @Override
    public int hashCode () {
        return Objects.hash( name );
    }

    /**
     * Eclipse generated equals method, modified to only consider the name and
     * not the id.
     */
    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Ingredient other = (Ingredient) obj;
        return Objects.equals( name, other.name );
    }

}
