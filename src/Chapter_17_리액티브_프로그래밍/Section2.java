package Chapter_17_리액티브_프로그래밍;

import Chapter_17_리액티브_프로그래밍.test_17_2_2.RxTemperature;
import Chapter_17_리액티브_프로그래밍.test_17_2_2.TempInfo;
import Chapter_17_리액티브_프로그래밍.test_17_2_2.TempObserver;
import io.reactivex.rxjava3.core.Observable;

public class Section2 {
    public static void main(String[] args) {
    /**
     *                      > 17.3.2 Observable 변환하고 합치기
     * RxJava나 기타 리액티브 라이브러리는 자바 9 Flow API에 비해 스트림을 합치고, 만들고, 거르는 등의 풍부한 도구 상자를 제공하는 것이 장점이다.
     * 이미 살펴봤듯이 한 스트림을 다른 스트림의 입력으로 사용할 수 있다. 17.2.3절에서는 자바 9의 Flow.Processor를 이용해 화씨를 섭씨로 바꿀 수 있음
     * 을 살펴봤다. 이 외에도 스트림에서 관심있는 요소만 거른 다른 스트림을 만들거나 매핑 함수로 요소를 변환하거나 두 스트림을 다양한 방법으로 합치는 등의
     * 작업을 할 수 있다. (Flow.Processor 만으로 달성하기 어려움)
     *
     * 이런 변환, 합치기 함수는 상당히 복잡하므로 말고 설명하기 어렵다. 예를 들어 RxJava의  mergeDelayError의 설명을 보자
     *
     *          "Observable을 한 Observable로 방출하는 Observable을 평면화해서 모든 Observable 소스에서
     *          성공적으로 방출된 모든 항목을 Observable가 수신할 수 있도록 한다. 이때 이들 중 에러 알림이 발생해도
     *          방해받지 않으며 이를 Observable에 동시 구독할 수 있는 숫자는 한계가 있다."
     *
     * 바로 이해하기는 어렵다. 리액티브스트림을 이해하려면 마블 다이어 그램을 이해하는 것이 좋다. 더 나아가 Observable에 map을 이용해서 화씨 -> 섭씨로
     * 변환해 보자
     *
     *                           Chapter_17_리액티브_프로그래밍/test_17_2_2/RxTemperature.java
     */
        System.out.println("\n------- RxJava map --------");
        Observable<TempInfo> observable = RxTemperature.getCelsiusTemperature("Seoul");
        observable.blockingSubscribe(new TempObserver());

        System.out.println("\n------- RxJava filter --------");
        observable = RxTemperature.getNegativeTemperature("Seoul");
        observable.blockingSubscribe(new TempObserver());
    /**
     * 이번에는 마지막으로 구현한 메소드를 일반화해서 사용자가 한 도시뿐 아니라 여러 도시에서 온도를 방출하는 Observable을 가질 수 있다고 가정하자
     * 아래 메소드는 이전 메소드를 각 도시에 호출한 다음 이전 호출로부터 얻은 Observable을 merge로 하나로 합쳐서 마지막 요구사항을 구현한다.
     * 이 메소드를 온도를 얻으려는 도시 집합을 포함하는 가변 인수를 받는다. 이 가변 인수를 문자열 스트림으로 변환하고 getCelsiusTemperature 메소드로
     * 전달한다. 이런식으로 매 초마다 온도를 방출하는 Observable로 변신한다. 마지막으로 Observable의 스트림은 리스트로 모아지며 다시 리스트는 Observable
     * 클래스가 제공하는 정적 팩토리 메소드 merge로 전달한다. 이는 Observable의 Iterable을 인수로 받아 마치 한 개의 Observable 처럼 동작하도록 결과를
     * 합친다.
     */
        System.out.println("\n------- RxJava merge --------");
        Observable<TempInfo> merge = RxTemperature.getCelsiusTemperatureMerge("NewYork", "Chicago", "San Francisco");
        merge.blockingSubscribe( new TempObserver() );
    /**
     * 각 도시에서 온도를 가져오다 에러가 발생하기 전까지 온도를 출력한다.
     */
    }
}
