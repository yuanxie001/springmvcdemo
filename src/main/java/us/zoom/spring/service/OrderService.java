package us.zoom.spring.service;

import us.zoom.spring.domian.Order;

public interface OrderService {
    Long saveOrder(Order order);
    Order getOrder(Long id);
}
