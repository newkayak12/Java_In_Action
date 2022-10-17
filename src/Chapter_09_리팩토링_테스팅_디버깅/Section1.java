package Chapter_09_리팩토링_테스팅_디버깅;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class Section1 {
    /**
     *          >  리팩토링, 테스팅, 디버깅
     *   람다로 가독성과 유연성을 높이려면 어떻게 기존 코드를 리팩토링 해야하는지 알아보자. 또한 람다 표현식으로  Strategy, Template Method, Observer
     *   Chain of Responsibility, Factory 패턴을 어떻게 간소화할 수 있는지도 알아보자.
     *
     *
     *
     *
     *           > 9.1 가독성과 유연성을 개선하는 리팩토링
     *        > 9.1.1 코드 가독성 개선
     *
     *    코드 가독성이란 무엇일까? 코드 가독성이 좋다고 하면 주로 '어떤 코드를 다른 살마도 쉽게 이해할 수 있음'을 의미한다. 즉, 코드 가독성을 개선한다는
     *    것은 우리가 구현한 코드를 다른 사람이 쉽게 이해하고 유지보수 할 수 있게 만드는 것을 의미한다. 코드 가독성을 높이려면 코드의 문서화를 잘 하고,
     *    표준 코딩 규직을 준수하는 것이 기본이다.
     *
     *    더 나아가 자바8의 새로운 기능으로 코드 가독성을 높일 수 있다.
     *      1. 익명 클래스를 람다로 리팩토링
     *      2. 람다를 메소드 참조로 리팩토링
     *      3. 명령형 데이터 처리를 스트림으로 리팩토링
     *
     *
     *        > 9.1.2 익명 클래스를 람다 표현식으로 리팩토링
     *     하나의 추상 메소드를 구현하는 익명 클래스는 람다로 리팩토링할 수 있다. 일단 왜 익명 클래스가 리팩토링 대상이 되는가?를 이해해야한다. 익명 클래스는
     *     코드는 장황하게 하고, 쉽게 에러를 만든다.
     */
    Runnable r1 = new Runnable() {
        @Override
        public void run() {
            System.out.println("Hello");
        }
    };
    Runnable r2 = () -> System.out.println("Hello");

    public Section1() throws IOException {
    }

    /**
     *    그러나 모든 익명 클래스를 람다로 바꿀 수 있는 것은 아니다. 첫번쨰, 익명 클래스에서 사용한 this, super는 람다에서와는 다른 의미를 갖는다.
     *    익명 클래스에서 this는 익명 클래스 자신을 가리키지만 람다는 람다를 감싸는 클래스를 가리킨다. 둘째, 익명 클래스는 감싸고 있는 클래스의 변수를
     *    가릴 수 있다. (스코프가 달라진다.). 하지만 아래와 같이 람다로는 변수를 가릴 수 없다. 마지막으로 익명 클래스를 람다로 바꾸면 context overloading
     *    에 따른 모호함이 초래된다. 익명 클래스는 인스턴스화할 때 명시적으로 형식이 정해지는 반면 람다 형식은 context에 따라 달라진다.
     */
    interface  Task {
        public void execute();
    }
    public static void doSomething(Runnable r){r.run();}
    public static void doSomething(Task a){a.execute();}
    //Task를 구현하는 익명 클래스를 전달할 수 있다.
    {
        doSomething(new Task(){
            @Override
            public void execute() {
                System.out.println("DANGER!");
            }
        });
    }
    /**
     *  하지만 익명 클래스를 람다로 바꾸면 메소드를 호출할 때, Runnable, Task 모두 대상 형식이 될 수 있으므로 문제가 생긴다.
     */
    {
//        doSomething(() -> System.out.println("DANGER"));
        /**
         * Ambiguous method call. Both
         *  >   doSomething
         *      (Runnable) in Section1 and
         * >   doSomething
         *      (Task) in Section1 match
         */

        //해당 문제 해결된 것으로 보인다. -> 가 아니라 IntelliJ가 잡아준다.
    }
    /**
     *
     *          > 9.1.3 람다 표현식을 메소드 참조로 리팩토링
     *   람다 표현식은 쉽게 전달할 수 있는 짧은 코드이다. 하지만 람다 표현식 대신 메소드 참조를 이용하면 가독성을 높일 수 있다. 메소드 참조의 메소드명
     *   으로 코드의 의도를 명확하게 알릴 수 있다.
     */
    List<String> example = Arrays.asList("String", "Integer", "Swift", "Long", "Short", "Byte");
    Map<Integer, List<String>> examples = example.stream().collect(Collectors.groupingBy(String::length));
    int totalLetter = example.stream().map(String::length).reduce(0, (c1, c2) -> c1 + c2);
    /**
     *
     *          > 9.1.4 명령형 데이터 처리를 스트림으로 리팩토링하기
     *    이론적으로는 반복자를 이용한 기존 모든 컬렉션 처리 코드를 스트림 API로 바꿔야한다. 이유가 뭘까? 스트림 API는 데이터 처리 파이프라인의 의도를 더
     *    명확하게 보여준다. 스트림은 쇼트 서킷, LAZY와 같은 최적화뿐만 아니라 멀티코어 아키텍처를 사용할 수 있는 지름길을 제공한다.
     *
     *
     *          > 9.1.5 코드 유연성 개선
     *      1. 함수형 인터페이스 사용 - 조건부 연기 실행
     *    실제 작업을 처리하는 코드 내부 제어 흐름문이 복잡하게 얽혀있는 코드를 흔히 볼 수 있다. 이러한 코드는 불필요한 제어문을 사용하게 하고, 객체의 상태를
     *    클라이언트 코드로 노출시킨다. 이러한 점을 개선하려면 내부적으로 상태 확인하도록 개선하는 것이 좋다.
     *
     *
     *      2. 함수형 인터페이스 사용 - 실행 어라운드
     *    매번 같은 준비, 종료 과정을 반복적으로 수행한다면 이를 람다로 바꿀 수 있다. 한 줄씩 문자열을 읽는 이전의 예시를
     *    미리 구현하여 다양하게 활용할 수 있도록 파라미터화했다.
     */
    public interface BufferedReaderProcessor {
        String process(BufferedReader b) throws IOException;
    }
    public static String processFile(BufferedReaderProcessor p) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader("BufferedReaderProcessor"))){
            return p.process(br);
        }
    }
    String oneLine = processFile((BufferedReader b) -> b.readLine());
    String twoLine = processFile((BufferedReader b) -> b.readLine());
    /**
     *          > 9.2 람다로 객체지향 디자인 패턴 리팩토링
     *
     *    디자인 패턴은 공통적인 소프트웨어 문제를 설계할 때 재사용할 수 있는 검증된 청사진을 제공한다 .디자인 패턴은 재사용할 수 있는 부품으로 여러 가지를
     *    조립해서 만드는 엔지니어링에 비유할 수 있다. 예를 들어 구조체와 동작하는 알고리즘을 서로 분리하고 싶을 때 방문자 디자인 패턴을 사용할 수 있다.
     *    또 다른 예시로 싱글톤을 이용해서 클래스 인스턴스화를 하나의 객체로 제한할 수도 있다.
     *
     *    이러한 디자인 패턴에 람다가 더해지면 색다른 기능을 발휘할 수 있다. 즉, 람다를 이용하면 이전에 디자인 패턴으로 해결하던 문제를 더 쉽고 간단하게
     *    해결할 수 있게 해준다. 또한, 람다로 기존의 많은 객체지향 디자인 패턴을 제거하거나 간결하게 재구성할 수 있다.
     *          1. 전략
     *          2. 템플릿 메소드
     *          3. 옵저버
     *          4. 의무 체인
     *          5. 팩토리
     *     순으로 그 예시를 알아볼 것이다.
     *
     *
     *              > 9.2.1 Strategy Pattern
     *    Strategy 패턴은 한 유형의 알고리즘을 보유한 상태에서 런타임에 적절한 알고리즘을 선택하는 기법이다. 다양한 기준을 갖는 입력값을 검증하거나,
     *    다양한 파싱 방법을 사용하거나, 입력 형식을 설정하는 등 다양한 시나리오에 Strategy 패턴을 응용할 수 있다.
     *
     *                                                          ConcreteStrategyB
     *      클라이언트  --->      Strategy(execute())        〈
     *                                                          ConcreteStrategyA
     *
     *     개요 >
     *          - 알고리즘을 나타내는 인터페이스
     *          - 다양한 알고리즘을 나타내는 한 개 이상의 인터페이스 구현
     *          - Strategy 객체를 사용하는 하나 이상의 클라이언트
     */

    public interface ValidationStrategy{
        boolean execute(String s);
    }
    public class IsAllLowerCase implements  ValidationStrategy{

        @Override
        public boolean execute(String s) {
            return s.matches("[a-z]+");
        }
    }
    public class IsNumeric implements ValidationStrategy {

        @Override
        public boolean execute(String s) {
            return s.matches("\\d+");
        }
    }
    public class Validator {
        private  final  ValidationStrategy strategy;
        public Validator(ValidationStrategy v){
            this.strategy = v;
        }
        public boolean validate(String s){
            return strategy.execute(s);
        }
    }
    Validator numericValidator = new Validator(new IsNumeric());
    boolean b1 = numericValidator.validate("aaa");
    Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
    boolean b2 = numericValidator.validate("aaa");

    /**
     *      > 람다로 리팩토링
     */
    Validator numericValidatorLambda = new Validator((String s) -> s.matches("[a-z]+"));
    boolean a1 = numericValidatorLambda.validate("aa");
    Validator lowerCaseValidatorLambda = new Validator((String s) -> s.matches("\\d+"));
    boolean a2 = lowerCaseValidatorLambda.validate("aa");
    /**
     *  위와 같이 람다를 사용하면 Strategy 패턴에서 발생하는 자잘한 코드를 제거할 수 있으며, 람다로 코드 조각을 캡슐화 한다. 즉, 람다 표현식으로
     *  Strategy 패턴을 대신할 수 있다.
     *
     *
     *              > 9.2.2 템플릿 메소드
     *
     *   알고리즘의 개요를 제시한 이후, 알고리즘의 일부를 고칠 수 있는 유연함을 제공해야 할 때 템플릿 메소드 디자인 패턴을 사용한다. 템플릿 메소드는
     *   '이 알고리즘을 사용하고 싶은데 그대로는 안 되고 조금 고쳐야 하는' 상황에 적합하다.
     */
    class Customer{
        private int id;
        private String userId;

        public String getUserId() {
            return userId;
        }

        public Customer(int id){
            this.id = id;
            this.userId = "user"+id;
        }
    }
    class Database{
        public Customer getCustomerWithId(int id){
            return new Customer(id);
        }

    }
    abstract class OnlineBanking{
        public void processCustomer(int id){
            Customer c = new Database().getCustomerWithId(id);
            makeCustomerHappy(c);
        }
        abstract void makeCustomerHappy(Customer c);
    }
    /**
     * 이와 같은 커스텀이 가능한, 어느 정도 선이 정해진 정도로 만든 템플릿 메소드는 람다와 결합하면 더 간단하게 만들 수 있다.
     */
    class OnlineBankingLambda{
        public void processCustomer(int id, Consumer<Customer> makeCustomerHappy){
            Customer c = new Database().getCustomerWithId(id);
            makeCustomerHappy.accept(c);
        }
    }

    {
        new OnlineBankingLambda().processCustomer(1773, (Customer c) -> System.out.println("Hello, "+c.getUserId()));
    }
    /**
     *  역시 람다로 코드 조각을 넘겨서 로직을 수행하게 만들 수 있다.
     *
     *
     *
     *              > 9.2.3 옵저버
     *  어떤 이벤트가 발생했을 때 한 객체(주체)가 다른 객체 리스트(옵저버)에 자동으로 알림을 보내야하는 상황에서 옵저버 패턴을 사용한다. GUI 애플리케이션
     *  에서 옵저버 패턴이 자주 사용된다.
     */

}
