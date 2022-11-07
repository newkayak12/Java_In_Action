package Chapter_11_null_대신_Optional_클래스;

import Chapter_11_null_대신_Optional_클래스.EX_11_1.Car;
import Chapter_11_null_대신_Optional_클래스.EX_11_1.Insurance;
import Chapter_11_null_대신_Optional_클래스.EX_11_1.Person;
import Chapter_11_null_대신_Optional_클래스.EX_11_4.OptionalCar;
import Chapter_11_null_대신_Optional_클래스.EX_11_4.OptionalInsurance;
import Chapter_11_null_대신_Optional_클래스.EX_11_4.OptionalPerson;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Section1 {
    /**
     *
     *                  > 11.1 값이 없는 상황 처리하기
     *
     * 아래의 코드에서는 문제가 발생할 수도 있다.
     */
    public String getCarInsuranceName(Person person){
        return person.getCar().getInsurance().getName();
    }
    /**
     * 문제는 차가 없는 사람이 있을 수 있다는 것이다. 만약 그런 상황이면 NullPointerException이 발생한다. 혹은 Person이 Null이면
     * 어떻게 할까?
     *
     *
     *                  > 11.1.1 보수적인 자세로 NullPointerException 줄이기
     * 예기치 않은 NullPointerException을 피하려면 어떻게 해야할까? 대부분 프로그래머는 필요한 곳에 null 확인 코드를 추가해서 null 예외
     * 문제를 해결한다.
     */
    public String getCarInsuranceName2(Person person){
        if(Objects.nonNull(person)){
            Car car = person.getCar();
            if(Objects.nonNull(car)){
                Insurance insurance = car.getInsurance();
                if (Objects.nonNull(insurance)){
                    return insurance.getName();
                }
            }
        }
        return "Unknown";
    }
    /**
     * 위 코드는 각 상황에 맞춰 null을 검사하고 null이면 문자열을 반환한다. 문제는 위와 같은 처리를 하면 depth가 늘어난다는 것이다.
     * 이를 다른 방법으로 해결해보자.
     */
    public String getCarInsuranceName3(Person person){
        if(Objects.isNull(person)) return "unknown";
        Car car = person.getCar();
        if(Objects.isNull(car)) return "unknown";
        Insurance insurance = car.getInsurance();
        if(Objects.isNull(insurance))  return "unknown";
        return insurance.getName();
    }
    /**
     * 이 역시 그리 좋지 않다. 메소드에 4개의 return 이 있기 떄문이다. 이러면 유지 보수가 어려워진다. 또한 return "unknown"을 여러 번 반복한다.
     * 이 과정에서 오타가 생길 수 있다.
     *
     * 이러한 방법들 보다는 근본적으로 값이 있거나 없을 수 있음을 표현하는 것이 훨씬 세련된 방법이다.
     *
     *
     *                      > 11.1.2 null 때문에 발생하는 문제
     *  1. 에러의 근원 : NullPointerException이 그 예이다.
     *  2. 코드를 어지럽힌다. : 때때로 중첩된 null 확인 코드를 추가해야 하므로 null 떄문에 코드 가독성이 떨어진다.
     *  3. 의미가 없다. : null은 아무 의미도 없다. 특히 정적 형식 언어에서 값이 없음을 표현하는 방법으로는 적절치 않다.
     *  4. 자바 철학에 위배된다. : 자바는 개발자로부터 모든 포인터를 숨겼다. 하지만 예외가 있는데 그것이 바로 null이다.
     *  5. 형식 시스템에 구멍을 만든다. : null은 무형식이며 정보가 없다. 그러므로 모든 참조 형식에 null을 할당할 수 있다. 이런 식으로 null을 할당
     *  하기 시작하면 시스템의 다른 부분으로 null이 퍼졌을 때, 어떤 의미로 null을 썼는지 알 수 없다.
     *
     *
     *
     *                     > 11.2 Optional 클래스 소개
     *  자바8은 하스켈, 스칼라의 영향을 받아서 java.util.Optional<T>이라는 새로운 클래스를 제공한다. Optional은 선택형값을 캡슐화하는 클래스이다.
     *  Optional은 가지고 있을 수도, 가지고 있지 않을 수도 있는 가능성을 내포하며 값이 있으면 Optional 클래스는 값을 감싼다. 값이 없다면 Optional.
     *  empty 메소드로 Optional을 반환한다.
     */
    public String getCarInsuranceName4(OptionalPerson person){
        OptionalCar car = person.getCar().orElseGet(()->new OptionalCar());
        OptionalInsurance insurance = car.getInsurance().orElseGet(()->new OptionalInsurance());
        return insurance.getName();
    }
    /**
     *  예를 들어 이런 식으로 null을 피할 수 있다.
     *
     *                      > 11.3 Optional 적용 패턴
     *                  > 11.3.1 Optional 객체 만들기
     *  Optional을 사용하려면 Optional 객체를 만들어야 한다. 다양한 방법으로 Optional을 만들 수 있다.
     *
     *      1. 빈 Optional
     *      정적 팩토리 메소드 Optional.empty로 빈 Optional을 얻을 수 있다.
     */
    Optional<Car> optCar = Optional.empty();
    /**
     *
     *      2. null 이 아닌 값을 Optional 만들기
     *      또는 정적 팩토리 메소드 Optional.of로 null이 아닌 값을 포함하는 Optional을 만들 수 있다.
     */
    Optional<Car> optCar2 = Optional.of(new Car());
    /**
     *
     *      만약 of에 들어가는 값이 null이면 NullPointerException이 발생한다.
     *
     *      3. Null 값으로 Optional 만들기
     */
    Optional<Car> optCar3 = Optional.ofNullable(null);
    /**
     *
     *      of와 같지만 null을 허용한다.
     *
     *
     *                  > 11.3.2 Map으로 Optional 추출하고 변환하기
     *  보통 객체의 정보를 추출할 때는 Optional을 사용할 때가 많다. 이런 유형의 패턴에 사용할 수 있도록 Optional은 map 메소들르 지원한다.
     *  다음 코드를 살펴보자.
     */
    Optional<OptionalInsurance> optionalInsurance = Optional.ofNullable(null/*insurance*/);
    Optional<String> name = optionalInsurance.map(OptionalInsurance::getName);
    /**
     *  스트림의 Map과 개념적으로 유사하다. Optional이 값을 포함하면 map의 인수로 제공된 함수가 값을 바꾼다. Optional이 비어있다면 아무 일도
     *  일어나지 않는다.
     *
     *
     *                  > 11.3.3 flatMap으로 Optional 객체 연결
     */
    Optional<Person> optPerson = Optional.of(null/*person*/);
    Optional<String> nameMap = optPerson.map(Person::getCar).map(Car::getInsurance).map(Insurance::getName);//컴파일 불가
    /**
     *  아쉽지만 위 코드는 컴파일 되지 않는다. 이는 반환되면서 Optional이 중첩 래핑되기 떄문이다. 물론 대안은 있다. flatMap이다. flatMap은
     *  인수로 받은 함수를 적용해서 생성된 각각의 스트림에서 콘텐츠만 남긴다. 아래와 같이 사용할 수 있다.
     */
    Optional<OptionalPerson> person = Optional.of(null);
    String nameFlatMap = person.flatMap(OptionalPerson::getCar).flatMap(OptionalCar::getInsurance).map(OptionalInsurance::getName).orElse("unknown");
    /**
     *          < 도메인 모델에 Optional을 사용했을 때 직렬화 할 수 없는 이유 >
     *   Optional을 초기 목표가 선택형 반환 값을 지원하는 것이었다. 그래서 Optional 클래스는 필드 형식으로 사용할 것을 가정하지 않았으므로 Serializable
     *   인터페이스를 구현하지 않는다. 따라서 도메인 모델에서 Optional을 사용하면 직렬화 모델을 사용하는 도구나 프레임워크에서 문제가 생길 수 있다.
     *
     *
     *
     *
     *                  > 11.3.4 Optional 스트림 조작
     *  자바 8에서는 Optional을 포함하는 스트림을 쉽게 처리할 수 있도록 Optional에 stream() 메소드를 추가했다. Optional 스트림을 값을 가진
     *  스트림으로 변환할 때 이 기능을 유용하게 활용할 수 있다.
     */
    public Set<String> getCarInsuranceNames(List<OptionalPerson> persons){
        return persons.stream()
                .map(OptionalPerson::getCar)
                .map(optCar -> optCar.flatMap(OptionalCar::getInsurance))
                .map(optIns -> optIns.map(OptionalInsurance::getName))
                .flatMap(Optional::stream).collect(Collectors.toSet());
    }
    /**
     *  보통 스트림 요소를 조작하려면 변환, 필터 등의 일련의 여러 긴 체인이 필요한데, 이 예제는 Optional로 값이 싸여 있으므로 과정이 더 복잡해졌다.
     *  첫 번째 map을 사용하고 Stream<Optional<Car>>를 얻는다. 이어지는 map으로 Optional<Car> -> Optional<Insurance>로 변환된다.
     *  이러한 변환 이후 마지막에 Optional을 제거하고 값을 언랩하는 것이 문제이다.
     */
    List<OptionalPerson> persons = new ArrayList<>();
    public Set<String> getCarInsuranceNames2(List<OptionalPerson> persons){
        Stream<Optional<String>> stream = persons.stream()
                .map(OptionalPerson::getCar)
                .map(optCar -> optCar.flatMap(OptionalCar::getInsurance))
                .map(optIns -> optIns.map(OptionalInsurance::getName));
        return  stream.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }
    /**
     * 이와 같이 Optional 클래스의 stream 메소드를 이용하면 한 번의 연산으로 같은 결과를 얻어낼 수 있다.
     *
     *
     *
     *              > 11.3.5 디폴트 액션과 Optional 언랩
     *   1. get()은 값을 읽는 가장 간단한 메소드이면서 동시에 가장 안전하지 않은 메소드이다. 메소드 get은 래핑된 값이 있으면 해당 값을 반환하고
     *   값이 없으면 NoSuchElementException이 발생한다. 따라서  Optional에 값이 반드시 있다고 가정할 수 있는 상황이 아니라면 get을 사용하지
     *   않는 것이 좋다.
     *
     *   2. orElse() 는 Optional에 값이 없을 때 기본 값을 제공할 수 있다. Else를 항상 읽는다.
     *
     *   3. orElseGet(Supplier<? extents T> other)은 orElse의 lazy 버전이다. Optional에 값이 없을 떄만 Supplier가 실행된다. 디폴트
     *   메소드를 만드는 데 시간이 걸리거나 Optional이 비어있을 때만 기본값을 생성한다.
     *
     *   4. orElseThrow(Supplier<? extents T> exceptionSupplier)는 Optional이 비어있을 때 예외를 발생시킨다는 점에서 get과 같지만
     *   발생시킬 예외의 종류를 선택할 수 있다.
     *
     *   5. ifPresentOrElse(Consumer<? and T> action, Runnable emptyAction 이 메소드는 Optional이 비었을 때 실핼할 수 있는 Runnable
     *   을 인수로 받는점만 ifPresent와 다르다.
     *
     *
     *
     *              > 11.3.6 두 Optional 합치기
     *   이제 Person, Car 정보를 이용해서 가장 저렴한 보험료를 제공하는 보험회사를 찾는 몇몇 복잡한 비즈니스 로직을 구현한 외부 서비스가 있다고 해보자.
     *   이 때 두 Optional을 인수로 받아서 Optional<Insurance>를 반환하는 null-safe 메소드를 구현해야한다고 가정하자. 인수로 전달한 값 중
     *   하나라도 비어있으면 빈 Optional<Insurance>를 반환한다. Optional 클래스는 Optional이 값을 포함하는지 여부를 알려 주는 ifPresent라는 메소드도
     *   제공한다.
     */
    public OptionalInsurance findCheapestInsurance(OptionalPerson person, OptionalCar car){
        return new OptionalInsurance();
    }
    public Optional<OptionalInsurance> nullSafeFindCheapestInsurance(Optional<OptionalPerson> person, Optional<OptionalCar> car){
//        if(person.isPresent() && car.isPresent()){
//            return Optional.of(findCheapestInsurance(person.get(), car.get()));
//        } else {
//            return Optional.empty();
//        }
        return person.flatMap(p -> car.map(c -> findCheapestInsurance(p,c)));
    }
    /**
     * 이러한 구성의 장점은 두 매개변수로 아무 정보도 없을 수 있다는 것을 명시적으로 보여준다는 것이다.
     *
     *
     *          > 11.3.7 필터로 특정값 거르기
     *    종종 객체의 메소드를 호출해서 어떤 프로퍼티를 확인해야 할 때가 있다. 이 작업을 안전하게 수행하려면 객체가 null인지 확인한 다음에 getName을 호출해야한다.
     *
     *    Insurance insurance = .... ;
     *    if(insurance != null && "CambridgeInsurance".equals(insurance.getName())){
     *        System.out.println("OK")
     *    }
     *
     *    Insurance insurance = .... ;
     *    optInsurance.filter(insurance -> "CambridgeInsurance".equals(insurance.getName())).ifPresent(x -> System.out.println("OK"));
     *    와 같이 고칠 수 있다.
     *
     *    filter는 Predicate를 인수로 받는다. Optioanl 객체가 값을 가지며 Predicate와 일치하면 filter는 그 값을 반환하고 그렇지 않으면
     *    빈 Optional을 반환한다.
     *
     *
     *
     *          >11.4 Optional을 사용한 실용 예제
     *    Optional을 효과적으로 사용하려면 잠재적으로 존재하지 않는 값의 처리 방법을 바꿔야한다. 즉 코드 구현만이 아니라 네이티브 자바 API와 상호작용
     *    하는 방식도 바꿔야 한다.
     *
     *
     *          >11.4.1 잠재적으로 null이 될 수 있는 대상을 Optional로 감싸기
     *    기존 자바 API에서는 null을 반환하면서 요청한 값이 없거나 어떤 문제로 계산에 실패했음을 알린다. 대부분의 상황에서 null 반환보다 Optional을
     *    반환하는 것이 더 바람직하다. 이러한 코드는 잠재적인 NullPointerException과 null처리 과정을 줄여준다.
     *
     *
     *          >11.4.2 예외와 Optional 클래스
     *    자바 API는 어떤 이유에서 값을 제공할 수 없을 때 null을 반환하는 대신 예외를 발생시킬 때도 있다. Integer.parseInt의 NumberFormatException이
     *    그 예시이다. 이러한 문제도 Optional로 해결할 수 있다. 즉 parseInt가 Optional을 반환하도록 할 수 있다.
     *
     */
    public Optional<Integer> stringToInt(String s){
        try{
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e){
            return Optional.empty();
        }
    }
    /**
     *
     *          > 11.4.3 기본형 Optional을 사용하지 말아야 하는 이유
     *  스트림처럼 Optional도 기본형으로 특화된 OptionalInt, OptionalLong, OptionalDouble 등의 클래스를 제공한다. 하지만 Optional의
     *  최대 요소 수는 한 개이므로 Optional에서는 기본형 특화 클래스로 성능을 개선할 수 있다. 그러나 기본형 특화 Optional은 map, flatMap,
     *  filter 등을 지원하지 않는다.
     *
     *
     *          > 11.4.4 응용
     *  Optional 클래스의 메소드를 실제 업무에서 어떻게 활용할지 살펴보자.
     */
    Properties properties = new Properties();
    {
        properties.setProperty("a", "5");
        properties.setProperty("b", "true");
        properties.setProperty("c", "-3");
    }
    public int readDuration ( Properties props, String name ) {
        String value = props.getProperty(name);
        if( value != null ){
            try {
                int i = Integer.parseInt(value);
                if ( i > 0 ) return i;
            } catch ( NumberFormatException nfe ) { }
        }
        return 0;
    }
    /**
     *   위 코드를
     */
    public int readDurationOptional ( Properties props, String name ) {
        return Optional.ofNullable(props.getProperty("name")).flatMap(i->stringToInt(i)).filter(i -> i > 0).orElseGet(() -> 0);
    }
    /**
     * 와 같이 변경할 수 있다.
     */
}
