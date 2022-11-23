# 비동기 프로그래밍 정리

```java
import java.util.concurrent.CompletableFuture;

/**
 *
 * CompletableFuture라는 인스턴스가 생겼다. 결과로 Future를 반환한다.
 * 애러처리는 CompletableFuture의 complete/ completeExceptionally/ TimeoutException로 처리한다.
 * 특히 timeout은 future의 get에 타임아웃을 주는 식으로 설정한다.
 * 
 * 또한 supplyAsync 팩토리 메소드로 CompletableFuture를 사용할 수 있다.
 * 더 나아 supplyAsync에 Executors로 쓰레드 풀을 만들어서 사용할 수 있다.
 */
class Future {
    Executor executors = Executors.newFixedThreadPool(shops.size(), (Runnable r) -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public void example() {
        CompletableFuture.supplyAsync(()-> {
            System.out.println("hello");}, executors);
    }
}
/**
 * 데몬쓰레도로 선언해서 메인이 종료할 떄 같이 종료시킬 수도 있다. 아래는 쓰레드 계산식이다.
 *
 *   {
 *                               쓰레드 풀 크기 조절
 *        쓰레드 풀이 너무 크면 CPU, 메모리 자원을 서로 경쟁하느라 시간을 낭비할 수 있다. 반면 쓰레드 풀이 너무 작으면 CPU의
 *        일부 코어는 활용되지 않을 수 있다. 브라이언 게츠는 아래의 공식으로 CPU 활용 비율을 계산할 수 있다고 제한한다.
 *                           N(thread) = N(cpu) * U(cpu) * ( 1 + W/C)
 *             * N(cpu): Runtime.getRuntime().availableProcessors()가 반환하는 코어 수
 *             * U(cpu): 0 과 1 사이의 값을 갖는 CPU 활용 비율
 *             * W/C는 대기 시간과 계산 시간의 비율
 *   }
 *   
 * 뿐만 아니라 join() 다른 쓰레드 결과를 기다려서 이를 통해서 계산을 할 수도 있다. 이외에
 * thenCombine, thenCompose, thenApply, thenAccept 등으로 후작업을 구현할 수도 있다.
 */

class ArrayCompletable {
    public void test () {
        CompletableFuture[] futures = shops.findPricesStream("myPhone").map(f -> f.thenAccept(System.out::println))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
    }
}
```