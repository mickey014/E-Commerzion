package com.codewithkirk.OrderItemsService.Repository;

import com.codewithkirk.OrderItemsService.Model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {
    List<OrderItems> findByCustomerId(Long customerId);
}
