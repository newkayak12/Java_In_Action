package Chapter_10_람다를_이용한_도메인_전용_언어;

public class TradeBuilder {
    private final MethodChainingOrderBuilder builder;
    public final Trade trade = new Trade();

    public TradeBuilder(MethodChainingOrderBuilder builder, Type type, int quantity){
        this.builder = builder;
        trade.setType(type);
        trade.setQuantity(quantity);
    }
    public StockBuilder stock(String symbol){
        return new StockBuilder(builder, trade, symbol);
    }
}
