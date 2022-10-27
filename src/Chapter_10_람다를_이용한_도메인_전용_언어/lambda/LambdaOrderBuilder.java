package Chapter_10_람다를_이용한_도메인_전용_언어.lambda;

import Chapter_10_람다를_이용한_도메인_전용_언어.Order;
import Chapter_10_람다를_이용한_도메인_전용_언어.Type;

import java.util.function.Consumer;

public class LambdaOrderBuilder {
    private Order order = new Order();
    public static Order order (Consumer<LambdaOrderBuilder> consumer){
        LambdaOrderBuilder builder = new LambdaOrderBuilder();
        consumer.accept(builder);
        return builder.order;
    }
    public void forCustomer(String customer){
        order.setCustomer(customer);
    }
    public void buy(Consumer<LambdaTradeBuilder> consumer){
        trade(consumer, Type.BUY);
    }
    public void sell(Consumer<LambdaTradeBuilder> consumer){
        trade(consumer, Type.SELL);
    }
    private void trade(Consumer<LambdaTradeBuilder> consumer, Type type){
        LambdaTradeBuilder builder = new LambdaTradeBuilder();
        builder.trade.setType(type);
        consumer.accept(builder);
        order.addTrade(builder.trade);
    }
}
