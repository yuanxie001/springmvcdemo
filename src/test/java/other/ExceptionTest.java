package other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class ExceptionTest {
    //private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static void main(String[] args) {
       String format="dynamodb.%s.amazonaws.com";

        String result = String.format(format, null);
        System.out.println(result);

    }
}
