package com.codewithkirk.orderService.Service;

import com.codewithkirk.orderService.Dto.OrderDto;
import com.codewithkirk.orderService.Model.Orders;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Orders createOrder(OrderDto orderDto);

    List<Orders> getAllOrderDetailsByCustomerId(Long customerId);
    Optional<Orders> getOrderDetailsByCustomerIdAndOrderId(Long customerId, String orderId);

    Orders updateOrder(Long customerId, String orderId, OrderDto orderDto);


    Optional<Orders> getByOrderId(String orderId);
}
