package lamdba.mothod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodRefenceTest {

    private List<User> users;
    private static List<String> test;
    static {
        test=new ArrayList<String>();
        test.add("tom");
        test.add("john");
        test.add("smith");
        test.add("lily");
        test.add("lucy");
        test.add("dream");
    }

    public static void main(String[] args) {
        List<Integer> collect = test.stream().map(String::length).collect(Collectors.toList());
        System.out.println(collect);

        List<String> list = test.stream().filter(str -> str.length() == 4).collect(Collectors.toList());
        System.out.println(list);
    }
}
