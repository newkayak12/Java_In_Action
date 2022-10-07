package Chapter_06_스트림으로_데이터_수집;

import java.util.*;
import java.util.stream.Collectors;

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
     */
}
