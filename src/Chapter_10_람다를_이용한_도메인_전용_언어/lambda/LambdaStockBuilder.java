package Chapter_10_람다를_이용한_도메인_전용_언어.lambda;

import Chapter_10_람다를_이용한_도메인_전용_언어.Stock;

public class LambdaStockBuilder {
    public Stock stock = new Stock();
    public void symbol(String symbol){
        stock.setSymbol(symbol);
    }
    public void market(String market){
        stock.setMarket(market);
    }
}
