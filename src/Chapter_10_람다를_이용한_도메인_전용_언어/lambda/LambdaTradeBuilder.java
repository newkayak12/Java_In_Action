package Chapter_10_람다를_이용한_도메인_전용_언어.lambda;

import Chapter_10_람다를_이용한_도메인_전용_언어.Trade;

import java.util.function.Consumer;

public class LambdaTradeBuilder {
    public Trade trade = new Trade();
    public void quantity(int quantity){
        trade.setQuantity(quantity);
    }
    public void price(double price){
        trade.setPrice(price);
    }
    public void stock(Consumer<LambdaStockBuilder> consumer){
        LambdaStockBuilder builder = new LambdaStockBuilder();
        consumer.accept(builder);
        trade.setStock(builder.stock);

    }
}
