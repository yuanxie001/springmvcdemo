package lamdba.stream;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DishTest {

    List<Dish> menu;
    @Before
    public void beforeMethod(){
        menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH));
    }

    /**
     * 初始配置
     */
    @Test
    public void test1(){
        List<String> list = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .map(Dish::getName)
                .limit(3)
                .collect(Collectors.toList());
        System.out.println(list);
    }

    /**
     *reduce方法 lamdba表达式
     */
    @Test
    public void testReduceLamdba(){
        Integer sums = menu.stream().map(Dish::getCalories).reduce(0, (sum, e) -> sum + e);
        System.out.println(sums);
    }

    /**
     * reduce方法 方法引用
     */
    @Test
    public void testReduceMethod(){
        Integer sums = menu.stream().map(Dish::getCalories).reduce(0, Integer::sum);
        System.out.println(sums);
    }

    /**
     * 获取最大值方法
     * 最大值里面需要添加一个比较器。不然无法获取
     */
    @Test
    public void testMax1(){
        //Optional<Integer> max = menu.stream().map(Dish::getCalories).max(Comparator.comparingInt(Integer::intValue));
        Optional<Integer> max = menu.stream().map(Dish::getCalories).reduce(Integer::max);

        System.out.println(max.get());
    }
    /**
     * 获取最大值方法
     * 使用基本流的情况
     */
    @Test
    public void testMax(){
        OptionalInt max = menu.stream().mapToInt(Dish::getCalories).max();

        System.out.println(max.getAsInt());
    }

    /**
     * 计数
     */
    @Test
    public void testCount(){
        //long count = menu.stream().count();
        Long count = menu.stream().collect(Collectors.counting());
        System.out.println(count);
    }


    /**
     * 分组，分组后mapping
     */
    @Test
    public void testGroup(){
        //long count = menu.stream().count();
//        Map<Dish.Type, List<Dish>> map = menu.stream().collect(Collectors.groupingBy(Dish::getType));
        // 添加分组后转换的case
        Map<Dish.Type, List<Integer>> collect = menu.stream().collect(Collectors.groupingBy(Dish::getType,
                Collectors.mapping(Dish::getCalories, Collectors.toList())));
        System.out.println(collect);
    }

    /**
     * 分组，分组后filter
     */
    @Test
    public void testGroup11(){
        //long count = menu.stream().count();
//        Map<Dish.Type, List<Dish>> map = menu.stream().collect(Collectors.groupingBy(Dish::getType));
        // 添加分组后转换的case
//        Map<Dish.Type, List<Dish>> collect = menu.stream().collect(Collectors.groupingBy(Dish::getType,
//                Collectors.filtering(d -> d.isVegetarian(),Collectors.toList())));
//        System.out.println(collect);
    }

    @Test
    public void testGroup1(){
        // peek操作是用于检查流处理的各种问题，不会导致问题存在。
        int sum = menu.stream().peek(t -> System.out.println(t.getName())).mapToInt(Dish::getCalories).sum();
        System.out.println(sum);
    }

    /**
     * 分组 谷歌实现
     */
    @Test
    public void testGroup2(){
        //long count = menu.stream().count();
        ImmutableListMultimap<Dish.Type, Dish> index = Multimaps.index(menu, dish -> dish.getType());
        Map<Dish.Type, Collection<Dish>> map= index.asMap();
        System.out.println(map);
    }

    /**
     * 用于实现flatmap计算多个组合的可能.
     * 讲a从1到100，b也从1到100.计算勾股数的组合。
     *
     * 两种方案都是计算勾股数组合的点子，还是很有意思的
     */
    @Test
    public void testFlatMap(){
        // 方案1
        IntStream.rangeClosed(1, 100).boxed().flatMap(
                a -> IntStream.rangeClosed(a, 100).
                        filter(b -> Math.sqrt(a * a + b * b) % 1 == 0).
                        mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)})
        ).peek(a -> System.out.println(Arrays.toString(a))).collect(Collectors.toList());

        // 方案2： 方案二是需要是计算一次，但用了double的stream，返回总觉得奇怪。
        List<double[]> result = IntStream.rangeClosed(1, 100).boxed().flatMap(
                a -> IntStream.rangeClosed(a, 100).
                        mapToObj(b -> new double[]{a, b, Math.sqrt(a * a + b * b)}).
                        filter(arr -> arr[2] % 1 == 0)
        )
                .peek(a -> System.out.println(Arrays.toString(a)))
                .collect(Collectors.toList());

    }

}
