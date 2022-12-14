package Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_2;

import Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_4.Discount;
import Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_4.ExchangeService;
import Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_4.Quote;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Shops {
    List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItAll"));
    private final Executor executors = Executors.newFixedThreadPool(shops.size(), (Runnable r) -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });
    public List<String> findPrices(String product){
        return shops.stream().map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))).collect(Collectors.toList());
    }
    public List<String> findPricesParallelize(String product){
        return shops.parallelStream().map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))).collect(Collectors.toList());
    }
    public List<String> findPriceCompletableFuture(String product){
        List<CompletableFuture<String>> priceFuture = shops.stream().map(shop -> CompletableFuture.supplyAsync( () -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))).collect(Collectors.toList());
        return priceFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    public List<String> findPriceExecutor(String product){
        List<CompletableFuture<String>> priceFuture = shops.stream().map(shop -> CompletableFuture.supplyAsync( () -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)), executors)).collect(Collectors.toList());
        return priceFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    public List<String> findPriceDiscount(String product){
        return shops.stream()
                .map(shop -> shop.getPriceDiscount(product))
                .map(Quote::parse)
                .map(Discount::applyDiscount)
                .collect(Collectors.toList());
    }

    public List<String> findPriceCombineBlockAndNoneBlock(String product){
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPriceDiscount(product), executors))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executors)))
                .collect(Collectors.toList());

        return priceFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    public List<String> findPriceWithExchangeRate(String product){
        List<CompletableFuture<Double>> priceFuture =  shops.stream().map(
                shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                        .thenCombine(CompletableFuture.supplyAsync(() -> ExchangeService.getRate(ExchangeService.Money.EUR, ExchangeService.Money.USD)),
                        (price,rate) -> price * rate
                ))
                .collect(Collectors.toList());

        return priceFuture.stream().map(result -> {
            try {
                return result.get().toString();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

    }

    public List<String> findPriceWithExchangeRateTimeout(String product){
        List<CompletableFuture<Double>> priceFuture =  shops.stream().map(
                        shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                                .thenCombine(CompletableFuture.supplyAsync(() -> ExchangeService.getRate(ExchangeService.Money.EUR, ExchangeService.Money.USD)),
                                        (price,rate) -> price * rate
                                ).orTimeout(3, TimeUnit.SECONDS))
                .collect(Collectors.toList());

        return priceFuture.stream().map(result -> {
            try {
                return result.get().toString();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public List<String> findPriceWithExchangeRateCompleteOnTimeout(String product) {
        List<CompletableFuture<Double>> priceFuture = shops.stream().map(
                        shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                                .thenCombine(CompletableFuture.supplyAsync(() -> ExchangeService.getRate(ExchangeService.Money.EUR, ExchangeService.Money.USD)),
                                        (price, rate) -> price * rate
                                ).completeOnTimeout(1.23, 1, TimeUnit.SECONDS))
                .collect(Collectors.toList());

        return priceFuture.stream().map(result -> {
            try {
                return result.get().toString();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
    public Stream<CompletableFuture<String>> findPricesStream(String product){
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPriceDiscount(product), executors
                ))
                .map(future -> future.thenApply(Quote::parse))
                .map(future ->  future.thenCompose(quote ->
                  CompletableFuture.supplyAsync(
                          () -> Discount.applyDiscount(quote), executors
                )));
    }
}
