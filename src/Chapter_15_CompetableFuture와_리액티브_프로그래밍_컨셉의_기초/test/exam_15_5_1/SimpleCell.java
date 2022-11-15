package Chapter_15_CompetableFuture와_리액티브_프로그래밍_컨셉의_기초.test.exam_15_5_1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

public class SimpleCell implements Flow.Publisher<Integer>, Flow.Subscriber<Integer> {
    public int value = 0;
    private String name;
    private List<Flow.Subscriber> subscribers = new ArrayList<>();

    public SimpleCell(String name) {
        this.name = name;
    }


    @Override
    public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
        subscribers.add(subscriber);
    }

    private void notifyAllSubscribers() {
        subscribers.forEach(subscriber -> subscriber.onNext(this.value));
    }

    @Override
    public void onNext(Integer newValue) {
        this.value = newValue;
        System.out.println(this.name + " : " + this.value);
        notifyAllSubscribers();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
    }

    @Override
    public void onError(Throwable throwable) {
    }

    @Override
    public void onComplete() {
    }
}
