package other;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClasTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<String> dateStrList = Arrays.asList(
                "2018-04-01 10:00:01",
                "2018-04-02 11:00:02",
                "2018-04-03 12:00:03",
                "2018-04-04 13:00:04",
                "2018-04-05 14:00:05"
        );

        ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

        for (String str : dateStrList) {

            executorService.execute(() -> {
                SimpleDateFormat simpleDateFormat = simpleDateFormatThreadLocal.get();
                Date parse            = null;
                try {
                    parse = simpleDateFormat.parse(str);
                } catch (Exception e){
                    System.out.println(e.getCause().getMessage());
                }
                System.out.println(parse);
            });
        }
    }

    int aligin2grain(int i,int grain){
        return (i+grain-1)& ~(grain-1);
    }

    void whileDouble(){
        double i = 0.0;
        while (i<100.1){
            i++;
        }
    }

    int less(double d){
        if (d < 100.0){
            return 1;
        }else {
            return -1;
        }
    }
}
