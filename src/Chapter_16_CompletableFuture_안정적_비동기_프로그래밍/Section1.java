package Chapter_16_CompletableFuture_안정적_비동기_프로그래밍;

import Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_2.Shop;

import java.util.concurrent.*;

public class Section1 {
    public static Double doSomeLongComputation() {
        return 1.0;
    }

    public static void main(String[] args) {
        /**
         *    이전 장에서는 여러 다중 처리 리소스를 고려하여 프로그램이 이들 자원을 효율적으로 활용할 수 있는 동시성 기법을 봤다. 병렬 스트림과 포크/조인
         *    을 이용해서 컬렉션을 반복하거나 분할 - 정복 알고리즘을 활용하는 프로그램에서 높은 수준의 병렬을 적용할 수 있음을 확인했다. 이제 볼 내용은
         *    CompletableFuture이 비동기 프로그램에 얼마나 큰 도움을 주는지이다.
         *
         *
         *
         *
         *              > 16.1 Future의 단순 사용
         *    자바 5부터 미래의 어느 시점에 결과를 얻는 모델을 활용할 수 있도록 Future 인터페이스를 제공하고 있다. 비동기 계산을 모델링하는데 Future를
         *    이용할 수 있으며, Future는 계산이 끝났을 때 결과에 접근할 수 있는 참조를 제공한다. 시간이 걸릴 수 있는 작업을 Future 내부로 설정하면
         *    호출자 쓰레드가 결과를 기다리는 동안 다른 유용한 작업을 할 수 있다. 비유하면 세탁소 주인은 드라이클리닝이 언제 끝날지 적인 영수증(Future)
         *    을 줄 것이다. 드라이클리닝 하는 동안 우리가 하고 싶은 일을 할 수 있다. Future은 저수준의 쓰레드에 비해 직관적으로 이해하기 쉽다는 장점이 있다.
         *    Future를 이용하려면 시간이 오래 걸리는 작업을 Callable 객체 내부로 감싼 다음에 ExecutorService에 제출해야 한다.
         */
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return doSomeLongComputation();
            }
        });
        doSomeLongComputation();
        try {
            Double result = future.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        /**
         * 위 예에서 보여주는 것처럼 이와 같은 유형의 프로그래밍에서는 ExecutorService에서 제공하는 쓰레드가 시간이 오래 걸리는 작업을 처리하는 동안
         * 우리 쓰레드로 다른 작업을 동시에 실행할 수 있다. 다른 작업을 처리하다가 시간이 오래 걸리는 작업 결과가 필요한 시점이 됐을 때 Future의 get
         * 으로 꺼내 올 수 있다. get을 호출했을 때 이미 계산이 끝났다면 상관없지만 아니라면 쓰레드를 블록하고 기다려버린다. 문제는 만약 Future가
         * 영영 끝을 맺지 못한다면? 그래서 위 get처럼 timeout을 주는 것이 좋다.
         *
         *
         *              > 16.1.1 Future 제한
         *  첫 번째로 살펴볼 간단한 예제에서는 Future 인터페이스가 비동기 계산이 끝났는지 확인할 수 있는 isDone 메소드, 계산이 끝나길 기다리는 메소드,
         *  결과 회수 메소드 등을 제공함을 보여준다. 하지만 이들 메소드만으로는 충분치 않다. 예를 들어 Future의 결과가 있을 때 이들의 의존성을 표현하기가
         *  어렵다. '오래 걸리는 A가 끝나면 그 결과를 B로 전달하시오. 그리고 B결과가 나오면 다른 질의와 B를 조합하시오.'와 같은 요구사항을 쉽게
         *  구현할 수 있어야 한다. Future로 이와 같은 동작을 구현하는 것은 쉽지 않다. 아래와 같은 선언형 기능이 있다면 유용할 것이다.
         *
         *      {
         *           ***  프로그램이 어떤 방법으로 해야 하는지를 나타내기보다 무엇과 같은지를 설명하는 경우에 "선언형"이라고 한다.
         *      }
         *
         *      1. 두 개의 비동기 계산 결과를 하나로 합친다. 두 가지 계산 결과는 서로 독립적일 수 있으며 또는 두 번쨰 결과가 첫 번째 결과에 의존할 수 있다.
         *      2. Future 집합이 실행하는 모든 태스크의 완료를 기다린다.
         *      3. Future 집합에서 가장 빨리 완료되는 태스크를 기다렸다가 결과를 얻는다.
         *      4. 프로그램적으로 Future를 완료시킨다.
         *      5. Future 완료 동작에 반응한다. (결과를 기다리면서 블록되지 않고 결과가 준비되었다는 알람을 받으면 Future로 원하는 동작을 한다.)
         *
         *  Stream과 CompletableFuture는 비슷한 패턴, 즉 람다와 파이프라이닝을 활용한다. 따라서 Future와 CompletableFuture 관계를 Collection과
         *  Stream에 비유할 수 있다.
         *
         *
         *             > 16.1.2  CompletableFuture로 비동기 애플리케이션 만들기
         *  어떤 제품이나 서비스를 이용해야한다고 가정했을 때, 저렴한 가격을 제시하는 상점을 찾는 애플리케이션을 완성하는 예제를 이용해서 CompletableFuture
         *  를 살펴보자.
         *
         *      1. 고객에 비동기 API 호출
         *      2. 동기 API를 사용해야할 때 코드를 비블록으로 만드는 방법을 익힌다. 두 개의 비동기 동작을 파이프라인으로 만드는 방법과 두 개의 동작
         *      결과를 하나의 비동기 계산으로 합치는 방법을 본다. 예를 들어 온라인 상점에서 할인코드를 반환한다고 할때 다른 원격 할인 서비스에 접근해서
         *      할인 코드에 해당하는 할인율을 찾아야한다.
         *      3. 비동기 동작의 완료에 대응하는 방법을 배운다. 모든 상점에서 가격을 받을 때까지 기다리는 것이 아니라 상점에서 받을 때마다 즉시
         *      최저 가격을 찾는 애플리케이션을 갱신하는 방법을 배운다.
         *
         *
         *              {
         *                                                  동기 API와 비동기 API
         *                   전통적인 동기 API에서는 메소드를 호출한 다음에 메소드가 계산을 완료할 떄까지 기다렸다가 메소드가 반환되면 호출자는 반환된
         *                   값으로 계속 다른 동작을 수행한다. 호출자와 피호출자가 각가 다른 쓰레드에서 실행되는 상황이었더라도 호출자는 피호출자의
         *                   동작 완료를 기다렸을 것이다. 이러한 동기 호출을 블록 호출이라고 한다.
         *
         *                   반면 비동기 API에서는 메소드가 즉시 반환되며 끝내지 못한 나머지 작업을 호출자 쓰레드와 동기적으로 실행될 수 있도록 다른 쓰레드에
         *                   할당한다. 이와 같은 비동기 APi를 사용하는 상황을 논블로킹 호출이라고 한다. 다른 쓰레드에 할당된 나머지 계산 결과는 콜백
         *                   메소드를 호출해서 전달하거나 '계산 결과가 끝날 때까지 기다림' 메소드를 추가로 호출하면서 전달된다. 주로 I/O 시스템
         *                   프로그래밍에서 이와 같은 방식으로 동작을 수행한다. 즉, 계산 동작을 수행하는 동안 비동기적으로 디스크 접근을 수행한다.
         *                   그리고 더 이상 수행할 동작이 없으면 디스크 블록 메모리로 로딩될 때까지 기다린다.
         *              }
         *
         *
         *                  > 16.2 비동기 API 구현
         *
         *              ~.test.exam_16_2.Shop
         *
         *      getPrice 메소드는 상점의 DB를 이용해서 가격 정보를 얻는 동시에 다른 외부 서비스에도 접근할 것이다. 우리는 API 호출하기 부담스러우므로
         *      delay라는 메소드로 대체한다. 1초 지연하는 메소드이다. 또한 임의의 계산 값을 계산해서 반환한다. 사용자가 이 API를 호출하면
         *      비동기 동작이 완료될 때까지 1초 동안 블록된다. 모든 가격을 검색해야하므로 블록은 바람직하지 않다. 뒤에서 이를 비동기로 소화해낼 것이다.
         *
         *
         *                  > 16.2.1 동기 메소드를 비동기 메소드로 변환
         *      동기 메소드 getPrice를 비동기 메소드로 변환하려면 Future<Double>로 반환하도록 변경해야한다. 자바 5부터 java.util.concurrent.Future
         *      인터페이스를 지원한다. 간단히 말해 Future은 결과값의 핸들일 뿐이며 계산이 완료되면 get 메소드를 결과를 얻을 수 있다.
         *
         *              ~.test.exam_16_2.Shop
         *
         *      비동기 계산과 완료 결과를 포함하는 CompletableFuture 인스턴스를 만들었다. 그리고 실제 가격을 계산할 다른 쓰레드를 만든 다음에 오래 걸리는
         *      계산 결과를 기다리지 않고 결과를 포함할 Future 인스턴스를 바로 반환했다. 요청한 제품의 가격 정보가 도착하면 complete 메소드를 이용해서
         *      CompletableFuture를 종료할 수 있다.
         */

        Shop shop = new Shop("BestShop");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " ms");

        doSomethingElse(); //다른작업

        try {
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " ms");
    }

    /**
     *       위 코드에서 확인할 수 있는 것처럼 클라이언트는 특정 제품의 가격 정보를 상점에 요청하고 상점은 비동기 API를 제공하므로 즉시 Future을
     *       반환한다. 클라이언트는 반환된 Future를 이용해서 나중에 결과를 얻을 수 있다. 그 사이 클라이언트는 다른 상점에 가격 정보를 요청하는 등
     *       첫 번째 상점의 결과를 기다리면서 대기하지 않고 다른 작업을 할 수 있다. 나중에 클라이언트가 특별히 할 일이 없다면 future의 get을
     *       호출한다. 이떄 future가 결과를 가지고 있다면 바로 실행되고 없다면 블록된다.
     *
     *
     *
     *
     *                      > 16.2.2 에러 처리
     *
     *      지금까지 개발한 코드가 에러가 없으리라는 보장이 없다. 물론 예외가 발생하면 해당 쓰레드만 영향을 받는다. 즉 에러가 발생해도 가격 계산은 계속
     *      되며 일의 순서가 꼬인다. 결과적으로 클라이언트는 get을 받을 때까지 기다릴 수도 있다.
     *      클라이언트는 타임아웃을 받는 get의 오버로드 메소드를 만들어 해결할 수 있다. 이처럼 블록 문제가 발생할 수 있다면 타임아웃을 활용하는 것이
     *      좋다. 그래야 문제가 발생했을 때 클라이언트가 영원히 블록되지 않고 타임아웃이 되면 TimeoutException을 받을 수 있다. 이떄 제품 가격 계산에
     *      왜 에러가 났는지 알 수 있는 방법이 없다. 따라서 CompleteExceptionally 메소드를 활용해서 CompletableFuture 내부에서 발생한
     *      예외를 클라이언트로 전달해야한다.
     *
     *      이러면 클라언트는 가격 계산 메소드에서 발생한 예외 파라미터를 포함하는 ExecutionException을 받게 된다.
     *
     *
     *                      > 팩토리 메소드 supplyAsync로 CompletableFuture 만들기
     *
     *
     */

    public static void doSomethingElse() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
