package collection;

import java.util.Arrays;
import java.util.HashSet;

public class ASlistDemo {
    public static void main(String[] args) {
        HashSet<Integer> integers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));

        boolean contains = integers.contains(1);

        System.out.println(contains);
    }
}
