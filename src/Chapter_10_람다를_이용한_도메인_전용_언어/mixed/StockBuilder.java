package Chapter_10_람다를_이용한_도메인_전용_언어.mixed;

import Chapter_10_람다를_이용한_도메인_전용_언어.Stock;
import Chapter_10_람다를_이용한_도메인_전용_언어.Trade;

public class StockBuilder {
    private final TradeBuilder builder;
    private final Trade trade;
    private final Stock stock = new Stock();

    public StockBuilder(TradeBuilder builder, Trade trade, String symbol){
        this.builder = builder;
        this.trade = trade;
        stock.setSymbol(symbol);
    }

    public TradeBuilder on(String market){
        stock.setMarket(market);
        trade.setStock(stock);
        return builder;
    }
}
