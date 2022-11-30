package Chapter_17_리액티브_프로그래밍.test_17_2_2;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class TempObserver implements Observer<TempInfo> {

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull TempInfo tempInfo) {
        System.out.println(tempInfo);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        System.err.println( "Got problem " + e.getMessage() );
    }

    @Override
    public void onComplete() {
        System.out.println("Done!");
    }
}
