package store.xiaolan.spring.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.RequestPath;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.ServletRequestPathUtils;
import store.xiaolan.spring.domian.Order;
import store.xiaolan.spring.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "order/save",method = RequestMethod.POST)
    public Order saveOrder(@RequestBody Order order){
        if (order == null){
            throw new RuntimeException("order 不能为空");
        }
        Long id = orderService.saveOrder(order);
        order.setId(id);
        return order;
    }

    @GetMapping("order/{id:.+}")
    public Order getOrder(@PathVariable("id") Long id, HttpServletRequest request){
        RequestPath parsedRequestPath = ServletRequestPathUtils.getParsedRequestPath(request);

        return orderService.getOrder(id);
    }

    @GetMapping("order/name")
    public List<Order> getOrderByName(@RequestParam("name") String name,
                                    @RequestParam(name = "price",required = false) BigDecimal price){
        if (price!=null && price.doubleValue() < 0) {
            throw new RuntimeException("price 不能小余0");
        }

        if (price != null) {
            return orderService.listOrderByNameAndPrice(name,price);
        }
        return orderService.listOrder(name);
    }
}
