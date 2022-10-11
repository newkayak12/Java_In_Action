package Chapter_06_스트림으로_데이터_수집;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

public class Section1 {
    /**
     *      6. 스트림으로 데이터 수집
     *  스트림의 연산은 filter, map과 같은 중간 연산과 count, findFirst, forEach, reduce 등의 최종 연산으로 구분할 수 있다. 중간 연산은
     *  한 스트림을 다른 스트림으로 변환하는 연산으로서, 여러 연산을 연결할 수 있다. 중간 연산은 스트림 파이프라인을 구성하며, 스트림의 요소를 소비 하지 않는다.
     *  반면 최종 연산은 스트림의 요소를 소비하여 최종 결과를 도출한다. 이번 챕터에서는 최종 연산자 중 collect, 컬렉터로 할 수 있는 일을 알아볼 것이다. 
     *  
     *  
     *      6-1. 컬렉터란 무엇인가?
     *  Collector 인터페이스 구현은 스트림의 요소를 어떤 식으로 도출할지 지정한다. 예를 들어 toList를 Collector 인터페이스의 구현으로 사용했다. 여기서
     *  groupBy를 이용해서 그루핑을 할 수 있다.
     *  
     *      6-1-1 고급 리듀싱 기능을 수행하는 컬렉터
     *  collect는 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의할 수 있다는 점이 최대 강점이다. 구체적으로 설명해서 스트림에 collect를 호출하면
     *  스트림의 요소에 리듀싱 연산이 수행된다. 
     *  
     *      6-1-2 미리 정의된 컬렉터
     *  미리 정의된 컬렉터, 즉 groupingBy와 같이 Collectors 클래스에서 제공하는 팩토리 기능을 살펴보자. Collector에서 제공하는 메소드의 기능은
     *  크게 세 가지로 구분할 수 있다.
     *  
     *      1. 스트림 요소를 하나의 값으로 리듀스하고 요약
     *      2. 요소 그룹화 : 다수준으로 그룹화, 각가의 결과 서브그룹에 추가로 리듀싱 연산 적용, 다양한 컬렉터를 조합
     *      3. 요소 분할  
     *  
     *  
     *      6-2. 리듀싱과 요약
     *  
     *  일단 Collector 팩토리 클래스로 만든 컬렉터 인스턴스로 어떤 일을 할 수 있는지 살펴보자. 앞서 본 바와 같이 Stream.collect 메소드의 인수(컬렉터)
     *  로 스트림의 항목을 컬렉션으로 재구성 할 수 있다.
     *
     *  첫 번쨰 예제로 counting()이라는 팩토리 메소드가 반환하는 컬렉션을 계산해보자.
     */
    enum Type {
        MEAT,FISH,OTHER;
    }
    class Dish {
        private String name;
        private Boolean isVegetable;
        private Integer calorie;
        private Type type;

        public Dish(String name, Boolean isVegetable, Integer calorie, Type type) {
            this.name = name;
            this.isVegetable = isVegetable;
            this.calorie = calorie;
            this.type = type;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Boolean getVegetable() {
            return isVegetable;
        }
        public void setVegetable(Boolean vegetable) {
            isVegetable = vegetable;
        }
        public Integer getCalorie() {
            return calorie;
        }
        public void setCalorie(Integer calorie) {
            this.calorie = calorie;
        }
        public Type getType() {
            return type;
        }
        public void setType(Type type) {
            this.type = type;
        }
    }

    List<Dish> specialMenu = Arrays.asList(
            new Dish("seasonal fruit", true, 120, Type.OTHER),
            new Dish("prawns", false, 300, Type.FISH),
            new Dish("rice", true, 350, Type.OTHER),
            new Dish("chicken", false, 400, Type.MEAT),
            new Dish("french fries", true, 530, Type.OTHER)
    );
    long howManyDishes = specialMenu.stream().collect(Collectors.counting());
    /**
    *  Collectors의 couting은 다른 컬렉터와 함께 사용할 때 위력을 발휘한다.
     *
     *      6-2-1. 스트림 값에서 최대값과 최소값 검색
     */
    Comparator<Dish> dishCaloriesComparator = Comparator.comparing(Dish::getCalorie);
    Optional<Dish> mostCalorieDish = specialMenu.stream().collect(maxBy(dishCaloriesComparator));
    /**
     * 합계, 평균 등을 반환하는 연산에도 리듀싱이 자주 사용된다. 이러한 연산을 요약 연산이라고 한다.
     *
     *
     *      6-2-2. 요약 연산
     *  Collectors 클래스는 Collector.summingInt라는 요약 팩토리 메소드를 제공한다. summingInt는 객체를 int로 매핑하는 함수의 인수로 받는다.
     *  summingInt의 인수로 전달된 함수는 객체를 int로 매핑한 컬렉터를 반환한다. 그리고 summingInt가 Collect 메소드로 전달되면 요약 작업을 시작한다.
     */
    int totalCalories = specialMenu.stream().collect(summingInt(Dish::getCalorie));
    /**
     *  이러한 단순 합계 외에 평균값 계산 등의 연산도 요약 기능으로 제공된다. 즉, Collector.averageInt/Long/Double 등으로 다양한 형식으로 이뤄진 숫자
     *  집합의 평균을 계산할 수 있다.
     *  또한 이러한 모든 연산을 한 번에 수행해야할 때도 있다. 이런 상황에서는 팩토리 메소드 summerizaingInt가 반환하는 컬렉터를 사용할 수 있다.
     */
    IntSummaryStatistics summaryStatistics = specialMenu.stream().collect(summarizingInt(Dish::getCalorie));
    //
    //   private long count;
    //    private long sum;
    //    private int min = Integer.MAX_VALUE;
    //    private int max = Integer.MIN_VALUE;
    //
    //  등을 제공한다.
    /**
     *      6-2-3 문자열 연결
     *  컬렉터에 joining 팩토리 메소드를 사용하면 스트림의 각 객체에 toString 메소드를 호출해서 추출한 모든 문자열은 하나의 문자열로 연결해서 반환한다.
     */
    String shortMenu = specialMenu.stream().map(Dish::getName).collect(Collectors.joining());
    /**
     * joining 메소드는 내부적으로 StringBuilder를 사용해서 문자열을 하나로 만든다. 여기서 joining에 구분자를 제공하지 않으면 문자가 그대로 붙는다.
     * 따라서 그래서 매개변수로 구분자를 받는다.
     */
    String getShortMenu = specialMenu.stream().map(Dish::getName).collect(joining(","));
    /**
     *      6-2-4. 범용 리듀싱 요약 연산
     *  이러한 위의 작업들은 모두 reducing 팩토리 메소드로도 가능하다. 즉, 범용 Collectors.reducing으로도 구현할 수 있다. 그럼에도 위의 메소드들
     *  을 구현해 놓은 이유는 편의성 때문이다.
     */
    int getTotalCalories = specialMenu.stream().collect(reducing(0, Dish::getCalorie, (i,j)->i+j));
    /**
     * 이와 같이 구현할 수 있다. 리듀싱은 세 개의 인수를 받는다.
     *      1. 첫 번째 인수는 리듀싱의 시작 값이다.
     *      2. 두 번쨰 인수는 리듀싱을 사용할 요소를 반환한다.
     *      3. 세 번째 인수는 리듀싱 작업을 명시한다.
     */
    Optional<Dish> getMostCalorieDish = specialMenu.stream().collect(reducing((d1,d2)-> d1.getCalorie() > d2.getCalorie()? d1: d2));
    /**
     * 이와 같이 최대 값 역시 얻어낼 수 있다.
     *
     *          > 컬렉션 프레임 워크의 유연성 : 같은 연산도 각기 다른 방법으로
     *  reducing으로 Integer의 sum을 이용해서 tatalCalroies를 더 편하게 구할 수 있다.
     */
    int getTotalCalories2 = specialMenu.stream().collect(reducing(0, Dish::getCalorie, Integer::sum));
    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *      6.3 그룹화
     *  데이터 집합을 하나 이상의 특성으로 분류하여 그룹화하는 연산도 자바 8의 함수형을 이용하면 가독성 있게 그룹화를 구현할 수 있다. 메뉴를 그룹화하는
     *  예시를 살펴보자 이는 팩토리 메소드 Collectors.groupingBY로 수행할 수 있다.
     */
    Map<Type, List<Dish>> dishGroup = specialMenu.stream().collect(groupingBy(Dish::getType));
    {
        dishGroup.get(Type.FISH);
        dishGroup.get(Type.MEAT);
        dishGroup.get(Type.OTHER);
    }
    /**
     * 이렇게 그룹화 하여 Map으로 반환할 수 있다. groupingBy에 전달한 요소를 기준으로 스트림이 그룹화되므로 이를 '분류 함수'라고 부른다.
     *
     *
     *      6.3.1 그룹화된 요소의 조작
     *  요소를 그룹화한 뒤 의미있는 데이터를 만들기 위해서 그룹의 요소를 조작하는 일이 있을 수 있다. 이전 예제와 달리  각 그룹을 문자열 리스트로 하여 예시를
     *  살펴보자. groupingBy와 연계하여 세 번째 컬렉터를 사용해서 일반 맵이 아닌 flatMap 변환을 수행할 수도 있다.
     */
    Map<String,List<String>> dishTag = Map.of("pork",  Arrays.asList("greasy", "salty"),"beef", Arrays.asList("salty", "roasted"),
            "chicken", Arrays.asList("fried", "crisp"), "french fries", Arrays.asList("greasy","fried"), "rice", Arrays.asList("light", "natural"),
            "pizza", Arrays.asList("tasty", "salty"), "prawns", Arrays.asList("tasty", "roasted"), "salmon", Arrays.asList("delicious","fresh"));
    /**
     * flatMapping 컬렉터를 사용하면 각 형식의 요리의 태그를 간편하게 추출할 수 있다.
     */
    Map<Type, Set<String>> dishNamesByType = specialMenu.stream().collect(groupingBy(Dish::getType,
            flatMapping(dish -> dishTag.get(dish.getName()).stream(), toSet())));
    /**
     * Meat=[salty,greasy,roasted,fried,crisp], FISH=[roasted,tasty,fresh,delicious], OHTER=[salty,greasy,natural,light,tasty,
     * fresh,fried]}와 같은 결과를 얻을 수 있다.
     *
     *
     *      6.3.4 다수준 그룹화
     *  두 인수를 받는 팩토리 메소드 Collectors.groupingBy를 이용해서 항목을 다수준으로 그룹화할 수 있다. Collectors.groupingBy는 일반적인
     *  분류 함수와 컬렉터를 인수를 받는다. 즉, 바깥쪽 메소드에 스트림의 항목을 분류할 두 번째 기준을 정의하는 내부 groupingBy를 전달해서 두 수준으로
     *  스트림의 항목을 그룹화할 수 있다.
     */
    enum Caloric {
        DIET,NORMAl,FAT;
    }
    Map<Type,Map<Caloric, List<Dish>>> dishesByTypeCaloricLevel = specialMenu.stream().collect(
            groupingBy(Dish::getType,groupingBy(dish -> {
                if(dish.getCalorie() <= 400) return Caloric.DIET;
                else if (dish.getCalorie() <= 700) return Caloric.NORMAl;
                else return Caloric.FAT;
            }))
    );
    /**
     * 이렇게 하면 그룹화 결과로
     * {
     *      MEAT={DIET=[chicken], NORMAL=[beef], FAT=[pork]}, FISH={DIET=[prawns],NORMAL=[salmon]},
     *      OTHER={DIET=[rice,seasonal fruit], NORMAL=[french fries, pizza]}
     * }
     * 를 얻을 수 있다.
     *
     *
     *      6-3-3. 서브 그룹으로 데이터 수집
     *  위에서 groupingBy로 다수준 그룹화 연산을 구현했다. 첫 번째 groupingBy로 넘겨주는 컬렉터의 형식은 제한이 없다. 아래는 그 예시이다.
     */
    Map<Type, Long> typeCount = specialMenu.stream().collect(groupingBy(Dish::getType, counting()));
    /**
     *  분류 함수 한 개의 인수를 갖는 groupingBy(f)는 groupingBy(f, toList())의 축약형이다.
     *
     *      > 컬렉터 결과를 다른 형식에 적용하기
     *  컬렉터가 반환한 결과를 Collectors.collectingAndThen으로 다른 형식으로 사용할 수도 있다
    */
    Map<Type, Dish> mostCaloricByType = specialMenu.stream().collect(groupingBy(Dish::getType,
            collectingAndThen(
                    maxBy(Comparator.comparingInt(Dish::getCalorie)),
                    Optional::get
            )));
    /**
     *  팩토리 메소드 collectingAndThen은 적용할 컬렉터와 변환 함수를 인수로 받아 다른 컬렉터를 반환한다. 반환하는 컬렉터는 기존 컬렉터의 래퍼 역할을
     *  하며, collect의 마지막 과정에서 변환하는 함수로 자신이 반환하는 값을 매핑한다.
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *          6.4 분할
     *   분할은 분할 함수(Partitioning function)이라고 불리는 Predicate를 분류 함수로 사용하는 특수한 그룹화 기능이다.
     *   분할은 분할 함수(Partitioning function)이라고 불리는 Predicate를 분류 함수로 사용하는 특수한 그룹화 기능이다. 분할 함수는 불리언을 반환하므로
     *   맵의 키 형식은 Boolean이다. 결과적으로 그룹 화 맵은 최대 두 그룹으로 분류된다(true/false)
     */
    Map<Boolean, List<Dish>> partitionedMenu = specialMenu.stream().collect(partitioningBy(Dish::getVegetable));
    /**
     *          6.4.1 분할의 장점
     *   분할 함수가 반환하는 참,거짓 두 가지 요소의 스트림 리스트를 모듀 유지한다는 것이 분할의 장점이다. 위의 예시에 추가적으로 컬렉터를 두 번째 인수로
     *   전달할 수 있는 오버로드된 partitioningBy가 있다.
     */
    Map<Boolean, Map<Type, List<Dish>>> vegetarianDishesByType = specialMenu.stream().collect(
            partitioningBy(Dish::getVegetable, groupingBy(Dish::getType))
    );
    /**
     *    partitioningBy가 반환한 맵 구현은 참/ 거짓 두 가지 키만 포함하므로 더욱 간결하고 효과적이다. 사실 내부적으로 partitioningBy는 특수한
     *    맵과 두 개의 필드로 구현되었다. 그 외에도 groupingBy와 partitioningBy 컬렉터와 유사한 점이 또 있다. 이는 다수준으로 그룹화를 할 수 있던
     *    것처럼 다수준으로 분할할 수 있다는 것이다.
     */
    Map<Boolean, Map<Boolean, List<Dish>>> a = specialMenu.stream().collect(partitioningBy(Dish::getVegetable, partitioningBy(d->d.getCalorie() > 500)));
    //    Map<Boolean, Long> b = specialMenu.stream().collect(partitioningBy(Dish::getVegetable, Collectors.partitioningBy(Dish::getType)));
    Map<Boolean, Long> c = specialMenu.stream().collect(partitioningBy(Dish::getVegetable, counting()));
    /**
     *
     *          6.4.2 숫자를 소수, 비소수로 분할하기
     *    정수 n을 인수로 받아서 2에서 n까지 자연수를 소수/ 비소수로 나누느 프로그램을 만들어보자. 먼저 소수인지 아닌지 판단하는 Predicate를 만들어보자.
     *    그 후, partitioningBy 컬렉터로 리듀스해서 숫자를 소수, 비소수로 분류할 수 있다.
     */
    public boolean isPrime(int candidate){
        return IntStream.range(2,candidate).noneMatch(i -> candidate % i == 0);
    }
    public boolean isPrime2(int candidate){
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return IntStream.rangeClosed(2, candidateRoot).noneMatch(i -> candidate % i == 0);
    }
    public Map<Boolean, List<Integer>> partitionPrimes(int n ){
        return IntStream.rangeClosed(2, n).boxed().collect(partitioningBy(candidate -> isPrime(candidate)));
    }
    /**
     *
     *          6.5 Collector 인터페이스
     *   Collector 인터페이스는 리듀싱 연산을 어떻게 구현할지 제공하는 메소드의 집합으로 구성된다. 만약 이것이 사용하고자 하는 바와 맞지 않는다면
     *   Collector 인터페이스를 직접 구현하여 더 효율적으로 문제를 해결하는 컬렉터를 만들 수도 있다.
     *   Collector를 구현하여 커스터마이징하기 전에 toList를 살펴보고 Collector가 어떻게 정의되어 있고, 내부적으로 collect 메소드는 toList가
     *   반환하는 함수를 어떻게 사용하는지 보면서 이해할 수 있다.
     *
     *      public interface Collector<T, A, R> {
     *          Supplier<A> supplier();
     *          BiConsumer<A,T> accumulator();
     *          Function<A, R> finisher();
     *          BinaryOperator<A> combiner();
     *          Set<Characteristics> characteristics();
     *      }
     *
     *   위 코드를 설명하면 아래와 같다.
     *
     *    T는 수립될 스트림 항목의 제네릭 형식이다.
     *    A는 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식이다.
     *    R은 수집 연산 결과 객체의 형식(대게 컬렉션 형식)이다.
     *
     *   예를 들어 Stream<T>의 모든 요소를 List<T>로 수집하는 ToListCollector<T>라는 클래스를 구현할 수 있다.
     *   public class ToListCollector<T> implements Collector<T,List<T>, List<T>>
     *
     */

    class Scope  <T,A,R>{
        /**
         *            6.5.1 Collector 인터페이스의 메서드
         *              > supplier 메소드: 새로운 결과 컨테이너 만들기
         *   supplier 메소드는 빈 결과로 이뤄진 Supplier를 반환해야 한다. 즉, supplier는 수집과정에 빈 누적자 인스턴스를 만드는 파라미터가 없는
         *   함수이다.
         */
        public Supplier<List<T>> supplier(){
            return () -> new ArrayList<T>();
        }
        public Supplier<List<T>> supplier2(){
            return ArrayList::new;
        }
        /**
         *              > accumulator 메소드: 결과 컨테이너에 요소 추가하기
         *  accumulator 메소드는 리듀싱 연산을 수행하는 함수를 반환한다. 스트림에서 n번째 요소를 탐색할 때 두 인수, 즉 누적자와 n번째 요소를 함수에
         *  적용한다. 함수의 반환값은 void, 요소를 탐색하면서 적용하는 함수에 의해 누적자 내부 상태가 바뀌므로 누적자가 어떤 값일지 단정할 수 없다.
         */
        public BiConsumer<List<T>, T> accumulator(){
            return  (list, item) -> list.add(item);
        }
        public BiConsumer<List<T>, T> accumulator2(){
            return List::add;
        }
        /**
         *               > finisher 메소드: 최종 변환값을 결과 컨테이너로 적용하기
         *   finisher 메소드는 스트림 탐색을 끝내고 누적자 객체를 최종 결과로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환해야 한다. 때로는
         *   ToListCollector에서 볼 수 있는 것처럼 누적자 객체가 이미 최종 결과인 경우도 있다. 이 때는 변환 과정을 필요치 않으므로
         *   finisher 메소드는 항등 함수를 반환한다.
         */
        public Function<List<T>, List<T>> finisher(){
            return Function.identity();
        }
        /**
         *              > combiner 메소드: 두 결과 컨테이너 병합
         *   마지막으로 리듀싱 연산에서 함수를 반환하는 메소드 combiner는 스트림의 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게
         *   처리할지를 결정한다. 즉, 스트림의 두 번쨰 서브 파트에서 수집한 항목 리스트를 첫 번쨰 서브파트 결과 리스트 뒤에 추가하면 된다.
         *
         */
        public BinaryOperator<List<T>> combiner(){
            return (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            };
        }
        /**
         *   이 메소드를 사용하면 스트림의 리듀싱을 병렬로 수행할 수 있다. 스트림의 리듀싱을 병렬로 수행할 때 자바 7의 포크/조인 프레임워크에서 다룰
         *   Spliterator를 사용한다.
         *
         *      1. 스트림을 분할해야하는지 정의하는 조건이 거짓으로 바뀌기 전까지 원래 스트림을 재귀적으로 분할한다.
         *      2. 모든 서브스트림의 각 요소에 리듀싱 연산을 순차적으로 적용해서 서브스트림을 병렬로 처리할 수 있다.
         *      3. 마지막에는 컬렉터의 combiner 메소드가 반환하는 함수로 모든 부분 결과를 쌍으로 합친다. 즉, 분할된 모든 서브스트림의 결과를
         *      합치면 연산이 완료된다.
         *
         *
         *                > Characteristics 메소드
         *    마지막 characteristics 메소드는 컬렉터의 연산을 정의하는 Characteristic 형식의 불변 집합을 반환한다. Characteristics는
         *    스트림을 병렬로 리듀스할 것인지 그리고 병렬로 리듀스한다면 어떤 최적화를 선택해야 할지 힌트를 제공한다.
         *
         *      1. UNORDERED : 리듀실 경과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
         *      2. CONCURRENT: 다중 쓰레드에서 accumulator 함수를 동시에 호출할 수 있으며 이 컬렉터는 스트림의 병령 리듀싱을 수행할 수 있다.
         *      컬렉터의 플래그에 UNORDERED를 함께 설정하지 않았다면 데이터 소스가 정렬되어 있지 않는 상황에서만 (집합처럼 순서가 무의미한 상황)
         *      병렬 리듀싱을 수행할 수 있다.
         *      3. IDENTITY_FINISH : finisher 메소드가 반환하는 함수는 단순히 identity를 적용할 뿐이므로 이를 생략할 수 있다. 따라서 리듀싱
         *      과정의 최종 결과로 누적자 객체를 바로 사용할 수 있다. 또한 누적자 A를 결과 R로 안전하게 형변환할 수 있다.
         */
    }

    public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

        @Override
        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<T>, T> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<T>> combiner() {
            return (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            };
        }

        @Override
        public Function<List<T>, List<T>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH, Characteristics.CONCURRENT));
        }
    }
    /**
     *              > 컬렉터 구현을 만들지 않고도 커스텀 수집 수행하기
     *  IDENTITY_FINISH 수집 연산에서는 Collector 인터페이스를 완전히 새로 구현하지 않고도 같은 결과를 낼 수 있다. Stream은 세 함수 (발행, 누적, 합침)
     *  를 인수로 받는 collect 메소드를 오버로드하여 각각의 메소드는 Collector 인터페이스의 메소드가 반환하는 함수와 같은 기능을 수행한다.
     *
     *      List<Dish> dishes = menuStream.collect(
     *                                             ArrayList::new,
     *                                             List::add,
     *                                             List::addAll
     *                                             );
     *   위 코드는 이전에 비해서 간결하고 축약되어 있지만 가독성은 떨어진다. 적절한 클래스토 커스텀 클래스를 구현하는 편이 오히려 재사용성을 높이고 중복을
     *   줄이는 데 도움이 된다. 또한 Characteristics를 전달할 수 없다. 즉, IDENTITY_FINISH와 CONCURRENT, UNORDERED
     *
     *
     *
     *                > 6.6  커스텀 컬렉터를 구현해서 성능 개선하기
     *              > Collector 클래스 시그니처 정의 : 소수 판별기
     */
    public static <A> List<A> takeWhile(List<A> list, Predicate<A> p){
        int i = 0;
        for(A item: list){
            if(!p.test(item)){
                return list.subList(0, i);
            }
            i++;
        }
        return list;
    }
    public static boolean isPrime(List<Integer> primes, int candidate){
        int candidateRoot = (int) Math.sqrt((double) candidate);
//        return primes.stream().takeWhile(i -> i <= candidateRoot)
//                .noneMatch(i -> candidate % i == 0);
        return takeWhile(primes, i-> i <= candidateRoot).stream().noneMatch(p -> candidate % p == 0);
    }
    public class PrimeNumbersCollector implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>>{
        @Override
        public Supplier<Map<Boolean, List<Integer>>> supplier() {
            return ()->new HashMap<Boolean, List<Integer>>(){{
                put(true, new ArrayList<Integer>());
                put(false, new ArrayList<>());
            }};
        }
        /**
         * 1. 리듀싱 연산 구현
         * supplier에서는 누적자를 만드는 함수를 반환해야 한다.
         */

        @Override
        public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
            return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
                acc.get( isPrime(acc.get(true), candidate)).add(candidate);
            };
        }
        /**
         * 위 코드는 발견한 소수 리스트와 소수 여부를 확인하는 candidate를 인수로 isPrime을 호출했다. isPrime 호출 결과에 따라서
         * 소수 리스트 또는 비소수 리스트 중 알맞은 리스트로 candidate를 추가한다.
         */

        @Override
        public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
            return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
                map1.get(true).addAll(map2.get(true));
                map1.get(false).addAll(map2.get(false));
                return map1;
            };
        }
        /**
         * 2. 병렬 실행할 수 있는 컬렉터 만들기
         * 두 부분 누적자를 합칠 수 있는 메소드를 만든다. 위 예시에서는 소수 리스트, 비소수 리스트의 모든 수를 첫 번째 Map에 추가한다.
         * 물론 병렬로 돌지 않기 때문에 별 의미는 없다.
         */

        @Override
        public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
        }
    }
    /**
     *  1. collect는 스트림의 요소를 요약 결과로 누적하는 다양한 방법을 인수로 갖는 최종 연산이다.
     *  2. 스트림의 요소를 하나의 값으로 리듀스하고 요약하는 컬렉터뿐 아니라 최솟값, 최댓값, 평균값을 계산하는 컬렉터 등이 미리 정의되어 있다.
     *  3. 미리 정의된 컬렉터인 groupingBy로 스트림의 요소를 그룹화하거나, partitioningBy로 스트림의 요소를 분할할 수 있다.
     *  4. 컬렉터는 다수준의 그룹화, 분할, 리듀싱 연산에 적합하게 설계되어 있다.
     *  5. collector 인터페이스에 정의된 메소드를 구현해서 커스텀 컬렉터를 개발할 수 있다.
     */

}
