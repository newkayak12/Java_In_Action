package Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_4;

public class ExchangeService {

  public static final double DEFAULT_RATE = 1.35;

  public enum Money {
    USD(1.0), EUR(1.35387), GBP(1.69715), CAD(.92106), MXN(.07683);

    private final double rate;

    Money(double rate) {
      this.rate = rate;
    }
  }

  public static double getRate(Money source, Money destination) {
    return getRateWithDelay(source, destination);
  }

  private static double getRateWithDelay(Money source, Money destination) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return destination.rate / source.rate;
  }
}