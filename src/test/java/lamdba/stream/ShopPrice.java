package lamdba.stream;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ShopPrice {

    static List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"),
            new Shop("Amaze"));

    private static final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100),
                (Runnable r)-> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
            );


    /**
     * 异步，线程同步获取价格
     * @param product
     * @return
     */
    public static List<String> findPrices(String product) {
        return shops.stream().parallel()
                .map(shop -> String.format("%s price is %.2f",
                        shop.getName(), shop.getPrice(product)))
                .collect(Collectors.toList());

    }


    /**
     * 异步调用获取价格
     * @param product
     * @return
     */
    public static List<String> findPricesAsyc(String product) {
//        List<CompletableFuture<String>> priceFutures =
//                shops.stream()
//                        .map(shop -> CompletableFuture.supplyAsync(
//                                () -> String.format("%s price is %.2f",
//                                        shop.getName(), shop.getPrice(product))))
//                        .collect(Collectors.toList());
        List<CompletableFuture<String>> priceFutures =
                shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> shop.getName() + " price is " + shop.getPrice(product), executor))
                        .collect(Collectors.toList());
        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }



    public static void main(String[] args) {
        long start = System.nanoTime();
        System.out.println(findPricesAsyc("myPhone27S"));
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " msecs");
    }

}
