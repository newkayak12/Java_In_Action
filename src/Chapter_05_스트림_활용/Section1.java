package Chapter_05_스트림_활용;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
     *
     *       7. 숫자형 스트림
     *  스트림에는 숫자 스트림을 효율적으로 처리하는 기본형 특화 스트림(primitive stream specialization)을 제공한다. 첫 번쨰는 박싱 비용을 피할 수
     *  있도록 각 원시 타입에 특화된 스트림이 존재하며, 스트림 합계를 계산하는 sum,  최댓값/최소값을 위한 max/min을 제공하며 또한, 필요할 때 다시 객체
     *  스트림으로 복원하는 기능도 제공한다.
     *
     *      7-1. 숫자 스트림으로 매핑
     *  스트림을 특화 스트림으로 변환할 떄는 mapToInt, mapToDouble, mapToLong 세 가지 메소드를 가장 많이 사용한다. 이들은 Map과 같은 기능을 하지만
     *  Stream<T> 대신 특화된 스트림을 반환한다.
     */
    int calories = specialMenu.stream().mapToInt(Dish::getCalorie).sum();
    /**
     *      7-2. 객체 스트림으로 복원하기
     */
    IntStream intStream = specialMenu.stream().mapToInt(Dish::getCalorie);
    Stream<Integer> stream = intStream.boxed();
    /**
     *      7-3. 스트림 합계와 최대/최소에서의 Optional
     *  만약 스트림에 요소가 없는 상황과 실제 최댓값이 0인 상황을 어떻게 구별할 수 있을까? 이전에 Optional에 대해서 본적이 있다. Optional을 Integer
     *  ,String 등의 참조 형식으로 파라미터화할 수 있다. 또한 OptionalInt, OptionalDouble, OptionalLong 세 가지 기본형 특화 스트림도 존재한다.
     */
    OptionalInt maxCalories = specialMenu.stream().mapToInt(Dish::getCalorie).max();
    /**
     *      7-4. 숫자 범위
     *  프로그램에서 특정 범위의 숫자를 이용해야하는 경우가 생긴다. 예를 들어 1 ~ 100의 숫자를 생성한다고 가정해보자. IntStream, LongStream에서는
     *  range, rangeClosed라는 두 가지 정적 메소드가 있다. 두 메소드 모두 첫 번째 인수 ~ 두 번째 인수로 끝나는 범위를 만들어준다. range 시작, 종료
     *  값이 포함되지 않는 반면 rangeClosed는 시작, 종료 값이 결과에 포함된다.
     *
     */
    IntStream evenNumbers = IntStream.rangeClosed(1, 100).filter(n -> ( n % 2 )== 0);
    {
        evenNumbers.forEach(System.out::println);
    }
    /**
     *      8. 스트림 만들기
     *  스트림을 꼭 컬렉션을 통해서만 만들 수 있는 것은 아니다. 일련의 값, 배열, 파일, 함수를 가지고 스트림을 만들 수도 있다.
     *
     *      8-1. 값으로 스트림 만들기
     */
    Stream<String> stringStream = Stream.of("Modern", "Java", "In", "Action");
    {
        stringStream.map(String::toUpperCase).forEach(System.out::println);
    }
    /**
     *      8-2. null이 될 수 있는 객체로 스트림 만들기
     *   자바 9부터 null이 될 수 있는 개체를 스트림으로 만들 수 있는 메소드가 추가되었다. 때로는 null이 될 수 있는 객체를 으로 만들어야할 수 있다.
     *   예를 들어서 System.getProperty는 제공된 키에 대응하는 속성이 없으면 null을 반환한다. 이런 메소드를 스트림에 활용하려면 null을
     *   명시해야한다.
     */
    String homeValue = System.getProperty("home");
    Stream<String> homeValueStream = homeValue == null? Stream.empty() : Stream.of(homeValue);
    /**
     * 위와 같은 코드를 자바 9에서는
     */
    Stream<String> nullStream = Stream.ofNullable(System.getProperty("home"));
    /**
     *      8-3. 배열로 스트림 만들기
     *  배열을 인수로 받는 정적 메소드 Arrays.stream을 이용해서 스트림을 만들 수 있다.
     */
    int[] numbers = {2,3,4,5,1,2,3,5,12,3,23};
    int sumStreamValue = Arrays.stream(numbers).sum();
    /**
     *      8-4. 파일로 스트림 만들기
     *  파일을 처리하는 등의 I/O 연산에 사용하는 자바의 NIO API(논블로킹 I/O)도 스트림 API를 활용할 수 있도록 업데이트 되었다. java.nio.file.Files
     *  의 많은 정적 메소드가 스트림을 반환한다. 예를 들어 Files.inlines는 주어진 파일의 행 스트림을 문자열로 반환한다.
     */
    {
        Long uniqueWords = 0L;
        try(Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())){
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" "))).distinct().count();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * 위의 예시는 파일에서 고유한 단어 개수를 얻는 로직이다.
     *
     *
     *      8-5. 함수로 무한 스트림 만들기
     * 스트림 API 함수에서 스트림을 만들 수 있는 두 정적 메소드 Stream.iterate와 Stream.generate를 제공한다. 두 연산을 이용해서 무한 스트림,
     * 즉 고적된 컬렉션에서 고정된 크기로 스트림을 만들었던 것과는 달리 크기가 고정되지 않은 스트림을 만들 수 있다. iterate, generate에서 만들
     * 만든 스트림은 요청할 때마다 주어진 함수를 이용해서 값을 만든다. 따라서 무제한으로 값을 계산할 수 있다. 하지만 보통 무한한 값을 출력하지 않도록
     * limit(n) 함수를 함께 연결해서 사용한다.
     *
     *      8-5-1. iterate 메소드
     */
    {
        Stream.iterate(0, n -> n + 2).limit(10).forEach(System.out::println);
        //For를 대체할 수 있을 듯?
    }
    /**
     *  iterate는 요청할 때마다 값을 생산할 수 있으며, 끝이 없으므로 무한 스트림을 만든다. 이러한 스트림을 Unbounded Stream이라고 표현한다.
     */
    {
        //피보나치 수열
        Stream.iterate(new int[] {0, 1}, arr -> new int[]{arr[1], arr[0]+arr[1]}).limit(20).forEach(System.out::println);
    }
    /**
     *
     *      8-5-2. generate 메소드
     *  iterate와 비슷하게 generate도 요구할 때 값을 계산하는 무한 스트림을 만들수 있다. 하지만 iterate와 달리 generate는 생산된 각 값을 연속적으로
     *  계산하지는 않는다. generate는 Suppler<T>를 인수로 받아 새로운 값을 생성한다.
     */
    {
        Stream.generate(Math::random).limit(5).forEach(System.out::println);
    }
    /**
     *  여기서 generate에 대해서 의문을 가질 수 있다. 우리가 사용한 Supplier는 상태가 없는 메소드, 즉 나중에 계산에 사용할 어떠한 값도 저장해 놓지 않는다.
     *  하지만 Supplier에 상태가 없어야 하는 것은 아니다. Supplier가 상태를 저장한 다음에 스트림의 다음 값을 만들 때 상태를 고칠 수도 있다. 여기서
     *  문제가 될만한 점은 병렬 스트림에서 Supplier에 상태가 있으면 안전하지 않다는 것이다. 따라서 generate의 Supplier에 상태를 갖는 것은
     *  지양하는 것이 좋다.
     */
    IntStream twos = IntStream.generate(new IntSupplier() {
        @Override
        public int getAsInt() {
            return 2;
        }
    });
    /**
     *  람다와 익명 구현 객체의 차이점은 getAsInt를 커스터마이징 할 수 있는 상태 필드를 정의할 수 있다는 점이다. 이 점이 부작용을 야기할 수 있는 예시이다.
     *  반면 람다는 상태를 바꾸거나 하지는 않는다.
     */
    IntSupplier fib = new IntSupplier() {
        private int previous = 0;
        private int current = 1;
        @Override
        public int getAsInt() {
            int oldPrev = this.previous;
            int nextValue = this.previous + this.current;
            this.previous = this.current;
            this.current = nextValue;
            return oldPrev;
        }
    };
    {
        IntStream.generate(fib).limit(10).forEach(System.out::println);
    }
    /**
     *
     */
}
