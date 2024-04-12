package store.xiaolan.spring.component;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import store.xiaolan.spring.domian.Order;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderComponent extends MongoRepository<Order,Long>{
    List<Order> findByName(String name);
    List<Order> findByNameAndPrice(String name, BigDecimal price);
}
