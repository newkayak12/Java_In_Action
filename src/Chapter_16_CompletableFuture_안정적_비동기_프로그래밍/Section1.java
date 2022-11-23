package Chapter_16_CompletableFuture_안정적_비동기_프로그래밍;

import Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_2.Shop;
import Chapter_16_CompletableFuture_안정적_비동기_프로그래밍.test.exam_16_2.Shops;

import java.util.ArrayList;
import java.util.List;
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
     *      지금까지 CompletableFuture을 직접 만들었다. 하지만 조금 더 간단하게 CompletableFuture을 만드는 방법이 있다. 예를 들어 getPriceAsync
     *      를 더 간단하게 만들 수 있다.
     *
     *              test.exam_16_2.Shop.getPriceAsyncLambda()
     *
     *      supplyAsync 메소드는 supplier를 인수로 받아서 CompletableFuture를 반환한다. CompletableFuture는 Supplier를 실행해서 비동기
     *      적으로 결과를 결과를 생성한다. ForkJoinPool의 Executor 중 하나가 Supplier를 실행할 것이다. 하지만 두 번쨰 인수를 받는 오버로드 버전의
     *      supplyAsync 메소드를 이용해서 다른 Executor를 지정할 수 있다. 결국 모든 다른 CompletableFuture의 팩토리 메소드에 Executor를
     *      선택적으로 전달할 수 있다.
     *
     *      지금부터는 Shop 클래스에서 구현할 API를 제어할 권한이 없다는 설정 아래서 모든 API는 동기 방식의 블록 메소드라고 가정할 것이다. 실제로
     *      몇몇 서비스에서 제공하는 HTTP API는 이와 같은 방식으로 동작한다. 블록 메소드를 사용할 수밖에 없는 상황에서 비동기적으로 블록을 최소한으로 해서
     *      애플리케이션의 성능을 높일 방법을 강구해보자.
     *
     *
     *                      > 16.3 비블록 코드 만들기
     *
     *      우선 아래와 같은 상점 리스트가 있다고 가정해보자.
     *
     *              test.exam_16_2.Shops
     *
     *      그리고 제품명을 입력하면 상점 이름과 문자열 정보를 제공하는 List를 반환하는 메소드를 구현해야한다.
     *
     *              test.exam_16_2.Shops.findPrices()
     *
     *      이러면 원하는 제품의 가격을 검색할 수 있다. 또한 요소를 찾는 시간도 확인할 수 있다.
     */
    System.out.println("\n\nSingle ----------");
    long startTime = System.nanoTime();
    System.out.println(new Shops().findPrices("myPhone27S"));
    long durationTime = ((System.nanoTime() - startTime) / 1_000_000);
    System.out.println("Done in "+ durationTime + " ms");
    /**
     *
     *      네 개의 상점을 검색하는 동안 대기 시간이 1초가 있으므로 결과적으로 4초 이상의 시간이 걸린다.
     *
     *
     *                  > 16.3.1 병렬 스트림으로 병렬화 하기
     *
     *                  test.exam_16_2.Shops.findPricesParallelize()
     *
     *      위와 같이 코드를 수정하고 실행해보자.
     */
    System.out.println("\n\nParallelize ----------");
    startTime = System.nanoTime();
    System.out.println(new Shops().findPricesParallelize("myPhone27S"));
    durationTime = ((System.nanoTime() - startTime) / 1_000_000);
    System.out.println("Done in "+ durationTime + " ms");
    /**
     *      이러면 병렬로 진행되므로 1초 남짓으로 줄어든다. 이를 더 개선할 방법은 없을까? CompletableFuture 기능을 활용해서 findPrices 메소드의
     *      동기 호출을 비동기로 바꿔보자.
     *
     *
     *                  > 16.3.2 CompletableFuture로 비동기 호출 구현하기
     *      팩토리 메소드 supplyAsync로 CompletableFuture을 만들 수 있다. 이를 활용해보자.
     *
     *
     *                  test.exam_16_2.Shops.findPricesCompletableFuture()
     *
     *      위 코드로 CompletableFuture를 포함하는 리스트 List<CompletableFuture<String>>를 얻을 수 있다. 리스트의 CompletableFuture는
     *      각각 계산 결과가 끝난 상점의 이름 문자열을 포함한다. 우리가 재구현한 findPrices 메소드의 반환 형식은 List<String>이므로 모든 CompleteFuture
     *      의 동작이 완료되고 결과를 추출한 다음에 리스트를 반환해야한다.
     *
     *      두 번째 map 연산을 List<CompletableFuture<String>>에 적용할 수 있다. 즉, 리스트의 모든 CompletableFuture에 join을 호출해서
     *      모든 동작이 끝나기를 기다린다. CompletableFuture 클래스의 join 메소드는 Future 인터페스의 get 메소드와 같은 의미를 갖는다. 다안
     *      join은 아무 예외도 발생시키지 않는다는 점이 다르다. 따라서 두 번쨰 map의 람다 표현식을 try/catch 감쌀 필요가 없다.
     *
     *      여기서 주목할 것은 map 연산을 하나의 스트림 처리 파이프라인으로 처리하지 않고 두 개의 스트림 파이프라인으로 처리했다는 점이다. 스트림 연산은
     *      lazy 특성이 있으므로 하나의 파이프라인으로 연산을 처리했다면 모든 가격 정보 요청 동작이 동기적, 순차적으로 이뤄지게 된다.CompletableFuture로
     *      각 상점의 정보를 요청할 떄 기존 요청 작업이 완료되어야 join이 결과를 반환하면서 다음 상점으로 요청할 수 있기 때문이다. 이러면 이전 요청의
     *      처리가 완전히 끝난 다음에 새로 만든 CompletableFuture가 처리된다.
     *
     *      반면 map 연산을 둘로 나누면 CompletableFuture를 리스트로 모은 다음에 다른 작업과 독립적으로 각자의 작업을 수행하는 모습을 보여준다.
     */
    System.out.println("\n\nCompletableFuture ----------");
    startTime = System.nanoTime();
    System.out.println(new Shops().findPriceCompletableFuture("myPhone27S"));
    durationTime = ((System.nanoTime() - startTime) / 1_000_000);
    System.out.println("Done in "+ durationTime + " ms");
    /**
     *      결과를 확인하면 많이 줄었지만 만족할 정도는 아니다.
     *
     *
     *                  > 16.3.3 더 확장성이 좋은 해결 방법
     *      병렬 스트림 버전의 코드는 네 개의 상점에 하나의 쓰레드를 할당해서 네 개의 작업을 병렬로 수행하면서 검색 시간을 최소화 할 수 있었다. 만약
     *      다섯 개의 상점으로 늘어난다면 어떻게 될까? 순차에서는 1초 정도 추가로 딜레이 된다. 병렬 스트림 버전은 (4 쓰레드라는 가정 아래)
     *      네 개 중 하나가 끝다면 그 다음 다섯 번째를 찾는다.
     *
     *      CompletableFuture는 병렬 스트림보다 약간 더 빠르다. 수를 늘려도 RunTime.getRuntime().availableProcessors()가 반환하는
     *      쓰레드 수에 기반해서 비슷한 결과를 낸다. 그러나 CompletableFuture는 병렬 스트림 버전에 비해 작업에 이용할 수 있는 다양한 Executor를
     *      지정할 수 있다는 장점이 있다. 따라서 Executor로  쓰레드 풀의 크기를 조절하는 등 애플리케이션에 맞는 최적화된 설정을 만들 수 있다.
     *
     *
     *                  > 16.3.4 커스텀 Executor 사용하기
     *      우리 애플리케이션이 실제로 필요한 작업량을 고려한 풀에서 관리하는 쓰레드 수에 맞게 Executor를 만들 수 있으면 좋을 것이다. 풀에서 관리하는
     *      쓰레드 수를 결정하는 방법은 무엇일까?
     *
     *
     *                  {
     *                                              쓰레드 풀 크기 조절
     *                       쓰레드 풀이 너무 크면 CPU, 메모리 자원을 서로 경쟁하느라 시간을 낭비할 수 있다. 반면 쓰레드 풀이 너무 작으면 CPU의
     *                       일부 코어는 활용되지 않을 수 있다. 브라이언 게츠는 아래의 공식으로 CPU 활용 비율을 계산할 수 있다고 제한한다.
     *
     *
     *                                          N(thread) = N(cpu) * U(cpu) * ( 1 + W/C)
     *
     *                            * N(cpu): Runtime.getRuntime().availableProcessors()가 반환하는 코어 수
     *                            * U(cpu): 0 과 1 사이의 값을 갖는 CPU 활용 비율
     *                            * W/C는 대기 시간과 계산 시간의 비율
     *
     *                  }
     *
     *      우리 애플리케이션은 상점의 응답을 대략 99퍼센트의 시간만큼 기다리므로 W/C 비율을 100으로 간주할 수 있다. 즉, 대상 CPU 활용률이 100퍼센트라면
     *      400쓰레드를 갖는 풀을 만들어야 함을 의미한다. 하지만 상점 수보다 많은 쓰레드를 가지고 있어봐야 사용할 가능성이 전혀 없으므로 상점 수보다 많은
     *      쓰레드를 갖는 것은 낭비이다. 따라서 한 상점에 하나의 쓰레드가 할당될 수 있도록 Executor를 설정한다. 쓰레드 수가 너무 많으면 오히려 서버가
     *      크래시될 수 있다. 하나의 Executor에서 사용할 쓰레드의 최대 개수는 100이하로 하는 것이 좋다.
     *
     *
     *          test.exam_16_2.Shops.ExecutorService
     *
     *      우리가 만드는 풀은 데몬 쓰레드를 포함한다. 자바에서 일반 쓰레드가 실행 중이면 프로그램이 종료되지 않는다. 이벤트를 한없이 기다리면서 종료되지
     *      않는 일반 쓰레드가 있다면 문제가 될 수 있다. 반면 데몬 쓰레드는 자바 프로그램이 종료될 때 강제로 실행이 종료될 수 있다. 두 쓰레드의 성능은
     *      같다. 이제 새로운 ExecutorService를 팩토리 메소드  supplyAsync의 두 번쨰 인수로 전달할 수 있다.
     *
     *      이렇게 하면 애플리케이션의 특성에 맞는 Executor를 만들어 사용할 수 있다.
     *
     *
     *
     *              {
     *
     *                                                  스트림 병렬화와 CompletableFuture 병렬화
     *
     *                    지금까지 컬렉션 계산을 병렬화하는 두 가지 방법을 봤다 하나는 병렬 스트림으로 변환해서 컬렉션을 처리하는 방법이고 다른 하나는
     *                    컬렉션을 반복하면서 CompletableFuture 내부 연산으로 만드는 방법이다. CompletableFuture를 이용하면 전체적인 계산이
     *                    블록되지 않도록 쓰레드 풀의 크기를 조절할 수 있다. 아래를 참고하면 어떤 병렬화 기법을 사용할 것인지 선택하는 데 도움이 된다.
     *
     *                      1. I/O가 포함되지 않은 계산 중심의 동작을 실행할 때는 스트림 인터페이스가 가장 구현하기 간단하며 효율적일 수 있다.
     *                      (모든 쓰레드가 계산 작업을 수행하는 상황에서 프로세서 코어 수 이상의 쓰레드를 가질 필요가 없어진다.)
     *
     *                      2. 반면 작업이 I/O를 기다리는 작업을 병렬로 실행할 떄는 CompletableFuture가 더 많은 유연성을 제공하며 대기/시간
     *                      (W/C)의 비율에 적합한 쓰레드 수를 설정할 수 있다. 특히 스트림의 lazy 특성 떄문에 스트림에서 I/O를 실제로 언제 처리할지
     *                      예측 하기 어렵다는 문제도 있다.
     *              }
     *
     *
     *
     *                      > 16.4 비동기 작업 파이프 라인 만들기(선언형으로)
     *       우리와 계약을 맺은 모든 상점이 하나의 할인 서비스를 제공하기로 했다고 가정하자. 할인 서비스에서는 서로 다른 할인율을 제공하는 다섯 가지 코드를
     *       제공한다.
     *
     *              test.exam_16_4.Discount.Code
     *
     *       또한 상점에서 getPrice 메소드의 결과 형식도 바꾸기로 했다. 이제 getPrice는 ShopName:price:DiscountCode 형식의 문자열을 반환한다.
     *
     *              test.exam_16_2.Shops.getPriceDiscount()
     *
     *
     *
     *                      > 16.4.1 할인 서비스 구현
     *        이제 우리 최저 가격 검색 애플리케이션은 여러 상점에서 가격 정보를 얻어오고, 결과 문자열을 파싱하고, 할인 서버에 질의를 보낼 준비가 되었다.
     *        할인 서버에서 할인율을 확인해서 최종 가격을 계산할 수 있다. 상점에서 제공한 문자열 파싱은 Quote 클래스로 캡슐화할 수 있다.
     *
     *              test.exam_16_4.Quote
     *
     *        상점에서 얻은 문자열을 정적 팩토리 메소드 parse로 넘기면 상점이름, 할인전 가격, 할인된 가격을 포함하는 Quote가  생성된다. 그러면 아래와 같이
     *        할인된 가격 문자열을 반환하는 applyDiscount가 제공된다.
     *
     *
     *                      > 16.4.2 할인 서비스 사용
     *
     *              test.exam_16_2.Shops.findPriceDiscount()
     *        세 개의 map 연산을 상점 스트림에 파이프라인으로 연결해서 원하는 결과를 얻었다.
     *
     *          1. 첫 번째 연산에서는 각 상점을 요청한 제품의 가격과 할인 코드로 변환한다.
     *          2. 두 번째 연산에서는 이들 문자열을 파싱해서 Quote 객체로 만든다.
     *          3. 세 번쨰 연산에서는 원격 Discount 서비스에 접근해서 최종 가격을 계산하고 가격에 대응하는 상점 이름을 포함하는 문자열을 반환한다.
     *
     *
     *
     */
    System.out.println("\n\nDiscount ----------");
    startTime = System.nanoTime();
    System.out.println(new Shops().findPriceDiscount("myPhone27S"));
    durationTime = ((System.nanoTime() - startTime) / 1_000_000);
    System.out.println("Done in "+ durationTime + " ms");
    /**
     *      예상대로 순차로 상점에 요청하느라 4초가 소모됐고 각 상점에서 반환한 가격 정보에 할인 코드를 적용할 수 있도록 할인 서비스가 적용되면서 4초가
     *      소모됐다. 병렬 스트림을 사용하면 성능을 쉽게 개선할 수 있다는 것은 이미 확인했다. 하지만 병렬 스트림은 쓰레드 풀의 크기가 고정되어 있어서
     *      상점 수가 늘어났을 때, 검색 대상이 확장됐을 때 유연하게 대응할 수 없다는 사실도 확인했다. 따라서 CompletableFuture에서 수행하는
     *      태스크를 설정할 수 있는 커스텀 Executor를 정의함으로써 자원 사용률을 극대화할 수 있다.
     *
     *
     *
     *                      > 16.4.3 동기 작업과 비동기 작업 조합하기
     *
     *              test.exam_16_2.Shops.findPriceCombineBlockAndNoneBlock()
     *
     *      세 가지 변환 과정이 있다. 다만 이번에는 CompletableFuture 클래스를 이용해서 비동기로 만들어야 한다.
     *
     *          1. 가격 정보 얻기
     *          첫 번째 연산은 팩토리 메소드 supplyAsync에 람다 표현식을 전달해서 비동기적으로 상점에서 정보를 조회했다. 첫 번째 변환의
     *          결과는 Stream<CompletableFuture<String>>이다. 각 CompletableFuture는 작업이 끝났을 떄 해당 상점에서 반환하는 문자열
     *          정보를 포함한다.
     *
     *          2. Quote 파싱
     *          두 번째는 첫 번쨰 결과 문자열을 Quote로 변환한다. 파싱 동작에서는 원격 서비스나  I/O가 없으므로 즉시 지연 없이 동작을 할 수 있다. 따라서
     *          첫 번쨰 과정에서 생성된 CompletableFuture에 thenApply 메소드를 호출한 다음에 문자열을 Quote 인스턴스로 변환하는 Future으로
     *          전달한다.
     *          thenApply 메소드는 CompletableFuture가 끝날 때까지 블록하지 않는다는 점을 주의해야한다. 즉, CompletableFuture가 동작을
     *          완전히 완료한 다음 thenApply 메소드로 전달된 람다 표현식을 적용할 수 있다. 따라서 CompletableFuture<String>을 Completable<Quote>
     *          로 변환할 것이다. 이는 마치 CompletableFuture의 결과물로 무엇을 할지 지정하는 것과 같다. 스트림 파이프라인에도 같은 기능이 존재했다.
     *
     *          3. CompletableFuture를 조합해서 할인된 가격 계산하기
     *          세 번째 map 연산에서는 상점에서 받은 할인전 가격에 Discount로 할인률을 적용해야한다. 람다 표현식으로 이 동작을 팩토리 supplyAsync에
     *          전달할 수 있다. 그러면 다른 CompletableFuture가 반환된다. 결국 두 가지 CompletableFuture로 이뤄진 연쇄적으로 수행되는 두 개의
     *          비동기 동작을 만들 수 있다.
     *
     *              1. 상점에서 가격 정보 얻어와 Quote로 변환
     *              2. 변환된 Quote를 Discount 서비스로 전달해서 할인된 최종 가격 획득하기
     *
     *          자바 8의 CompletableFuture API는 이와 같이 두 비동기 연산을 파이프라인으로 만들수 있도록 thenCompose 메소드를 제공한다.
     *          thenCompose 메소드는 첫 번째 연산의 결과를 두 번째 연산으로 전달한다. 즉, 첫 번째 CompletableFuture에 thenCompose  메소드를
     *          호출하고 Function에 넘겨주는 식으로 두 CompletableFuture를 조합할 수 있다. Function은 첫 번쨰 CompletableFunction
     *          반환 결과를 인수로 받고 두 번쨰 CompletableFuture를 반환하는데, 두 번쨰 CompletableFuture는 첫 번째 CompletableFuture의
     *          결과를 계산의 입력으로 사용한다. 따라서 Future가 여러 상점에서 Quote를 얻는 동안 메인 쓰레드는 UI 이벤트에 반응하는 등 유용한
     *          작업을 수행할 수 있다.
     *
     *          세 개의 map 연산 결과 스트림의 요소를 리스트로 수집하면 List<CompletableFuture<String>> 형식의 자료를 얻을 수 있다.
     *          마지막으로 CompletableFuture가 완료되기를 기다렸다가 join으로 값을 추출할 수 있다.
     *
     *          CompletableFuture 클래스의 다른 메소드처럼 thenCompose 메소드도 Async로 끝나는 메소드가 존재한다. Async로 끝나지 않은 메소드
     *          는 이전 작업을 수행한 쓰레드와 같은 쓰레드에서 작업을 실행함을 의미하며 Async로 끝나는 메소드는 다음 작업이 다른 쓰레드에서 실행되도록
     *          쓰레드 풀로 작업을 제출한다.
     *          여기서 두 번쨰 CompletableFuture의 결과는 첫 번쨰 CompletableFuture에 의존하므로 두 CompletableFuture를 하나로 조합하든
     *          Async 버전의 메소드를 쓰든 최종 결과나 개괄적인 실행 시간에는 영향이 없다. 오히려 쓰레드 전환 오버헤드가 적게 발생하면서
     *          효율성이 조금 더 좋은 thenCompose를 쓰는 것이 이 경우는 좋다.
     *
     */
    System.out.println("\n\nasync + sync ----------");
    startTime = System.nanoTime();
    System.out.println(new Shops().findPriceCombineBlockAndNoneBlock("myPhone27S"));
    durationTime = ((System.nanoTime() - startTime) / 1_000_000);
    System.out.println("Done in "+ durationTime + " ms");
    /**
     *                      > 16.4.4 독립 CompletableFuture와 비독립 CompletableFuture 합치기
     *
     *          exam_16_2.Shops.findPriceCombineBlockAndNoneBlock()의 첫 번째 CompleteableFuture에 thenCompose 메소드를 실행한
     *          다음에 실행 결과를 첫 번째 실행 결과를 입력으로 받는 두 번째 CompletableFuture을 전달했다. 실전에서는 독립적으로 실행된 Completable
     *          Future의 결과를 합쳐야 하는 상황이 종종 발생한다. 물론 첫 번째 CompletableFuture의 동작 완료와 관계없이 두 번째 CompleteFuture
     *          를 실행할 수 있어야 한다.
     *
     *          이런 상황에서는 thenCombine 메소드를 사용한다. thenCombine 메소드는 BiFunction을 두 번째 인수로 받는다. BiFunction은 두 개의
     *          CompletableFuture의 결과를 어떻게 합칠지 정의한다. thenCompose와 마찬가지로 thenCombine 메소드에도 Aysnc 버전이 존재한다.
     *          thenCombineAysnc 메소드에는 BiFunction이  정의하는 조합 동작이 쓰레드 풀로 제출되면서 별도의 태스크에 비동기적으로 수행된다.
     *
     *          예제에서는 한 온라인 상점이 EUR 가격 정보를 제공하는데, 고객에게는 항상 USD를 보여줘야한다. 우리는 주어진 상품의 가격을 상점에 요청하는
     *          한편 원격 환율 교환 서비스를 이용해서 유로와 달러의 현재 환율을 비동기적으로 요청해야한다. 두 가지 데이터를 얻었다면 환율을 곱해서 결과를
     *          합칠 수 있다. 이렇게 해서 두 CompletableFuture의 결과가 생성되고 BiFunction으로 합쳐진 다음에 세 번쨰 CompletableFuture를
     *          얻을 수 있다.
     *
     *                  Future<Double> futurePriceInUSD = CompletableFuture.supplyAsync( () -> shop.getPrice(product))
     *                                         .thenCombine( CompletableFuture.supplyAsync( () -> exchangeService.getRate(Money.EUR, Money.USD)),
     *                                           (price, rate) -> price * rate ));
     *
     *
     *          여기서 합치는 연산은 단순 곱연산이므로 별도의 태스트에서 수행해서 자원을 낭비할 필요가 없다. 따라서 thenCombineAsync 대신 thenCombine
     *          메소드를 사용한다.
     *
     *
     *
     *                    > 16.4.5 Future의 리플렉션과 CompletableFuture의 리플렉션
     *          위 예시와 전 예시는 자바 8 이전의 Future에 비해 CompletableFuture가 어떤 큰 이점을 제공하는지 명확히 보여준다. CompltableFuture
     *          는 람다 표현식을 사용한다. 이미 살펴본것처럼 람다 덕분에 다양한 동기 태스크, 비동기 태스크를 황용해서 복잡한 연산 수행 방법을 효과적으로 쉽게
     *          정의할 수 있는 선언형 API를 만들 수 있다. 자바 7로 구현하면서 실질적으로 CompletableFuture를 이용했을 때 얻을 수 있는 코드 가독성의
     *          이점이 무엇인지 확인할 수 있다. 아래는 자바 코드이다.
     *
     *                  exam_16_2.Shops.findPriceWithExchangeRate()
     *
     *                  ExecutorService executor = Executors.newCachedThreadPool();
     *                  final Future<Double> futureRate = executor.submit(new Callable<Double>() {
     *                      public Double call() {
     *                          return exchangeService.getRate(Money.EUR, Money.USD);
     *                      }
     *                  });
     *
     *                  Future<Double> futurePriceInUSD = executor.submit(new Callable<Double>() {
     *                      public Double call() {
     *                          double priceInEUR = shop.getPrice(product);
     *                          return priceInEUR * future.Rate.get();
     *                      }
     *                  }
     *
     *           얼핏보기에는 별 다른 차이가 없어 보인다.
     *
     *
     *
     *                      > 16.4.6 타임아웃 효과적으로 사용하기
     *           16.2.2에서 살펴본 바와 같이 Future의 계산 결과를 읽을 때는 무한정 기다리는 상황이 발생할 수 있으므로 블록을 하지 않는 것이 좋다.
     *           자바 9에서는 CompletableFuture에서 제공하는 몇 가지 기능을 이ㅛㅇㅇ해 이런 문제를 해결할 수 있다. orTimeout 메소드는 지정된
     *           시간이 지난 후에 CompletableFuture을 TimeoutException으로 완료하면서 또 다른 CompletableFuture를 반환할 수 있도록
     *           내부적으로 ScheduledThreadExecutor를 활용한다. 이 메소드를 이용하면 계산 파이프라인을 연결하고 여기서 TimeoutException이
     *           발생했을 때 사용자가 수비게 이해할 수 있는 메시지를 제공할 수 있다.
     *
     *                  exam_16_2.Shops.findPriceWithExchangeRateTimeout()
     *
     *           일시적으로 서비스를 이용할 수 없는 상황에서 꼭 서버에서 얻은 값이 아닌 미리 지정된 값을 사용할 수 있는 상황도 있다. 예를 들어
     *           EUR -> USD가 1초 이내에 완료되지 않았을 때, 그렇다고 전체 계산을 Exception 처리하지 않는 상황이라면 미리 정의한 값으로 연산을 이어
     *           나갈 수도 있다. 자바 9에서 completeOnTimeout 메소드로 이 기능을 수행할 수 있다.
     *
     *
     *             exam_16_2.Shops. findPriceWithExchangeRateCompleteOnTimeout()
     *
     *
     *
     *                  >16.5 CompletableFuture의 종료에 대응하는 방법
     *           이런 상황에서 문제가 하나 더 남아 있다. 얼마나 지연될지 모른다는 것이 문제이다. 서버 부하에서 네트워크 문제에 이르기까지 다양한 지연
     *           요소가 있기 때문이다. 또한 질의당 얼마를 더 지불하느냐에 따라 우리 애플리케이션이 제공하는 서비스의 질이 달라질 수 있다.
     *
     *           여러 상점에 정볼르 제공했을 때 몇몇 상점은 다른 상점보다 먼저 결과를 제공할 가능성이 크다. 0.5~2.5 사이의 임의 지연으로 이를 시뮬레이션
     *           해보자
     *
     *                      exam_16_2.Shops.findPricesStream()
     *
     *
     */
    Shops shops = new Shops();
    shops.findPricesStream("myPhone").map(f -> f.thenAccept(System.out::println));
    /**
     *          thenCompose, thenCombine과 마찬가지로 thenAccept에도 thenAcceptAsync가 있다. thenAcceptAsync는 CompletableFuture
     *          가 완료된 쓰레드가 아니라 새로운 쓰레드를 이용해서 Consumer를 실행한다. 불필요한 콘텍스트 변경은 피하는 동시에 CompletableFuture가
     *          완료되는 즉시 응답하는 것이 좋으므로 thenAcceptAsync를 사용하지 않았다.
     *
     *          thenAccept는 CompletableFuture가 생성한 결과를 어떻게 소비할지 미리 지정했으므로 CompletableFuture<Void>를 반환한다.
     *          따라서 네 번째 map은 <CompletableFuture<Void>>를 반환한다. 이제 <CompletableFuture<Void>>가 동작을 끝낼 떄까지 딱히 할 수
     *          있는 일이 없다. 또한 느린 상점에서 응답을 받아서 반환된 가격을 출력할 기회를 제공하고 싶다고 가정하자 그러기 위해서는 아래 코드에서와 같이
     *          CompletableFuture<Void>를 배열로 추가하고 실행 결과를 기다려야 한다.
     *
     */
    CompletableFuture[] futures = shops.findPricesStream("myPhone").map(f -> f.thenAccept(System.out::println))
            .toArray(size -> new CompletableFuture[size]);
    CompletableFuture.allOf(futures).join();
    /**
     *          팩토리 메소드 allOf는 CompletableFuture 배열을 입력으로 받아 CompletableFuture<Void>를 반환한다. 전달된 모든 CompletableFuture
     *          가 완료되어야 CompletableFuture<Void>가 완료된다. 따라서 allOf 메소드가 반환하는 CompletableFuture에 join을 호출하면
     *          원래 스트림의 모든 CompletableFuture의 실행 완료를 기다릴 수 있다. 이를 이용해서 최저가격 검색 애플리케이션은 '모든 상점이 결과를
     *          반환했거나 타임아웃되었음' 같은 메시지를 사용자에게 보여줌으로써 사용자는 추가로 가격 정보를 기다리지 않아도 된다는 사실을 보여줄 수 있다.
     *
     *          반면 배열의 CompletableFuture 중 하나의 작업이 끝나길 기다리는 상황도 있을 수 있다. 예를 들어 같은 서버로 동시에 접근 했을 때
     *          둘 중 하나의 결과만 있으면 되는 경우이다. 이때 팩토리 메소드 anyOf를 사용한다. anyOf 메소드는 CompletableFuture 배열을 입력으로
     *          받아서 CompletableFuture<Object>를 반환한다. CompletableFuture<Object>는 처음으로 완료한 CompletableFuture의 값으로
     *          동작을 완료한다.
     *
     *                      > 16.5.2 응용
     *          randomDelay로 임의의 지연으로 원격 서비스를 흉내낼 것이다. 아래의 코드를 실행시키면 지정된 시간에 나타나지 않을 뿐만 아니라 상점 가격
     *          정보가 들어오는 대로 결과가 출력된다.
     */
    System.out.println("\n\n 응용----------------");
    Long start2 = System.nanoTime();
    List<String> result = new ArrayList<>();
    CompletableFuture[] futures2 = shops.findPricesStream("myPhone275").peek(i-> {
                try {
                    result.add(i.get());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).map(f -> f.thenAccept(
            s -> System.out.println(s + "(done in "+((System.nanoTime() - start2) / 1_000_00)+" ms)")))
            .toArray(size -> new CompletableFuture[size]);
    CompletableFuture.allOf(futures2).join();
    System.out.println(result);
    System.out.println("All shops now responded in "+ ((System.nanoTime() - start2) / 1_000_000)+" ms");
    }
    public static void doSomethingElse() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
