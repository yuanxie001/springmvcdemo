package lamdba.stream;

import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuctionAction {

    static List<List<Integer>> subsets(List<Integer> list) {
        if (list.isEmpty()) {
            List<List<Integer>> ans = new ArrayList<>();
            ans.add(Collections.emptyList());
            return ans;
        }

        Integer first = list.get(0);
        List<Integer> rest = list.subList(1, list.size());
        List<List<Integer>> subans = subsets(rest);
        List<List<Integer>> subans2 = insertAll(first, subans);

        return concat(subans, subans2);
    }

    private static List<List<Integer>> concat(List<List<Integer>> subans, List<List<Integer>> subans2) {
        List<List<Integer>> r = new ArrayList<>(subans);
        r.addAll(subans2);
        return r;
    }

    private static List<List<Integer>> insertAll(Integer first, List<List<Integer>> lists) {
        List<List<Integer>> result = new ArrayList<>();
        for (List<Integer> list : lists) {
            List<Integer> copyList = new ArrayList<>();
            copyList.add(first);
            copyList.addAll(list);
            result.add(copyList);
        }
        return result;
    }

    public static void main(String[] args) {
        List<Integer> list = Lists.newArrayList(1, 4, 9);
        List<List<Integer>> listList = subsets(list);
        System.out.println(listList);
    }

}
