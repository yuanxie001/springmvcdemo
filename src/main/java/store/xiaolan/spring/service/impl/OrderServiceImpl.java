package store.xiaolan.spring.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import store.xiaolan.spring.component.OrderComponent;
import store.xiaolan.spring.component.id.SnowflakeIdGenerate;
import store.xiaolan.spring.domian.Order;
import store.xiaolan.spring.service.OrderService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
        return optionalOrder.orElse(null);
    }
    @Override
    public List<Order> listOrder(String name) {
        return orderComponent.findByName(name);
    }

    @Override
    public List<Order> listOrderByNameAndPrice(String name, BigDecimal price) {
        return orderComponent.findByNameAndPrice(name,price);
    }
}
