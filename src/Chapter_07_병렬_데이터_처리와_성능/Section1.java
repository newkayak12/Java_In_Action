package Chapter_07_병렬_데이터_처리와_성능;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Section1 {
    /**
     * 자바 7 등정 이전에는 데이터 컬렉션을 병렬로 처리하는 것은 꽤 난이도가 있는 작업이었다. 데이터를 서브파트로 분할해야한다. 그리고 분할된 파트를 각각
     * 쓰레드로 할당해야한다. 쓰레드로 할당한 다음에는 의도치 않은 레이스 컨티션이 발생하지 않도록 적절한 동기화를 추가해야하며, 마지막으로 각 부분을 합쳐서
     * 하나의 결과로 내놓아야만 했다. 자바 7에서는 이러한 병렬화를 수행하면서 에러를 최소한으로 할 수 있는 포크/조인 프레임 워크를 제공한다.
     *
     *
     *          > 7.1 병렬 스트림
     * 컬렉션에 parallelStream을 호출하면 병렬 스트림이 만들어지는 것으로 비교적 쉽게 처리할 수 있다. 병렬 스트림이란 각가의 쓰레드에서 처리할 수 있도록
     * 스트림 요소를 여러 청크로 분할한 스트림이다. 따라서 병렬 스트림을 이용하면 모든 멀티 코어 프로세서가 각가의 청크를 처리하도록 할당할 수 있다.
     */
    public Long sequentialSum(Long n){
        return Stream.iterate(1L, i -> i+1).limit(n).reduce(0L, Long::sum);
    }
    /**
     * 전통적인 자바에서는 이를 반복문으로 처리해야 한다. 그렇다면 이를 어떻게 병렬로 처리할까? 쓰레드는 몇 개가 적당한가? 동기화는 어떻게 해야할까?
     *
     *
     *
     *          > 7.1.1 순차 스트림을 병렬 스트림으로 변환하기
     * 순차 스트림에 parallel 메소드를 호출하면 기존의 함수형 리듀싱 연산이 병렬로 처리된다.
     */
    public Long parallelSum(Long n){
        return Stream.iterate(1L, i -> i + 1).limit(n).parallel().reduce(0L, Long::sum);
    }
    /**
     * 위에서처럼 parallel을 적용하면 리듀싱 연산을 여러 청크에서 병렬로 수행할 수 있다. 마지막으로 리듀싱 연산으로 생성된 부분의 결과를 다시 리듀싱
     * 연산으로 합쳐서 전체 스트림의 리듀싱 결과를 도출한다.
     * 사실 순차 스트림에 parallel을 호출해도 스트림 자체에는 아무 변화도 일어나지 않는다. 내부적으로는 parallel을 호출하면 이후 연산이 병렬로 수행해야
     * 함을 의미하는 블래그 플래그가 설정된다. 반대로 sequential로 병렬 스트림을 순차 스트림으로 바꿀 수도 있다. parallel, sequential 두 메소드 중
     * 최종적으로 호출된 메소드가 전체 파이프 라인에 영향을 미친다.
     *
     *      {
     *          병렬 스트림에서 사용하는 쓰레드 풀 설정
     *          스트림의 parallel 메소드는 분명 쓰레드를 사용한다. 이 쓰레드가 몇 개가 생성되는지? 그리고 그 과정을 어떻게 커스터마이징해야 하는지
     *          궁금할 수 있다. 병렬 스트림은 내부적으로 ForkJoinPool을 이용한다. 기본적으로 ForkJoinPool은 프로세서 수, 즉 Runtime.getRunttime()
     *          .availableProcessor()가 반환하는 값에 상응하는 쓰레드를 갖는다.
     *
     *          System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");
     *
     *          와 같이 설정하면 이는 전역 설정이므로 이후의 모든 병렬 스트림 연산에 영향을 준다.
     *      }
     *
     *
     *
     *          > 7.1.3 병렬 스트림의 올바른 사용법
     * 병렬 스트림을 잘못 사용하면서 발생하는 많은 문제는 공유된 상태를 바꾸는 알고리즘을 사용하기 때문에 일어난다. 아래는 n까지 더하면서 공유된 누적자를
     * 바꾸는 코드이다.
     */
    public class Accumulator {
        public long total = 0;
        public void add(long value){ total += value;}
    }
    public long sideEffectSum(Long n){
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).forEach(accumulator::add);
        return accumulator.total;
    }
    public long sideEffectParallelSum(Long n){
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }
    /**
     * 위 코드는 본질적으로 순차 구성으로 구현되어 있으므로 병렬로 실행할 경우 문제가 생긴다. 특히 total로 접근할 때마다 데이터 레이스 문제가 발생한다.
     * 동기화 문제를 해결하다보면 결국 병렬화라는 특징이 없어질 것이다.
     *
     * 이를 기어코 병렬로 구성하여 실행하면 올바른 결과 값이 도출되지 않는다. 여러 쓰레드에서 동시에 누적자를 실행하면서 이런 문제가 발생한다. 특히나 위의
     * 예시는 atomic 연산도 아니기에 결국 여러 쓰레드에서 공유하는 객체의 상태를 바꾸는 forEach 블록 내부에서 add 메소드를 호출하면서 결과가 틀어진다.
     * 이러한 상태 공유에 따른 부작용을 피해야만 한다.
     *
     *
     *
     *              >7.1.4 병렬 스트림 효과적으로 사용하기
     * 병렬 스트림을 사용하는 조건을 양으로 정하기에는 적절하지 않은 감이 있다. 상황에 따라 달라지지만 적어도 아래의 경우를 고려하면 병렬 스트림을 사용해야하는
     * 경우를 조금이나마 명확하게 판단할 수 있을 것이다.
     *
     *  1. 박싱에 주의 : 박싱/ 언박싱은 꽤 리소스 소모가 큰 작업이다. 기본형 스트림(IntStrea, LongStream, DoubleStream)을 사용하면 이득을
     *  볼 수 있다.
     *
     *  2. 순차 스트림보다 병렬 스트림이 오히려 느려지는 경우가 있다. : 특히 limit, findFirst처럼 요소의 순서에 의존하는 연산을 병렬 스트림에서 수행
     *  하려면 비싼 비용을 치러야 한다.
     *
     *  3. 스트림에서 수행하는 전체 파이프라인 연산 비용을 고려하자: 처리해야 할 요소 수가 N이고 하나의 요소를 처리하는 데 드는 비용을 Q라고 하면
     *  전체 스트림 파이프라인 처리 비용을 N*Q라고 예상할 수 있다. Q가 높아진다는 것은 병렬 스트림으로 성능을 개선할 가능성이 있음을 시사한다.
     *
     *  4. 소량의 데이터에서는 오히려 병렬 스트림이 불필요하다. : 소량의 데이터를 처리하는 상황에서는 병렬화 과정에서 생기는 부가 비용을 상쇄할 정도의
     *  메리트가 있는 경우가 거의 없다. 따라서 소량일 경우 순차 스트림을 사용하는 것이 비용적으로 이득이다.
     *
     *  5. 스트림을 구성하는 자료 구조를 고려하라. : 예를 들어 ArrayList는 LinkedList보다 병렬 스트림에 적합하다. 또한 range 팩토리 메소드로
     *  만든 기본형 스트림도 쉽게 분해할 수 있다.
     *
     *  6. 스트림의 특성과 파이프라인의 중간 연산이 스트림의 특성을 어떻게 바꾸는가에 따라 달라진다. : 예를 들어 SIZED 스트림은 정확히 같은 크기의 두 스트림
     *  으로 분할할 수 있으므로 효율적이지만, 필터 연산이 있다면 스트림의 길이를 예측하기 어려우므로 효과적으로 스트림을 병렬 처리할지 불확실하다.
     *
     *  7. 최조 연산 과정의 병확 과정 비용을 고려하라. : 병합 과정의 비용이 높다면 병렬 스트림으로 얻은 성능의 이익이 서브스트림의 부분 결과를 결합하는
     *  과정에서 상쇄될 수 있다.
     *
     *
     *         {
     *             스트림 소스와 분해성
     *             ArrayList      : 아주 좋음
     *             LinkedList     : 나쁨
     *             IntStream.range: 아주 좋음
     *             Stream.iterate : 나쁨
     *             HashSet        : 좋음
     *             TreeSet        : 좋음
     *         }
     *
     *  마지막으로 병렬 스트림이 수행되는 내부 인프라 구조도 살펴봐야 한다.
     *
     *
     *
     *
     *                 >7.2 포크/조인 프레임워크
     * 포크/조인 프레임워크는 병렬화할 수 있는 작업을 재귀적으로 작은 작업으로 분할한 다음 서브태스크 각각의 결과를 합쳐서 전체 결과를 만들도록 설계되어
     * 있다. 포크/조인 프레임워크에서는 서브태스크를 쓰레드 풀(ForkJoinPool)의 작업자 쓰레드에 분산 할당하는 ExecutorService 인터페이스를 구현한다.
     *
     *
     *                  >7.2.1 RecursiveTask 활용
     * 쓰레드 풀을 이용하려면 RecursiveTask<R>의 서브 클래스를 만들어야 한다. 여기서 R은 병렬화된 태스크가 생성하는 결과 형식 또는 결과가 없을 때는
     * RecursiveAction 형식이다. RecursiveTask를 정의하려면 추상메소드 compute를 구현해야한다.
     * compute 메소드는 태스크를 서브 태스크로 분할하는 로직과 더 이상 분할할 수 없을 때 개별 서브태스크의 결과를 생산할 알고리즘을 정의한다. 따라서
     * 대부분의 compute 메소드 구현은 아래와 같은 의사코드 형식을 유지한다.
     *
     *          if(태스크가 충분히 작거나 더 이상 분할할 수 없다면){
     *              순차적으로 태스크 계산
     *          } else {
     *              태스크 분할
     *              태스크 소분하도록 재귀적 호출
     *              모든 서브태스크 연산 완료시까지 기다림
     *              각 서브태스크의 결과를 합침
     *          }
     * 이 알고리즘은 분할 정복 알고리즘의 병렬화 버전이다. 포크/조인 프레임워크를 이용해서 범위의 숫자를 더하는 문제를 구현하면서 포크/조인 프레임워크를
     * 사용하는 방법을 알아보자.
     */
    public class ForkJoinSumCalculator extends java.util.concurrent.RecursiveTask<Long>{
        private final long[] numbers;
        private final int start;
        private final int end;
        private static final long THRESHOLD = 10_000;

        public ForkJoinSumCalculator(long[] numbers){
            this(numbers, 0, numbers.length);
        }
        public ForkJoinSumCalculator(long[] numbers, int start, int end){
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }
        @Override
        protected Long compute() { //RecursiveTask의 추상 메소드 오버라이드
            int length = end - start; // 이 태스크에서 더할 배열의 길이
            if(length <= THRESHOLD){
                return computeSequentially(); //기준값과 같거나 작으면 순차적으로 결과를 계산
            }
            ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start+length/2); // 배열의 첫 번째 절반을 더하도록 서브태스크를 생성
            leftTask.fork(); //ForkJoinPool의 다른 쓰레드로 새로 생성한 태스크를 비동기로 실행
            ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length/2, end); // 배열의 나머지 절반을 더하도록 서브태스크를 생성
            Long rightResult = rightTask.compute();// 두 번째 서브태스크를 동시 실행, 이때 추가로 분할이 발생할 수 있다.
            Long leftResult = leftTask.join(); // 첫 번째 서브태스크의 결과를 읽거나 아직 결과가 없으면 기다림
            return leftResult + rightResult; // 두 서브태스크의 결과를 조합한 값
        }

        private long computeSequentially(){
            long sum = 0;
            for( int i = start; i < end; i++){
                sum += numbers[i];
            }
            return sum;
        }
    }
    public /*static*/  long forkJoinSum(Long n){
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        return new ForkJoinPool().invoke(task);
    }
    /**
     * 위의 코드는 LongStream으로 n까지의 자연수를 포함하는 배열을 생성했다. 그리고 생성된 배열을 ForkJoinSumCalculator의 생성자로 전달하여
     * ForkJoinTask를 만들었다. 마지막으로 생성한 태스크를 새로운 ForkJoinPool의 invoke 메소드로 전달했다. ForkJoinPool에서 실행되는
     * 마지막 invoke 메소드의 반환값은 ForkJoinSumCalculator에서 정의한 태스크의 결과가 된다.
     *
     * 일반적으로 애플리케이션에서는 둘 이상의 ForkJoinPool을 사용하지 않는다. 즉, 소프트웨어의 필요한 곳에서 언제든 가져다 쓸 수 있도록 FokJoinPool
     * 을 한 번만 인스턴스화해서 정적필드에 싱글톤으로 저장한다. ForkJoinPool을 만들면 인수가 없는 디폴트 생성자를 이용했는데, 이는 JVM에서 이용할 수
     * 있는 모든 프로세서가 자유롭게 풀에 접근할 수 있음을 의미한다. 더 정확히는 Runtime.availableProcessors의 반환값으로 풀에 사용할 쓰레드 수를
     * 결정 한다. (availalbeProcessors는 실제 프로세스 외 하이퍼쓰레딩과 관련된 가상 프로세스 수도 포함한다.)
     *
     *      > ForkJoinSumCalculator 실행
     * ForkJoinSumCalculator를 ForkJoinPool로 전달하면 풀의 쓰레드가 ForkJoinSumCalculator의 compute 메소드를 실행하면서 작업을 실행한다.
     * compute 메소드는 병렬로 실행할 수 있을 만큼 태스크의 크기가 충분히 작아졌는지 확인하며, 아직 태스크의 크기가 크다고 판단되면 숫자 배열을 지속적으로
     * 반으로 분할해서 두 개의 새로운 ForkJoinSumCalculator로 할당한다. 그러면 재귀적으로 반복되며, 주어진 조건이 만족될 때까지 태스크 분할을 반복한다.
     *
     * 이제 각 서브태스크는 순차적으로 처리되며, 포킹 프로세스로 만들어진 이진트리의 태스크를 루트에서 역순으로 방문한다(재귀이므로) 그 후, 각 서브태스크의
     * 부분 결과를 합쳐서 최종 결과를 계산한다.
     *
     *
     *
     *              >7.2.2 포크/조인 프레임워크를 제대로 사용하는 방법
     * 포크/조인 프레임워크는 쉽게 사용할 수 있는 편이지만 항상 주의를 기울여야한다. 아래는 포크/조인 프레임워크를 효과적으로 사용하는 방법이다.
     *
     *  1. join 메소드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 불록시킨다. 따라서 두 서브태스크가 모두 시작된 다음에 join을
     *  호출해야한다. 그렇지 않으면 각각의 서브 태스크가 다른 태스크가 끝나길 기다리는 일이 발생하면 원래 순차 알고리즘보다 느리고 복잡한 프로그램이 된다.
     *
     *  2. RecursiveTask 내에서는 ForkJoinPool의 invoke 메소드를 사용하지 않아야한다. 대신 compute, fork 메소드를 직접 호출할 수 있다. 순차
     *  코드에서 병렬 계산을 시작할 때만 invoke를 사용한다.
     *
     *  3. 서브태스크에 fork 메소드를 호출해서 ForkJoinPool의 일정을 조율할 수 있다. 왼쪽/ 오른쪽 작업 모두에 fork를 하는 것이 맞을 것 같지만
     *  한쪽에는 fork보다 compute를 호출하는 것이 효율적이다. 그러면 두 서브 태스크의 한 태스크에는 같은 쓰레드를 재사용할 수 있으므로 풀에서 불필요한
     *  태스크를 할당하는 오버헤드를 막을 수 있다.
     *
     *  4. 포크/조인 프레임워크를 이용하는 병렬 계산은 디버깅하기 어렵다. 포크/조인 프레임워크에서는 fork라고 불리는 다른 쓰레드에서 compute를 호출하므로
     *  스택 트래이스가 의미가 없다.
     *
     *  5. 병렬 스트림에서 본 것처럼 멀티코어에 포크/조인 프레임워크를 사용하는 것이 순차처리보다 '무조건 빠르다'는 것은 오산이다. 병렬처리로 성능을 개선하려면
     *  태스크를 여러 독립적인 서브태스크로 분할할 수 있어야 한다. 각 서브태스크의 실행 시간은 새로운 태스크를 포킹하는 데 드는 시간보다 길어야 한다.
     *  예를 들어 I/O를 한 서브 태스크에 할당하고 다른 서브태스크에서는 계산을 실행, 즉 I/O와 계산을 병렬로 실행할 수 있다. 또한, 순차 버전과 병렬 버전 성능을
     *  비교할 때는 다른 요소도 고려해야 한다.
     *
     *                  >7.2.3 작업 훔치기
     *  예를 들어 천만 개 항목을 포함하는 배열을 사용하면 ForkJoinSumCalculator(위의 예제)는 천 개 이상의 서브 태스크를 포크할 것이다. 코어 수가 적다면
     *  천 개 이산의 서브 태스크는 자원 낭비같아 보인다. 실제로 각각의 태스크가 CPU로 할당되는 상황이라면 크게 의미가 없어 보인다.
     *
     *  하지만 실제로 코어 개수와 관계없이 적절한 크기로 분할된 만은 태스크를 포킹하는 것이 바람직하다. 이론적으로 코어 개수만큼 병렬화된 태스크로 작업 부하를
     *  분할하면 모든 CPU 가 일을 할 것이고 크기가 같은 각각의 태스크가 동일한 시간에 시작했다면 동일한 시간에 종료할 것으로 보인다. 하지만 실제로는
     *  결과가 다를 수 있다. 분할 기법이 효율적이지 않을 수도 있고, 디스크 접근 속도가 달라지거나 외부의 영향을 받아 지연이 발생할 수도 있기 때문이다.
     *
     *  포크/조인 프레임워크는 작업 훔치기(Work stealing)으로 이 문제를 해결한다. 작업 훔치기는 ForkJoinPool의 모든 쓰레드를 거의 공정하게 분할한다.
     *  각각의 쓰레드는 자신에게 할당된 태스크를 포함하는 이중 연결 리스트(doubly linked list)를 참조하면서 작업이 끝날 때마다 큐의 헤드에서 작업을 가져다
     *  일을 시작한다. 이때 한 쓰레드는 다른 쓰레드보다 자신에게 주어진 일을 더 빨리 처리할 수도 있다. 즉, 한 쓰레드는 놀고 있고 다른 쓰레드는 일하고 있을
     *  수 있다. 이 때 할 일이 없어진 쓰레드는 다른 쓰레드 큐의 꼬리에서 작업을 훔쳐온다. 모든 태스크가 작업을 끝낼 때까지 이 과정을 반복한다.
     *  따라서 태스크의 크기를 잘게 쪼개야 작업자 쓰레드 간의 작업 부하를 비슷한 수준으로 유지할 수 있다.
     *
     *  (놀고 있는 쓰레드는 다른 쓰레드의 태스크 큐에 있는 마지막 작업을 가져다가 한다.)
     *
     *
     *
     *                  >7.3 Spliterator 인터페이스
     *  자바 8은 Spliterator라는 새로운 인터페이스를 제공한다. Spliterator는 '분할할 수 있는 반복자'라는 의미이다. Iterator처럼 Splitator는
     *  소스의 요소 탐색 기능을 제공한다는 점은 같지만 Spliterator는 병렬 작업에 특화되어 있다. 자바 8은 컬렉션 프레임워크에 포함된 모든 자료구조에
     *  사용할 수 있는 디폴트 Spliterator 구현을 제공한다.
     *
     *      public interface Spliterator<T>{
     *          boolean tryAdvance(Consumer<? super T> action);
     *          Spliterator<T> trySplit();
     *          long estimateSize();
     *          int characteristics();
     *      }
     *
     *  T는 Spliterator에서 탐색하는 요소의 형식을 가리킨다. tryAdvance 메소드는 Spliterator의 요소를 하나씩 순차적으로 소비하면서 탐색해야 할
     *  요소가 남아있으면 참을 반환한다. 반면 trySplit 메소드는 Spliterator의 일부 요소를 분할하여 두 번쨰 Spliterator를 생성하는 메소드이다.
     *  Spliterator에는 estimateSize 메소드로 탐색해야할 요소 수 정보를 제공할 수 있다. 특히 탐색해야할 요소 수가 정확하지는 않더라도
     *  제공된 값을 이용해서 더 쉽고 공평하게 Spliterator를 분할할 수 있다.
     *
     *
     *
     *                   > 7.3.1 분할 과정
     *  스트림에서 여러 스트림으로 분할하는 과정은 재귀적으로 발생한다. 1단계에서 첫 번쨰 Spliterator에 trySplit을 호출하면 두 번쨰 Spliterator가
     *  생성된다. 2단계에서 같은 일을 하면 네 개의 Spliterator가 생성된다. 이처럼 trySplit이 null이 될 때까지 반복한다. 모든 trySplit에서 null이
     *  나오면 재귀 분할을 중지한다.
     *
     *
     *          > Spliterator의 특성
     *   Spliterator는 charateristic이라는 추상 메소드도 정의한다. Characteristics 메소드는 Spliterator 자체의 특정 집합을 포함하는 int를
     *   반환한다. Spliterator를 이용하는 프로그램은 이들 특성을 참고하여 Spliterator를 더 잘 제어하고 최적화할 수 있다.
     *
     *          {
     *                  spliterator 특성
     *             ORDERED : 리스트처럼 정해진 순서가 있으므로 Spliterator는 요소를 분할/탐색할 때 순서에 유의해야한다.
     *             DISTINCT : x,y 두 요소를 방문했을 때, x.equals(y)는 항상 false이다.
     *             SORTED : 탐색된 요소는 미리 정의된 정렬 순서를 따른다.
     *             SIZED : 크기가 알려진 소스(Set과 같은)로 Spliterator를 생성했으므로 estimatedSize()는 정확한 값을 반환한다.
     *             NON-NULL : 탐색하는 모든 요소는 NON-NULL이다.
     *             IMMUTABLE : 이 Spliterator의 소스는 불변이다. 즉, 요소를 탐색하는 동안 요소를 추가하거나, 삭제하거나, 고칠 수 없다.
     *             CONCURRENT : 동기화 없이 Spliterator의 소스를 여러 쓰레드에서 동시에 고칠 수 있다.
     *             SUBSIZED : 이 Spliterator 그리고 분할되는 모든 Spliterator는 SIZED 속성을 갖는다.
     *          }
     *
     *                      > 7.3.2 커스텀 Spliterator 구현하기
     *  Spliterator 구현 예제를 보면서 이해해보자. 문자열의 단어 수를 계산하는 단순한 메소드를 구현할 것이다.
     *
     */
}
