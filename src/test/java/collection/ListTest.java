package collection;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListTest {
    public static void main(String[] args) {
//        ArrayList<String> list = new ArrayList<>();
//        list.add("1");
//        list.add("2");
//        list.add("3");
//        list.add("4");
//        list.add("5");
//
//
//        ArrayList<String> list2 = new ArrayList<>();
//        list2.add("1");
//        list2.add("4");
//        list2.add("6");
//
//        list2.retainAll(list);
//
//        System.out.println(list2);

        String[] arr = new String[]{"1234","124",null,"012",null};
        String t = Stream.of(arr).filter(s -> s != null).collect(Collectors.joining(","));
        System.out.println(t);
//        String str = "abc";
//        String t = str;
//        System.out.println(str.startsWith(str));



    }
}
