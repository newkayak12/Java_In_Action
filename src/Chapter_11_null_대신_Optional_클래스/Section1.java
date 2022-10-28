package Chapter_11_null_대신_Optional_클래스;

import Chapter_11_null_대신_Optional_클래스.EX_11_1.Car;
import Chapter_11_null_대신_Optional_클래스.EX_11_1.Insurance;
import Chapter_11_null_대신_Optional_클래스.EX_11_1.Person;
import Chapter_11_null_대신_Optional_클래스.EX_11_4.OptionalCar;
import Chapter_11_null_대신_Optional_클래스.EX_11_4.OptionalInsurance;
import Chapter_11_null_대신_Optional_클래스.EX_11_4.OptionalPerson;

import java.util.Objects;
import java.util.Optional;

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
     *            - Optional을 이용한 Person/Car/Insurance 참조 체인
     */

}
