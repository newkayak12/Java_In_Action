package Chapter_10_람다를_이용한_도메인_전용_언어.mixed;

import Chapter_10_람다를_이용한_도메인_전용_언어.Trade;

public class TradeBuilder {
    public Trade trade = new Trade();

    public TradeBuilder quantity(int quantity){
        trade.setQuantity(quantity);
        return this;
    }

    public TradeBuilder at(double price){
        trade.setPrice(price);
        return this;
    }

    public StockBuilder stock(String symbol){
        return new StockBuilder(this, trade, symbol);
    }
}
