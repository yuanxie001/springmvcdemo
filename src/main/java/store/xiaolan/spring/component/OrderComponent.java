package store.xiaolan.spring.component;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import store.xiaolan.spring.domian.Order;

@Repository
public interface OrderComponent extends MongoRepository<Order,Long>{

}
