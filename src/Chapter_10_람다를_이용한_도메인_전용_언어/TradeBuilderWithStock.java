package Chapter_10_람다를_이용한_도메인_전용_언어;

public class TradeBuilderWithStock {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;

    public TradeBuilderWithStock(MethodChainingOrderBuilder builder, Trade trade){
        this.builder = builder;
        this.trade = trade;
    }

    public MethodChainingOrderBuilder at(double price){
        trade.setPrice(price);
        return builder.addTrade(trade);
    }
}
