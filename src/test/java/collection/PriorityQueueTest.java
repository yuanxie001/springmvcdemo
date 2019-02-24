package collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 测试最小堆的算法. 用来验证程序输出的结果.
 * <p>
 * 最小堆可以用来解决topk问题.感觉方式比快排变种方式要更加优雅,而且复杂度更低.
 * 个人更加倾向这种吧.
 * <p>
 * 最小堆解决快排的步骤是
 * 1. 新建一个大小为k的最小堆.
 * 2. 遍历数组
 * 3. 如果最小堆里面的元素个数不满k个,直接添加
 * 4. 如果大于等于k,则添加进去,进入淘汰机制.
 * <p>
 * 最小堆解决topk问题比快排变种的方式的优势在于数组是否是动态的
 * 数组是动态的,则快排无效.只能用这种
 * @author arnold.zhu
 * @date 2019-02-23
 */
public class PriorityQueueTest {
    public static void main(String[] args) {
        List<Integer> intList = new ArrayList<>(10);
        intList.add(18);
        intList.add(23);
        intList.add(59);
        intList.add(125);
        intList.add(149);
        intList.add(26);
        intList.add(98);
        intList.add(12);
        intList.add(64);
        List<Integer> maxTop3 = maxTop(intList, 3);
        System.out.println(maxTop3);
    }

    public static <E extends Comparable<? super E>> List<E> maxTop(List<E> list, int k) {
        PriorityQueue<E> priorityQueue = new PriorityQueue<>(k);
        for (E e : list) {
            if (priorityQueue.size() < k) {
                priorityQueue.add(e);
            } else {
                E peek = priorityQueue.peek();
                int i = e.compareTo(peek);
                if (i > 0) {
                    priorityQueue.poll();
                    priorityQueue.add(e);
                }
            }
        }
        List<E> result = new ArrayList<>(k);
        while (!priorityQueue.isEmpty()) {
            result.add(priorityQueue.poll());
        }
        Collections.reverse(result);
        return result;
    }
}