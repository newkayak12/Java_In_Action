package Chapter_10_람다를_이용한_도메인_전용_언어;

import java.util.stream.Stream;

public class NestedFunctionOrderBuilder {
    public static Order order(String customer, Trade... trades){
        Order order = new Order();
        order.setCustomer(customer);
        Stream.of(trades).forEach(order::addTrade);
        return order;
    }
    public static Trade buy(int quantity, Stock stock, double price){
        return buildTrade(quantity, stock, price, Type.BUY);
    }
    public static Trade sell(int quantity, Stock stock, double price){
        return buildTrade(quantity, stock, price, Type.SELL);
    }

    private static Trade buildTrade(int quantity, Stock stock, double price, Type type){
        Trade trade = new Trade();
        trade.setQuantity(quantity);
        trade.setStock(stock);
        trade.setPrice(price);
        trade.setType(type);
        return trade;
    }

    public static double at(double price){
        return price;
    }

    public static Stock stock(String symbol, String market){
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setMarket(market);
        return stock;
    }
    public static String on(String market){
        return market;
    }
}
