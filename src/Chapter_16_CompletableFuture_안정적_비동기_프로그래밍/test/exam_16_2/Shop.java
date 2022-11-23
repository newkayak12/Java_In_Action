package Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_2;

import Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_4.Discount;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Shop {
    private String name;

    public Shop(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getPrice(String product) {
        return calculatorPrice(product);
    }
    public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        new Thread(() -> {
            try {
                double price = calculatorPrice(product);
                futurePrice.complete(price);
            } catch (Exception e) {
                futurePrice.completeExceptionally(e); //에러 전달
            }
        }).start();
        return futurePrice;
    }
    public Future<Double> getPriceAsyncLambda(String product){
        return CompletableFuture.supplyAsync(() -> calculatorPrice(product));
    }
    public String getPriceDiscount(String product){
        double price = calculatorPrice(product);
        Discount.Code code = Discount.Code.values()[new Random().nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }


    private double calculatorPrice(String product) {
//        delay();
        delayRandom();
        Random random = new Random();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    private void delayRandom(){
        int delay = 500 + new Random().nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
