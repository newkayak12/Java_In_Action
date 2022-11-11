package Chapter_15_CompetableFuture와_리액티브_프로그래밍_컨셉의_기초;

public class Section1 {
    public static void main(String[] args) {
        /**
         *      병렬성이 아니라 동시성을 필요로하는 상황 즉 조금씩 연관된 작업을 같은 CPU에서 동작하는 것 또는 애플리케이션을 생상성을 극대화할 수 있도록
         *      코어를 놀리지 않는 것이 목표라면, 원격 서비스나 데이터베이스 결과를 기다리는 쓰레드를 블록해서 연산 자원을 낭비하는 것을 막아야한다.
         *
         *      자바는 이런 환경에서 사용할 수 있는 두 가지 주요 도구를 제공한다. Future 인터페이스로 자바 8의 CompletableFuture 구현은 간단하고
         *      효율적인 문제 해결사이다. 여기서 동시성과 병렬성의 차이점이 확실하지 않을 수 있는데 병렬성은 동시간 대 각기 다른 코어에서 작업을 진행하는 것이고
         *      동시성은 코어의 유휴시간 없이 계속해서 각기 다른 작업을 수행하는 것을 일컫는다.
         *
         *
         *
         *              > 15.1 동시성을 구현하는 자바 지원의 진화
         *      자바의 동시 프로그래밍 지원은 변화에 맞춰 진화됐다. 처음에 자바는 Runnable, Thread를 동기화된 클래스와 메소드를 이용해서 잠궜다.
         *      자바 5는 표현력있는 동시성을 지원하는 특히 쓰레드 실행과 태스크 제출을 분리하는 ExecutorService 인터페이스, 높은 수준의 결과 즉,
         *      Runnable, Thread의 변형을 반환하는 Callable<T>, Future<T>, 제네릭 등을 지원했다. ExecutorServices는 Runnable, Callable
         *      을 모두 실행할 수 있다. 이런 기능들 덕분에 멀티코어 CPU에서 쉽게 병렬 프로그래밍을 구현할 수 있게 됐다.
         *      멀티코어 CPU에서 효과적으로 프로그래밍을 실행할 필요성이 커지면서 이후 자바 버전에서는 개선된 동시성 지원이 추가됐다. 자바 7에서는
         *      분할 정복 알고리즘의 포크/조인 구현을 지원하는  java.util.concurrent.RecursiveTask가 추가됐고 자바 8에서는 스트림, 람다 지원에
         *      기반한 병렬 프로세싱이 추가됐다.
         *      자바는 Future을 조합하는 기능을 추가하면서 동시성을 강화했고, 자바 9에서는 분산 비동기 프로그래밍을 명시적으로 지원한다. 이들 API는
         *      매쉬업 애플리케이션 즉, 다양한 웹 서비스를 이용하고 이들 정보를 실시간으로 조합해 사용자에게 제공하거나 추가 웹서비스를 통해 제공하는
         *      종류의 애플리케이션을 개발하는데 필수적인 기초 모델과 툴킷을 제공한다. 이 과정을 리액티브 프로그래밍이라고 부르며 자바9는 pub-sub 프로토콜
         *      (java.util.concurrent.Flow)로 이를 지원한다. CompletableFuture와 java.util.concurrent.Flow의 궁극적인 목표는
         *      가능한한 동시에 실행할 수 있는 독립적인 태스크를 가능하게 만들면서 멀티코어 또는 여러 기기를 통해 제공되는 병렬성을 쉽게 이용하는 것이다.
         *
         *
         *              > 15.1.1 쓰레드와 높은 수준의 추상화
         *      실제로 네 개의 코어를 가진 CPU에서 이론적으로 프로그램을 네 개의 코어에서 병렬로 실행함으로 실행 속도를 네 배까지 향상시킬 수 있다.
         *      예시로 학생들이 제출한 숫자 1,000,000개를 저장할 배열을 처리하는 예제를 살펴보자.
         *
         *         long sum = 0;
         *           for (int i = 0; i < 1_000_000; i++){
         *               sum += stat[i]
         *          }
         *      이는 단일 쓰레드로 하는 작업이고 한나절 걸릴 것이다. 이를 4 개의 쓰레드를 이용하면 아래와 같다.
         *          long sum0 = 0;
         *           for (int i = 0; i < 250_000; i++){
         *                sum0 += stat[i]
         *           }
         *       이를 네 개의 쓰레드로 구성하고 .start()를 한 뒤 .join()으로 완료될 때까지 기다렸다가 sum = sum0 + ... + sum3으로 결과를
         *       받는다. 이를 각 루프로 처리하는 것은 에러가 발생할 수 있는 구조이다. 또한 스트림으로도 가능하다.
         *
         *          sum = Arrays.stream(stats).parallel().sum()
         *
         *       결론적으로 병렬 스트림 반복은 명시적으로 쓰레드를 사용하는 것에 비해 높은 수준의 개념이라는 것을 알 수 있다. 다시 말해 스트림을 이용해
         *       쓰레드 사용 패턴을 추상화할 수 있다. 스트림으로 추상화하는 것은 패턴 디자인을 적용하는 것과 비슷하지만 대신 쓸모 없는 코드가 라이브러리
         *       내부로 구현되면서 복잡성도 줄어든다는 장점이 더해진다. 자바 7의 java.util.concurrent.RecursiveTask 지원 덕분에 포크/조인
         *       쓰레드 추상화로 분할 정복 알고리즘을 병렬화하면서 멀티코어 머신에서 배열의 합을 효율적으로 계산하는 높은 수준의 방식을 제공하는 방법을
         *       설명한다.
         *       추가적 쓰레드 추상화를 보기 전, ExecutorService 개념과 쓰레드 풀을 보자.
         *
         *
         *              > 15.1.2 Executor와 쓰레드풀
         *       자바 5는 Executor 프레임워크와 쓰레드 풀을 통해 쓰레드를 극한으로 사용하는 프로그래머가 테스크 제출과 실행을 분리할 수 있는 기능을
         *       제공했다.
         *
         *              > 쓰레드의 문제
         *       쓰레드는 운영체제 쓰레드에 접근한다. 운영체제 쓰레드를 만들고 종료하려면 (페이지 테이블과 관련된) 비용을 치러야 하며 운영체제 쓰레드 수는
         *       제한되어 있다. 운영체제가 지원하는 쓰레드 수를 초과해서 사용하면 애플리케이션이 크래시될 수 있다. 기존 쓰레드가 실행되는 상태에서
         *       계속 쓰레드를 만드는 상황이 일어나지 않게 해야한다.(폭증)
         *
         *       보통 운영체제와 자바 쓰레드 개수가 하드웨어 쓰레드 수보다 많으므로 일부 운영 체제 쓰레드가 블록/ 자고 있는 상황에서 모든 하드웨어
         *       쓰레드가 코드를 실행하도록 할당된 상황에 놓을 수 있다.
         *
         *
         *              > 쓰레드 풀의 장점
         *       자바 ExecutorService는 태스크를 제출하고 나중에 결과를 수집할 수 있는 인터페이스를 제공한다. 프로그램은 newFixedThreadPool
         *       같은 팩토리 메소드 중 하나를 이용해서 쓰레드풀을 만들어 사용할 수 있다.
         *
         *       ExecutorService newFixedThreadPool(int nThreads)
         *
         *       이 메소드는 워크 쓰레드라고 불리는 nThread를 포함하는 ExecutorService를 만들고 이들을 쓰레드 풀에 저장한다. 쓰레드 풀에서
         *       사용하지 않는 쓰레드로 제출된 태스크를 먼저 온 순서대로 실행한다. 이들 태스크 실행이 종료되면 이들 쓰레드를 풀로 반환한다. 이 방식의
         *       장점은 하드웨어에 맞는 수의 태스크를 유지함과 동시에 수 천개의 태스크를 쓰레드 풀에 아무 오버헤드 없이 제출할 수 있다는 점이다.
         *       큐의 조정, 거부 정책, 태스크 종류에 따른 우선 순위등 다양한 설정을 할 수 있다.
         *       프로그래머는 Task(Runnable, Callable)를 제공하면 이를 쓰레드가 실행한다.
         *
         *
         *              > 쓰레드 풀의 단점
         *       대부분 상황에서 유리하지만 두 가지 사항을 주의해야 한다.
         *
         *          1. k 쓰레드를 가진 쓰레드 풀은 오직 k 만큼의 쓰레드를 동시에 실행할 수 있다. 초과로 제출된 태스크는 큐에 저장되며, 이전에 태스크
         *          중 하나가 종료되기 전까지는 쓰레드에 할당하지 않는다. 불필요하게 많은 쓰레드를 만드는 일을 피할 수 있으므로 보통 이 상황은 아무 문제가
         *          되지 않지만 잠을 자거나 I/O를 기다리거나 네트워크 연결을 기다리는 태스크가 있다면 주의해야한다. I/O를 기다리는 블록 상황에서 이들
         *          태스크가 워크 쓰레드에 할당된 상태를 유지하지만 아무 작업도 하지 않게 된다.
         *          2. 중요한 코드를 실행하는 쓰레드가 죽는 일이 발생하지 않도록 보통 자바 프로그램은 main이 반환하기 전에 모든 쓰레드의 작업이 끝나길
         *          기다린다. 따라서 프로그램을 종료하기 전에 모든 쓰레드 풀을 종료하는 습관을 갖는 것ㅇ리 중요하다. 보통 장기간 실행하는 인터넷 서비스를
         *          관리하도록 오래 실행되는 ExecutorService를 갖는 것은 흔한 일이다.
         *
         *
         *
         *                  > 15.1.3 쓰레드의 다른 추상화 : 중첩되지 않는 메소드 호출
         *       포크/ 조인을 사용한 동시성에서는 한 개의 특별한 속성 즉, 태스크나 쓰레드가 메소드 호출 안에서 시작되면 그 메소드 호출은 반환하지 않고 작업이
         *       끝나기를 기다렸다. 다시 말해 쓰레드 생성과 join()이 한 쌍처럼 중첩된 메소드 호출 내에 추가되었다. 이를 엄격한 포크/조인 이라고 부른다.
         *       시작된 태스크를 내부 호출이 아니라 외부 호출에서 종료하도록 기다리는 좀 더 여유로운 방식의 포크/조인을 사용해도 비교적 안전하다.
         *
         *       이번 장은 메소드 호출에 의해 쓰레드가 생성되고 메소드를 벗어나 계속 실행되는 동시성 형태에 초점을 둔다. 이런 종류, 특히 메소드 호출자에 기능을
         *       제공하도록 메소드가 반환된 후에도 만들어진 태스크 실행이 계속되는 메소드를 비동기 메소드라고 한다. 우선 이들 메소드를 사용할 떄 어떤
         *       위험성이 따르는 지 확인해보자.
         *
         *          1. 쓰레드 실행은 메소드를 호출한 다음의 코드와 동시에 실행되므로 데이터 경쟁 문제를 일으키지 않아야 한다.
         *          2. 기존 실행 중이던 쓰레드가 종료되지 않은 상황에서 자바의 main() 메소드가 반환하면 어떻게 될까? 두 가지 방법이 있는데 어느 하나도
         *          안전하지 않다.
         *              - 애플리케이션을 종료하지 못하고 쓰레드가 끝날 때까지 쓰레드가 실행을 끝낼 때까지 기다린다.
         *              - 애플리케이션 종료를 방해하는 쓰레드를 강제 종료 시키고 애플리케이션을 종료한다.
         *           첫 번쨰 방법에서는 잊고서 종료 못한 쓰레드에 의해 애플리케이션이 크래쉬 될 수 있다. 또한 다른 문제로 디스크 쓰기 I/O 작업을 시도하는
         *           일련의 작업을 중단했을 때 이로 인해 외부 데이터의 일관성이 파괴될 수 있다. 이들 문제를 피하려면 애플리케이션에서 만든 모든 쓰레드를
         *           추적하고 애플리케이션을 종료하기 전에 쓰레드 풀을 포함한 모든 쓰레드를 종료하는 것이 좋다.
         *
         *           자바 쓰레드는 setDaemon() 메소드를 이용해 데몬 또는 비데몬으로 구분시킬 수 있다. 데몬 쓰레드는 애플리케이션이 종료될 때 강제로
         *           종료되므로 디스크의 데이터 일관성을 파괴하지 않는 동작을 수행할 때 유용하게 활용할 수 있는 반면, main() 메소드는 모든 비데몬
         *           쓰레드가 종료될 때까지 프로그램을 종료하지 않고 기다린다.
         *
         *
         *                      > 15.1.4 쓰레드에 바라는 바
         *      일반적으로 모든 하드웨어 쓰레드를 이용해 병령성의 장점을 극대화하도록 프로그램 구조를 만드는 것 즉, 프로그램을 작은 태스크 단위로 구조화하는
         *      것이 목표이다. (하지만 태스크 변환 비용을 고려해서 너무 작은 크기는 아니어야 한다.)
         *
         *
         *
         *                      > 15.2 동기 API, 비동기 API
         *      루프 기반 계산을 제외한 다른 상황에서도 병렬성이 유용할 수 있다. 아래와 같은 시그니쳐를 갖는 f,g 메소드의 호출을 합하는 예제를 살펴보자.
         *
         *          int f ( int x );
         *          int g ( int x );
         *
         *      참고로 이들 메소드는 물리적 결과를 반환하므로 동기(API)라고 부른다. 다음처럼 두 메소드를 호출하고 합계를 출력하는 코드가 있다.
         *
         *          int y = f ( x );
         *          int z = g ( x );
         *          System.out.println( y + z );
         *
         *      f,g를 실행하는데 오랜 시간이 걸린다고 할 떄 f, g의 작업을 컴파일러가 완전하게 이해하기 어려우므로 보통 자바 컴파일러는 코드 최적화와
         *      관련한 아무 작업도 수행하지 않을 수 있다. f,g가 서로 상호작용하지 않는다는 사실을 알고 있거나 상호작용을 전혀 신경쓰지 않는다면 f, g를
         *      별도의 코어로 실행함으로써 f, g 중 오래 걸리는 시간으로 합계를 구하는 시간을 단축할 수 있다. 별도의 쓰레드로  f, g를 실행해 구현할 수
         *      있지만 코드가 복잡해진다.
         *
         *          int x = 1337;
         *          Result result = new Result();
         *
         *          Thread t1 = new Thread( () -> {result.left = f(x); } );
         *          Thread t2 = new Thread( () -> {result.right = g(x); } );
         *          t1.start();
         *          t2.start();
         *          t1.join();
         *          t2.join();
         *          System.out.println(result.left + result.right
         *
         *          private static class Result {
         *              private int left;
         *              private int right;
         *          }
         *
         *      쓰레드 풀을 사용해도 복잡하다.
         *
         *           int x = 1337;
         *
         *           ExecutorService = executorService = Executors.newFixedThreadPool(2);
         *           Future<Integer> y = executorService.submit(() -> f(x));
         *           Future<Integer> z = executorService.submit(() -> g(x));
         *           System.out.println(y.get() + z.get());
         *
         *           executorService.shutdown();
         *
         *
         *     이러한 문제의 해결은 비동기 API라는 기능으로 API를 바꿔서 해결할 수 있다.
         *     첫 번쨰 방법인 자바의 Future를 이용하면 이 문제를 조금 개선할 수 있다. 자바 5에서 소개된 Future은 CompletableFuture로
         *     이들을 조합할 수 있게 되면서 기능이 품부해졌다. 두 번쨰 방법은 publish - subsribe 모델에 기반한 java.concurrent.Flow 인터페이스를
         *     이용하는 방법이다.
         *
         *                          > 15.2.1 Future 형식 APi
         *      대안을 사용하면 f, g 시그니처가
         *
         *              Future<Integer> f (int x);
         *              Future<Integer> g (int x);
         *
         *              Future<Integer> y = f(x)
         *              Future<Integer> z = g(x)
         *              System.out.println(y.get() + z.get());
         *
         *      메소드 f,g는 원래 바디를 평가하는 태스크를 포함하는 Future를 반환한다. get을 사용해서 Future가 완료되어 결과가 합쳐지기를 기다린다.
         *      f, g 중 하나에만 Future을 적용할 수 있지만 그러지 않는다.
         *
         *          1. 다른 상황에서도 g에도 Future가 필요할 수 있으므로 통일하는 것이 좋다.
         *          2. 병렬 하드워ㅔ어로 실행속도를 극대화려면 여러 작은, 합리적인 크기의 태스크로 나누는 것이 좋다.
         *
         *
         *                          > 15.2.2 리액티브 형식 API
         */

    }
}
