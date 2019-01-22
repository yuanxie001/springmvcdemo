package collection;

import java.util.TreeMap;

public class TreeMapTest {
    public static void main(String[] args) {
//        这个场景是构建旋转两次的场景。
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        treeMap.put(55,"fifty-five");
        treeMap.put(56,"fifty-six");
        treeMap.put(57,"fifty-seven");
        treeMap.put(58,"fifty-eight");
        treeMap.put(83,"eighty-three");
        treeMap.remove(57);
        treeMap.put(59,"fifty-nine");
    }
}
