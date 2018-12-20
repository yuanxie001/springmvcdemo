package us.zoom.spring.service.impl;

import org.springframework.stereotype.Service;
import us.zoom.spring.component.OrderComponent;
import us.zoom.spring.domian.Order;
import us.zoom.spring.service.OrderService;

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
