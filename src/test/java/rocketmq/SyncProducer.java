package rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class SyncProducer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("rmq-group");
        producer.setNamesrvAddr("rocketmq01:9876;rocketmq02:9876");
        producer.setInstanceName("producer");
        producer.start();
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000); // 每秒发送一次MQ
                Message msg = new Message("itmayiedu-topic", // topic 主题名称
                        "TagA", // tag 临时值
                        ("itmayiedu-"+i).getBytes()// body 内容
                );
                SendResult sendResult = producer.send(msg);
                System.out.println(sendResult.toString());
                msg.setKeys("123");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }
}
