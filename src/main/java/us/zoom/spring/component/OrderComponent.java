package us.zoom.spring.component;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import us.zoom.spring.domian.Order;

@Repository
public interface OrderComponent extends MongoRepository<Order,Long>{

}
