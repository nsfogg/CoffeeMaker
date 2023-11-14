
package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Order;

/**
 * OrderRepository class to implement how Order functionality interacts with the
 * database This class extends JpaRepository
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds an Order object with the provided name. Spring will generate code
     * to make this happen.
     *
     * @param name
     *            Name of the ingredient
     * @return Found ingredient, null if none.
     */
    Order findByCustomer ( Long id );

}
