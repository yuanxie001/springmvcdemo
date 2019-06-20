package store.xiaolan.spring.service.impl;

import org.springframework.stereotype.Service;
import store.xiaolan.spring.component.OrderComponent;
import store.xiaolan.spring.domian.Order;
import store.xiaolan.spring.service.OrderService;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderComponent orderComponent;
    @Override
    public Long saveOrder(Order order) {
        Order insert = orderComponent.insert(order);
        if (insert!=null){
            return insert.getId();
        }
        return null;
    }

    @Override
    public Order getOrder(Long id) {
        Optional<Order> optionalOrder = orderComponent.findById(id);
        if (optionalOrder.isPresent()){
            return optionalOrder.get();
        }
        return null;
    }
}
