package other;

import java.util.Map;
import java.util.Properties;

public class SystemTest {
    public static void main(String[] args) {
        Properties properties = System.getProperties();
        System.out.println(properties);
        System.out.println("/r/n");
        Map<String, String> getenv = System.getenv();
        System.out.println(getenv);

    }
}
