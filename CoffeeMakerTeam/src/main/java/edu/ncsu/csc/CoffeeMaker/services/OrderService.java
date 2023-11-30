package edu.ncsu.csc.CoffeeMaker.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderRepository;

/**
 * The OrderService is used to handle CRUD operations on the ORder model. In
 * addition to all functionality from `Service`, we also have functionality for
 * retrieving a single Order by name.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class OrderService extends Service<Order, Long> {

    /**
     * OrderRepository, to be autowired in by Spring and provide CRUD operations
     * on Recipe model.
     */
    @Autowired
    private OrderRepository orderRepository;

    @Override
    protected JpaRepository<Order, Long> getRepository () {
        return orderRepository;
    }

    /**
     * Find a Order with the given customer id
     *
     * @param id
     *            Name of the users order to find
     * @return found uer order, null if none
     */
    public List<Order> findByUser ( final long id ) {
        return orderRepository.findByUser( id );
    }

}
