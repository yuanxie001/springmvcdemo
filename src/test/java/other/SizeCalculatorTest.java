package other;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

import java.util.HashMap;
import java.util.Map;

public class SizeCalculatorTest {
    public static void main(String[] args) {
        long objectSize = ObjectSizeCalculator.getObjectSize(new A());
        System.out.println(objectSize);
    }

    public static class A{
        int a;
//        byte b;
//        byte c;
//        byte c1;
//        byte c2;
//        Long c3;
//        int t;
//        Integer f;
//        Locale locale = Locale.CANADA;
        Map map = new HashMap();
    }

}
