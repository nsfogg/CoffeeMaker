
package edu.ncsu.csc.CoffeeMaker.repositories;

import java.util.List;

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
     * @param id
     *            id of the user
     * @return Found orders, null if none.
     */
    List<Order> findByUser ( Long id );

}
