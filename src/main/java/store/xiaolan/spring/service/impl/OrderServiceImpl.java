package store.xiaolan.spring.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import store.xiaolan.spring.component.OrderComponent;
import store.xiaolan.spring.component.id.SnowflakeIdGenerate;
import store.xiaolan.spring.domian.Order;
import store.xiaolan.spring.service.OrderService;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderComponent orderComponent;
    @Resource
    private SnowflakeIdGenerate snowflakeIdGenerate;

    @Override
    public Long saveOrder(Order order) {
        if (order.getId() ==null) {
            order.setId(snowflakeIdGenerate.generate());
        }
        if (order.getCreateTime() == null) {
            order.setCreateTime(new Date());
        }
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
