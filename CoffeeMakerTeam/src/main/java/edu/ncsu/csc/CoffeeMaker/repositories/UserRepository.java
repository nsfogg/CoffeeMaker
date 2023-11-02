package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * UserRepository is used to provide CRUD operations for the User model. Spring
 * will generate appropriate code with JPA.
 *
 * @author Kai Presler-Marshall
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User object with the provided name. Spring will generate code to
     * make this happen.
     *
     * @param name
     *            Name of the recipe
     * @return Found User, null if none.
     */
    User findByName ( String name );

}
