package store.xiaolan.spring.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.xiaolan.spring.domian.Order;
import store.xiaolan.spring.service.OrderService;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    @RequestMapping("order/save")
    public Order saveOrder(@RequestBody Order order){
        if (order == null){
            throw new RuntimeException("order 不能为空");
        }
        Long id = orderService.saveOrder(order);
        order.setId(id);
        return order;
    }
}
