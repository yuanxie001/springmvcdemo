package jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * pipeline测试,可以一次性发送多个命令.而不用一个一个的来.
 */
public class JedisPiplineTest {


    /**
     * pipeline测试,可以一次性发送多个命令.而不用一个一个的来.
     * @throws InterruptedException
     */
    @Test
    public void testPipeline() throws InterruptedException {
        Jedis jedis = new Jedis("192.168.137.242", 6379);
        Pipeline pipelined = jedis.pipelined();
        String key="keyword";
        for (int i=1;i<10;i++){
            pipelined.hset(key,key+i,"x"+i);
        }
        Thread.sleep(10000);
        pipelined.sync();
    }


}
