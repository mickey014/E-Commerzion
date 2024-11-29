package com.codewithkirk.orderService.Repository;

import com.codewithkirk.orderService.Model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, String> {

    Optional<Orders> findByCustomerIdAndOrderId(Long customerId, String orderId);
    Optional<Orders> findById(String orderId);

    List<Orders> findByCustomerId(Long customerId);
}
