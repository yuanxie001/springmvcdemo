package lamdba.stream;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TransactionTest {

    List<Transaction> transactions;

    @Before
    public void beforeTest() {

        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");
        transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

    }

    /**
     * (1)查找出2011年发生的所有交易，并按照交易额排序,由低到高
     */
    @Test
    public void test1() {
        transactions.stream().filter(dto->true).count();

        List<Transaction> transactionList = transactions.stream().
                filter(transaction -> transaction.getYear() == 2011).
                sorted(Comparator.comparing(Transaction::getValue)).
                collect(Collectors.toList());
        System.out.println(transactionList);
    }

    /**
     * (2)交易员都在哪些不同的城市工作过
     */
    @Test
    public void test2() {
        List<String> citys = transactions.stream()
                .map(Transaction::getTrader)
                .map(Trader::getCity)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(citys);
    }

    /**
     * (3)查找出所有来自剑桥的交易员，并按姓名排序
     */
    @Test
    public void test3() {
        List<Trader> traders = transactions.stream()
                .filter(transaction -> transaction!=null) //过滤为空的情况。如果为空，直接一个filter就可以解决了啊
                .map(Transaction::getTrader)
                .distinct()
                .filter(trader ->"Cambridge".equalsIgnoreCase(trader.getCity()))
                .sorted(Comparator.comparing(Trader::getName))
                .collect(Collectors.toList());
        System.out.println(traders);
    }

    /**
     * (4)查找出所有交易员的名称，并按照字母排序
     */
    @Test
    public void test4() {
        List<String> names = transactions.stream()
                .map(Transaction::getTrader)
                .distinct()
                .map(Trader::getName)
                .sorted(Comparator.comparing(String::toString))
                .collect(Collectors.toList());
        System.out.println(names);
    }

    /**
     * (5)查找出所有在米兰工作的交易员
     */
    @Test
    public void test5() {
        List<Trader> collect = transactions.stream()
                .map(Transaction::getTrader)
                .distinct()
                .filter(trader -> "Milan".equalsIgnoreCase(trader.getCity()))
                .collect(Collectors.toList());
        System.out.println(collect);
    }

    /**
     * (6)打印生活在剑桥的所有交易员的交易额总和
     */
    @Test
    public void test6() {
        Integer total = transactions.stream()
                .filter(transaction -> "Cambridge".equalsIgnoreCase(transaction.getTrader().getCity()))
                .map(Transaction::getValue)
                .reduce(0, (sum, a) -> sum + a);
        System.out.println("生活在剑桥的所有交易员的交易额总和:"+total);

    }

    /**
     * (7)所有交易中，交易金额最高的是多少
     */
    @Test
    public void test7() {
        Optional<Integer> max = transactions.stream()
                .map(Transaction::getValue)
                .max(Comparator.comparing(Integer::valueOf));
        System.out.println(max.get());
    }
    /**
     * (8)所有交易中，交易金额最低的是多少
     */
    @Test
    public void test8() {
        OptionalInt min = transactions.stream()
                .mapToInt(Transaction::getValue)
                .min();
        System.out.println(min.getAsInt());
    }
    /**
     * (9)输出勾股数，--看不懂
     */
    @Test
    public void test9() {
        Stream<int[]> pythagoreanTriples =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a ->
                                IntStream.rangeClosed(a, 100)
                                        .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
                                        .mapToObj(b ->
                                                new int[]{a, b, (int)Math.sqrt(a * a + b * b)})
                        );
        pythagoreanTriples.limit(5)
                .forEach(t ->
                        System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
    }


    /**
     * (9)获取每个交易员的交易数据
     */
    @Test
    public void test10() {
        Map<Trader, List<Transaction>> collect = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTrader));
        System.out.println(collect);
    }


    /**
     * (11)获取每个交易员的交易数量
     */
    @Test
    public void test11() {

        Map<Trader, Long> collect = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTrader, Collectors.counting()));
        System.out.println(collect);
    }

    /**
     * (12)获取每个交易员的交易总和,按交易员的名称和总和作为map
     */
    @Test
    public void test12() {

        Map<String, Integer> collect = transactions.stream()
                .collect(Collectors.groupingBy(transaction -> transaction.getTrader().getName(),
                        Collectors.summingInt(Transaction::getValue)));
        System.out.println(collect);
    }
    /**
     * (13)获取每个交易员的交易总和,按交易员的名称和交易记录的统计值作为value
     */
    @Test
    public void test13() {

        Map<String, DoubleSummaryStatistics> collect = transactions.stream()
                .collect(Collectors.groupingBy(transaction -> transaction.getTrader().getName(),
                        Collectors.summarizingDouble(Transaction::getValue)));
        System.out.println(collect);
    }
    /**
     * (13)获取每个交易员的交易总和,按交易员的名称和交易记录的统计值最大值
     */
    @Test
    public void test14() {

        Map<String, Integer> collect = transactions.stream()
                .collect(Collectors.groupingBy(transaction -> transaction.getTrader().getName(),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Transaction::getValue)),
                                option -> option.get().getValue())
                ));
        System.out.println(collect);
    }
}

