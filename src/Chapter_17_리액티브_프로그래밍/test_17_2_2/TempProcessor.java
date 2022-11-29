package Chapter_17_리액티브_프로그래밍.test_17_2_2;

import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class TempProcessor implements Processor<TempInfo, TempInfo> {
    private Subscriber<? super TempInfo> subscriber;
    @Override
    public void subscribe(Subscriber<? super TempInfo> subscriber) {
        this.subscriber = subscriber;
    }
    @Override
    public void onNext(TempInfo temp) {
        subscriber.onNext( new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9)); //변환하고 TempInfo 다시 전송
    }
    //아래 모든 신호는 업스트림 구독자에 전달
    @Override
    public void onSubscribe(Subscription subscription) {
        subscriber.onSubscribe(subscription);
    }
    @Override
    public void onError(Throwable throwable) {
            subscriber.onError(throwable);
    }
    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}
