package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.ncsu.csc.CoffeeMaker.utils.InventoryDeserializer;
import edu.ncsu.csc.CoffeeMaker.utils.InventorySerializer;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
@JsonDeserialize ( using = InventoryDeserializer.class )
@JsonSerialize ( using = InventorySerializer.class )
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long                           id;

    /**
     * The ingredients of this inventory and their corresponding amounts. This
     * map should always have all the ingredients present in the system.
     * Cascading should not be done because no operation done on the inventory
     * should propagate to its children.
     *
     * Resources referenced: https://www.baeldung.com/hibernate-persisting-maps
     */
    @ElementCollection ( fetch = FetchType.EAGER )
    @CollectionTable ( name = "inventory_items", joinColumns = @JoinColumn ( name = "inventory_id" ) )
    @MapKeyJoinColumn ( name = "ingredient_id" )
    @Column ( name = "amount" )
    private final Map<Ingredient, Integer> inventory;

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
        this.inventory = new HashMap<Ingredient, Integer>();
    }

    /**
     * Use this to create inventory with a map of ingredients and their amounts.
     *
     * @param inventory
     *            map to intialize inventory
     */
    public Inventory ( final Map<Ingredient, Integer> inventory ) {
        this.inventory = inventory;
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Retrieves the ingredients of this inventory
     *
     * @return a map of the ingredients of this inventory
     */
    public Map<Ingredient, Integer> getInventory () {
        return inventory;
    }

    /**
     * Add a new ingredient to the inventory. If the ingredient was already in
     * the inventory then its amount will be overwritten.
     *
     * @param i
     *            the ingredient to be added
     * @param amount
     *            the initial amount of the ingredient
     *
     * @throws IllegalArgumentException
     *             if the initial amount for an ingredient is negative
     */
    public void addNewIngredient ( final Ingredient i, final Integer amount ) {

        if ( amount < 0 ) {
            // the initial amount is negative
            throw new IllegalArgumentException( "The initial amount for an ingredient must be positive." );
        }

        inventory.put( i, amount );
    }

    /**
     * Updates the inventory by adding the amount mapped to each ingredient in
     * the map parameter if the ingredient already exists. Otherwise we add it
     * as a new ingredient with the amount specified.
     *
     * @param map
     *            a map containing the ingredients we are adding inventory for
     *            and the amount to be added.
     * @throws IllegalArgumentException
     *             if the amount to be added for any ingredient is negative
     *
     */
    public void updateInventory ( final Map<Ingredient, Integer> map ) {

        // Check early so we don't have to roll back with @Transactional
        for ( final Map.Entry<Ingredient, Integer> e : map.entrySet() ) {
            if ( e.getValue() < 0 ) {
                throw new IllegalArgumentException( "The amount to be added for an ingredient must be positive." );
            }
        }

        // Add each entry value to its corresponding ingredient value in the
        // inventory
        for ( final Map.Entry<Ingredient, Integer> e : map.entrySet() ) {

            // If the inventory already contains the ingredient then we will be
            // adding to it. Otherwise just put it in the map.
            if ( inventory.containsKey( e.getKey() ) ) {

                // This will throw an exception if there is integer overflow.
                int val = 0;
                try {
                    val = Math.addExact( inventory.get( e.getKey() ), e.getValue() );
                }
                catch ( final ArithmeticException ex ) {
                    // Throw an IAE detailing the issue.
                    throw new IllegalArgumentException(
                            "At least one updated ingredient's amount exceeds the max value of: " + Integer.MAX_VALUE );
                }

                inventory.put( e.getKey(), val );
            }
            else {
                inventory.put( e.getKey(), e.getValue() );
            }

        }
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        final Map<Ingredient, Integer> ingredients = r.getIngredients();

        for ( final Map.Entry<Ingredient, Integer> e : ingredients.entrySet() ) {
            final Integer amountInInv = inventory.get( e.getKey() );

            if ( amountInInv < e.getValue() ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        if ( enoughIngredients( r ) ) {

            final Map<Ingredient, Integer> rIngredients = r.getIngredients();

            for ( final Map.Entry<Ingredient, Integer> e : rIngredients.entrySet() ) {
                final Integer amountInInv = inventory.get( e.getKey() );

                inventory.put( e.getKey(), amountInInv - e.getValue() );
            }

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns a string describing the current contents of the inventory.
     *
     * @return String string representation of an inventory
     */
    @Override
    public String toString () {
        final StringBuilder build = new StringBuilder( "Inventory[\n" );

        final ArrayList<Map.Entry<Ingredient, Integer>> list = new ArrayList<Map.Entry<Ingredient, Integer>>(
                inventory.entrySet() );

        Collections.sort( list, new Comparator<Map.Entry<Ingredient, Integer>>() {

            @Override
            public int compare ( final Entry<Ingredient, Integer> o1, final Entry<Ingredient, Integer> o2 ) {
                return o1.getKey().getName().compareTo( o2.getKey().getName() );
            }

        } );

        for ( final Map.Entry<Ingredient, Integer> e : list ) {
            build.append( "{" + e.getKey().toString() + ": " + e.getValue() + "}\n" );
        }

        build.append( "]" );

        return build.toString();
    }

}
