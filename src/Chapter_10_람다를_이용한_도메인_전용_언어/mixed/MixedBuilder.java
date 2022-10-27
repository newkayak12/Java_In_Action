package Chapter_10_람다를_이용한_도메인_전용_언어.mixed;

import Chapter_03_람다_표현식.Section1;
import Chapter_10_람다를_이용한_도메인_전용_언어.Order;
import Chapter_10_람다를_이용한_도메인_전용_언어.mixed.TradeBuilder;
import Chapter_10_람다를_이용한_도메인_전용_언어.Type;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class MixedBuilder {
    public static Order forCustomer(String customer, TradeBuilder... builders){
        Order order = new Order();
        order.setCustomer(customer);
        Stream.of(builders).forEach(b -> order.addTrade(b.trade));
        return order;
    }

    public static TradeBuilder buy(Consumer<TradeBuilder> consumer){
        return buildTrade(consumer, Type.BUY);
    }

    public static TradeBuilder sell(Consumer<TradeBuilder> consumer){
        return buildTrade(consumer, Type.SELL);
    }

    private static TradeBuilder buildTrade(Consumer<TradeBuilder> consumer, Type type){
        TradeBuilder builder = new TradeBuilder();
        builder.trade.setType(type);
        consumer.accept(builder);
        return builder;
    }
}
