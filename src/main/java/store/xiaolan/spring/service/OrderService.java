package store.xiaolan.spring.service;

import store.xiaolan.spring.domian.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    Long saveOrder(Order order);

    Order getOrder(Long id);

    List<Order> listOrder(String name);

    List<Order> listOrderByNameAndPrice(String name, BigDecimal price);
}
