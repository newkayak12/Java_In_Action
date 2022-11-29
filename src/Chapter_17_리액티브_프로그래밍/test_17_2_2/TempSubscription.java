package Chapter_17_리액티브_프로그래밍.test_17_2_2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class TempSubscription implements Subscription {
    private final Subscriber<? super TempInfo> subscriber;
    private final String town;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    public TempSubscription(Subscriber<? super TempInfo> subscriber, String town) {
        this.subscriber = subscriber;
        this.town = town;
    }

    @Override
    public void request(long n) {
//        - BASIC
//        for ( long i = 0L; i < n; i++){//Subscriber가 만든 요청을 한 개씩 반복
//            try {
//                subscriber.onNext(TempInfo.fetch( town )); //현재 온도를 Subscriber로 전달
//            } catch (Exception e){
//                subscriber.onError( e ); //온도 가져오기 실패하면 Subscriber로 에러 전달
//                break;
//            }
//        }

//        - PREVENT STACKOVERFLOW
        executor.submit( () -> {
            for ( long i = 0L; i < n; i++){//Subscriber가 만든 요청을 한 개씩 반복
                try {
                    subscriber.onNext(TempInfo.fetch( town )); //현재 온도를 Subscriber로 전달
                } catch (Exception e){
                    subscriber.onError( e ); //온도 가져오기 실패하면 Subscriber로 에러 전달
                    break;
                }
            }
        });

    }

    @Override
    public void cancel() {
        subscriber.onComplete(); // 구독 취소 되면 (onComplete) 신호를 Subscriber에 전달
    }
}
