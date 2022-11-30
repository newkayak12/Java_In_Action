package Chapter_17_리액티브_프로그래밍.test_17_2_2;

import io.reactivex.rxjava3.core.Observable;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RxTemperature {
    public static Observable<TempInfo> getTemperature(String town ){
        return Observable.create(emitter -> //소비하는 Observable 만들기
                Observable.interval(1, TimeUnit.SECONDS).subscribe(i -> { // 매 초마다 무한으로 증가하는 long 값을 방출
                    if(!emitter .isDisposed()){ // 소비된 옵저버가 아직 폐기되지 않았으면 어떤 작업 수행
                        if( i >= 5) { //온도 5번 보고하면 스트림 종료
                            emitter.onComplete();
                        } else {
                            try{
                                emitter.onNext( TempInfo.fetch(town)); //온도 보고
                            } catch ( Exception e ){
                                emitter.onError(e); //에러
                            }
                        }
                    }
                })
        );
    }
    public static Observable<TempInfo> getCelsiusTemperature( String town ){
        return getTemperature(town).map(temp -> new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 /9));
    }
    public static Observable<TempInfo> getNegativeTemperature( String town ){
        return getCelsiusTemperature(town).filter( temp -> temp.getTemp() < 0);
    }
    public static Observable<TempInfo> getCelsiusTemperatureMerge( String... town ){
        return Observable.merge(Arrays.stream(town).map(RxTemperature::getCelsiusTemperature).collect(Collectors.toList()));
    }
}
