package edu.ncsu.csc.CoffeeMaker.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.ncsu.csc.CoffeeMaker.utils.RecipeDeserializer;
import edu.ncsu.csc.CoffeeMaker.utils.RecipeSerializer;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 * @author Mohamed Tawous
 */
@Entity
@JsonDeserialize ( using = RecipeDeserializer.class )
@JsonSerialize ( using = RecipeSerializer.class )
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long                     id = 0L;

    /** Recipe name */
    private String                   name;

    /** Recipe price */
    @Min ( 0 )
    private Integer                  price;

    /**
     * The ingredients of this recipe and their corresponding amounts.
     *
     * Resources referenced: https://www.baeldung.com/hibernate-persisting-maps
     */
    @ElementCollection ( fetch = FetchType.EAGER )
    @CollectionTable ( name = "recipe_ingredients", joinColumns = @JoinColumn ( name = "recipe_id" ) )
    @MapKeyJoinColumn ( name = "ingredient_id" )
    @Column ( name = "amount" )
    private Map<Ingredient, Integer> ingredients;

    /**
     * Default Constructor for Recipe (Used by Hibernate)
     *
     */
    public Recipe () {
        super();
        this.ingredients = new HashMap<Ingredient, Integer>();
    }

    /**
     * A minimalist constructor that takes only the required fields of a Recipe.
     *
     * @param name
     *            the name of this recipe
     * @param price
     *            the price of this recipe
     */
    public Recipe ( final String name, final int price ) {
        this();
        setName( name );
        setPrice( price );
    }

    /**
     * A complete constructor that takes a name, price and a map of ingredients
     * containing all ingredients and their amounts.
     *
     * @param name
     *            the name of this recipe
     * @param price
     *            the price of this recipe
     * @param ingredients
     *            a map of ingredients containing all ingredients and their
     *            amounts
     */
    public Recipe ( final String name, final int price, final Map<Ingredient, Integer> ingredients ) {
        this( name, price );
        setIngredients( ingredients );
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Adds an ingredient to the ingredients map of this recipe
     *
     * @param ingr
     *            the ingredient to be added
     * @param amount
     *            the amount of the ingredient to add to this recipe
     *
     * @throws IllegalArgumentException
     *             if the amount for an ingredient is negative
     */
    public void addIngredient ( final Ingredient ingr, final int amount ) {

        if ( amount < 0 ) {
            // the amount is negative
            throw new IllegalArgumentException( "The amount for an ingredient must be positive." );
        }

        ingredients.put( ingr, amount );
    }

    /**
     * Retrieves the map of ingredients in this recipe
     *
     * @return the map of ingredients of this recipe
     */
    public Map<Ingredient, Integer> getIngredients () {
        return ingredients;

    }

    /**
     * Sets the map of ingredients in this recipe
     *
     *
     * @param ingredients
     *            map of ingredients to be set
     * @throws IllegalArgumentException
     *             if an ingredient amount is less than zero
     *
     */
    public void setIngredients ( final Map<Ingredient, Integer> ingredients ) {

        for ( final Map.Entry<Ingredient, Integer> e : ingredients.entrySet() ) {
            if ( e.getValue() < 0 ) {
                throw new IllegalArgumentException( "Amounts for an ingredient must be positive." );
            }
        }

        this.ingredients = ingredients;

    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        if ( name != null && !name.isEmpty() ) {
            this.name = name;
        }
        else {
            throw new IllegalArgumentException( "The name of a Recipe is required" );
        }
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        if ( price > 0 ) {
            this.price = price;
        }
        else {
            throw new IllegalArgumentException( "The price of a Recipe must be a positive number" );
        }
    }

    /**
     * Updates the fields to be equal to the passed Recipe
     *
     * @param r
     *            with updated fields
     */
    public void updateRecipe ( final Recipe r ) {
        setName( r.getName() );
        setPrice( r.getPrice() );
        setIngredients( r.getIngredients() );

    }

    @Override
    public String toString () {
        return "Recipe [id=" + id + ", name=" + name + ", price=" + price + ", ingredients=" + ingredients.toString()
                + "]";
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    /**
     * Compares two recipe objects
     *
     * @param obj
     *            in which to be compared
     *
     * @return true if object equal, false if not
     *
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
        final Recipe other = (Recipe) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        }
        else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

}
