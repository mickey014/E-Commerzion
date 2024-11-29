package com.codewithkirk.OrderItemsService.Service;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsDto;
import com.codewithkirk.OrderItemsService.Model.OrderItems;

import java.util.List;
import java.util.Optional;

public interface OrderItemsService {
    void createOrderItems(OrderItemsDto orderItemsDto);

    List<OrderItems> getAllOrderItemsDetailsByCustomerId(Long customerId);

    Optional<OrderItems> getOrderItemsById(Long orderItemsId);
}
