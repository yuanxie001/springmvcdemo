package store.xiaolan.spring.service;

import store.xiaolan.spring.domian.Order;

public interface OrderService {
    Long saveOrder(Order order);
    Order getOrder(Long id);
}
