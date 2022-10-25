package Chapter_10_람다를_이용한_도메인_전용_언어;

public class MethodChainingOrderBuilder {
    public final Order order = new Order();
    private MethodChainingOrderBuilder(String customer){
        order.setCustomer(customer);
    }

    public static MethodChainingOrderBuilder forCustomer(String customer){
        return new MethodChainingOrderBuilder(customer);
    }
    public TradeBuilder buy(int quantity){
        return new TradeBuilder(this, Type.BUY, quantity);
    }
    public TradeBuilder sell(int quantity){
        return new TradeBuilder(this, Type.SELL, quantity);
    }
    public MethodChainingOrderBuilder addTrade(Trade trade){
        order.addTrade(trade);
        return this;
    }
    public Order end(){
        return order;
    }
}
