package lamdba.stream;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * 异步
 */
public class Shop {

    private String name;

    public Shop() {
    }

    public Shop(String name) {
        this.name = name;
    }

    private Random random=new Random();

    public double getPrice(String product) {
        return calculatePrice(product);
    }


    /**
     * 模拟延迟1s
     */
    public static void delay() {
        try {
            Thread.sleep(1000L);
//            throw new RuntimeException("error");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private double calculatePrice(String product) {
        delay();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public Future<Double> getPriceAsync(String product) {



        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        new Thread( () -> {
//            double price = calculatePrice(product);
//            futurePrice.complete(price);
            try {
                double price = calculatePrice(product);
                futurePrice.complete(price);
            } catch (Exception ex) {
                futurePrice.completeExceptionally(ex);
            }
        }).start();
        return futurePrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public static void main(String[] args) {
        Shop shop = new Shop();
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime+" msecs");

        //  计算商品价格的同时
        try {
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " msecs");
    }
}
