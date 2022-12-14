package Chapter_10_람다를_이용한_도메인_전용_언어;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String customer;
    private List<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade){
        trades.add(trade);
    }
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public List<Trade> getTrades() {
        return trades;
    }
    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }
}
