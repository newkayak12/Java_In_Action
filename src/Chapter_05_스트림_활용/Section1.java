package Chapter_05_스트림_활용;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Section1 {
    /**
     *                  > 스트림 활용
     *      1. 필터링
     *  > Predicate로 필터링
     *  filter는 Boolean을 반환하는 Predicate를 받아서 true인 모든 요소를 포함하는 스트림을 반환한다.
     *
     *  > 고유 요소 필터링
     *  스트림은 고유 요소로 이뤄진 스트림을 반환하는 distinct 메소드도 지원한다. (고유 여부는 hashcode, equals로 결정된다.)
     *
     *      2. 스트림 슬라이싱
     *  > 프레디케이트를 이용한 슬라이싱
     *  자바 9는 스트림 요소를 효과적으로 선택할 수 있도록 takeWhile, dropWhile 두 가지로 새로운 메소드를 지원한다.
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
    /**
     * >> Takewhile
     * filter 연산을 이용하면 전체 스트림을 반복하면 각 요소에 Predicate를 적용하게 된다. 따라서 리스트가 정렬되어 있다는 사실을 이용해서 320 칼로리
     * 보다 크거나 같은 요리가 나왔을 때 반복을 중단할 수 있다. 이러한 연산을 어떻게 수행할 수 있다. takeWhile 연산을 이용하면 이를 쉽게 처리할 수 있다.
     *
     */
    List<Dish> sliceMenu1 = specialMenu.stream().sorted(Comparator.comparingInt(Dish::getCalorie))
            .takeWhile(dish -> dish.getCalorie() < 320).collect(Collectors.toList());
    /**
     * >> Dropwhile
     * 나머지 요소를 선택하려면 어떻게 할까? Dropwhile은  TakeWhile의 정반대 작업을 수행한다.
     */
    List<Dish> sliceMenu2 = specialMenu.stream().sorted(Comparator.comparingInt(Dish::getCalorie))
            .dropWhile(dish -> dish.getCalorie() < 320).collect(Collectors.toList());

    /**
     *      3. 스트림 축소
     * 스트림은 주어진 값 이하의 크기를 갖는 새로운 스트림을 반환하는 limit(n) 메소드를 지원한다. 스트림이 정렬되어 있으면 최대 n개의 요소를 반환할 수 있다.
     * 이를 통해 300 칼로리 이상의 세 요리를 선택할 수도 있다.
     */
    List<Dish> dishesLimit = specialMenu.stream().filter(dish -> dish.getCalorie() > 300).limit(3).collect(Collectors.toList());
    /**
     *      4. 요소 건너뛰기
     *  스트림은 처음 N개의 요소를 제외한 스트림을 반환하는 skip(n) 메소드를 지원한다. n개 이하의 스트림에서 적용하면 빈 스트림이 반환된다.
     */
    List<Dish> dishesSkip = specialMenu.stream().filter(dish -> dish.getCalorie() > 300).skip(2).collect(Collectors.toList());
    /**
     *      5. 매핑
     *  스트림은 함수를 인수로 받는 map 메소드를 지원한다. 인수로 제공된 함수는 각 요소에 적용되며 함수를 적용한 결과가 새로운 요소로 매핑된다. 이 과정은 기존의
     *  값을 고친다는 개념보다는 새로이 만든다는 개념에 가깝기 때문에 변환에 가까운 매핑이다.
     */
    List<String> dishesName = specialMenu.stream().map(Dish::getName).collect(Collectors.toList());
    List<String> words = Arrays.asList("Modern", "Java", "In", "Action");
    List<Integer> wordLengths = words.stream().map(String::length).collect(Collectors.toList());
    /**
     *      6. 스트림의 평면화
     *  메소드 map을 이용해서 리스트의 각 문자를 고유 문자로 이뤄진 리스트로 반환할 수도 있다.
     */
//    List<Character> wordPiece = words.stream().map(i->i.split("")).distinct().collect(Collectors.toList());
    /**
     * 이렇게 하면 List<String[]>가 반환된다. 이를 해결하려 시도로 Arrays.stream을 사용하면 어떻게 될까?
     */
//    List<Character> wordPiece = words.stream().map(i->i.split("")).map(Arrays::stream).distinct().collect(Collectors.toList());
    /**
     * List<Stream<String>> 가 반환된다. 의도된 대로 되지 않는다.
     *
     *
     *          > flatMap 사용하기
     *  위의 문제들은 flatMap을 통해서 해결할 수 있다.
     */
    List<String> uniqueCharacters = words.stream().map(words -> words.split("")).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
    /**
     *  flatMap은 각 배열을 스트림이 아닌 스트림의 콘텐츠로 매핑한다. 즉, map(Arrays::stream)과 달리 flatMap은 하나의 평면화된 스트림을 반환한다.
     *
     *
     *          > 검색과 매칭
     *  특정 속성이 데이터 집합에 있는지 여부를 검색하는 데이터 처리도 자주 사용한다. 스트림 API는 allMatch, anyMatch, noneMatch, findFirst,
     *  findAny 등 다양한 유틸리티 메소드가 있다.
     *
     *      1.Predicate가 적어도 한 요소와 일치하는 하는지 확인
     */
    Boolean anyMatchVegetarian = specialMenu.stream().anyMatch(Dish::getVegetable);
    /**
     *      2. Predicate가 모든 요소와 일치하는지 확인
     */
    Boolean allMatchVegetarian = specialMenu.stream().allMatch(Dish::getVegetable);
    /**
     *      3. NoneMatch는 allMatch와 반대 연산을 수행한다.
     */
    Boolean noneMatchVegetarian = specialMenu.stream().noneMatch(Dish::getVegetable);
    /**
     *      {
     *                  쇼트 서킷 평가
     *         때로는 전체 스트림을 처리하지 않았더라도 결과를 반환할 수 있다. 예를 들어 여러 and로 연결된 커다란 Boolean 표현식을 평가한다고 가정했
     *         을때, 하나라도 거짓이면 나머지와 상관없이 거짓이 나오는 경우가 있다. 이런 상황을 쇼트 서킷이라고 한다. allMatch, noneMatch,
     *         findFirst, findAny 등의 연산은 모든 스트림의 요소를 처리하지 않고도 결과를 반환한다. 원하는 요소를 찾았다면 즉시 결과를 반환한다.
     *         limit 같은 경우도 쇼트 서킷 연산이다.
     *      }
     *
     *      4. 요소 검색
     *  findAny 메소드는 현재 스트림에서 임의의 요소를 반환한다. findAny 메소드는 다른 스트림과 연결해서 사용할 수 있다. 아래의 예시처럼 filter, find
     *  Any를 사용해서 채식 요리를 추려낼 수 있다.
     */
    Optional<Dish> dish = specialMenu.stream().filter(Dish::getVegetable).findAny();
    /**
     *  스트림 파이프라인은 내부적으로 단일 과정으로 실행할 수 있도록 최적화된다.
     *
     *
     *      5. Optional
     *  Optional<T> 클래스(java.util.Optional)는 값의 존재/부재 여부를 표현하는 컨테이너 클래스이다. 이전 예제에서 .finAny는 아무 요소도 반환
     *  하지 않을 수 있다. null은 쉽게 에러를 일으킬 수 있기에 Optional로 null관련 에러를 피할 수 있다. 아래의 메소드를 제공한다.
     *
     *      1. isPresent()는 Optional이 값을 포함하면 true를 반환하고 값을 포함하지 않으면 false를 반환한다.
     *      2. ifPresent(Consumer<T> block)은 값이 있으면 주어진 블록을 실행한다. Consumer 함수형 인터페이스는 T 형식의 인수를 받으며
     *      void를 반환한다.
     *      3. T get()은 값이 존재하면 값을 반환하고 없으면 NoSuchElementException을 일으킨다.
     *      4. T orElse(T other)는 값이 있으면 값을 반환하고, 값이 없으면 기본값을 반환한다.
     *
     *
     *      6. Reducing
     *  지금까지 살펴본 최종 연산은 allMatch 등의 (return boolean) 연산, forEach (return void), 또는 findAny(Optional) 등을 반환했다.
     *  이 절에서는 reduce로 스트림 요소를 조합하여 더 복잡한 질의를 표현하는 방법을 살펴본다. reducing 연산은 모든 스트림 요소를 처리하여 값으로 도출
     *  하는 연산이라고 이해하면 쉽다.
     *
     *  reduce는 두 개의 파라미터를 갖는다.
     *      1. 초기 값 0(등)
     *      2. 두 요소를 조합해서 새로운 값을 만드는 BinaryOperator<T>
     */
    List<Integer> nums = Arrays.asList(1,2,5,2,3,5,1,2,6,1,3,4,23,45,123,23,5,2);
    int sum = nums.stream().reduce(1, (a,b)->a+b);
    /**
     *  여기서 overload된 초기 값이 없는 reduce도 있다. 이 reduce는 Optional 객체를 반환한다.
     */
    Optional<Integer> optionalMultiply = nums.stream().reduce((a,b)->a*b);
    /**
     * 최대, 최소값을 찾을 때도 reduce를 이용할 수 있다.
     *  1. 초기 값
     *  2. 스트림의 두 요소를 합쳐서 하나의 값으로 만드는 데 사용할 람다.
     */
    Optional<Integer> maxNumber = nums.stream().distinct().reduce(Integer::max);
    Optional<Integer> minNumber = nums.stream().distinct().reduce(Integer::min);
    /**
     *      {
     *              reduce 메소드의 장점과 병렬화
     *         기존의 단계적 반복으로 합게를 구하는 것과 reduce를 이용하여 합계를 구하는 것은 어떤 차이가 있을까? reduce를 이용하면 내부 반복이
     *         추상화되면서 내부 구현에서 병렬로 reduce를 실행할 수 있게 된다. 반복적인 합계에서는 sum을 공유해야하므로 병렬화 하기 쉽지 않다.
     *         강제로 동기화 해도 얻는 이득보다 잃는 것이 많다. 여기서 parallelStream과 reduce를 사용하면 병렬성과 공유객체 이슈를 해결할 수 있다.
     *
     *
     *              스트림 연산: 상태 없음, 상태 있음
     *         스트림의 각각의 연산은 내부적인 상태를 고려해야 한다. map, filter 등은 입력 스트림에서 각 요소를 받아 0 또는 결과를 출력 스트림으로 보낸다.
     *         따라서 (사용자가 제공한 람다, 메소드 참조가 내부적인 가변 상태를 갖지 않는다는 가정하에) 이들은 보통 상태가 없는, 즉 내부 상태를 갖지 않는
     *         연산이다. (stateless operation)
     *
     *         하지만 reduce, sum, max 같은 연산은 결과를 누적할 내부 상태가 필요하다. 스트림에서 처리하는 요소 수와 관계 없이 내부 상태의 크기는
     *         한정되어 있다. (bounded)
     *
     *         반면 sorted, distinct 같은 연산은 filter, map처럼 스트림을 입력으로 받아 다른 스트림을 출력하는 것처럼 보일 수 있지만 하지만
     *         sorted나 distinct는 filter, map과는 다르다. 스트림의 요소를 정렬하거나 추가하려면 모든 요소가 버퍼에 추가되어 있어야 한다.
     *         연산을 수행한느데 필요한 저장소 크기는 정해져 있지 않다. 따라서 데이터 스트림의 크기가 크기거나 무한이라면 문제가 생길 수 있다.
     *         이러한 연산은 내부 상태를 갖는 연산이라고 한다.
     *      }
     */

}
