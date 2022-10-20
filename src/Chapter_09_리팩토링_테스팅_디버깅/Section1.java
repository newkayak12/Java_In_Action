package Chapter_09_리팩토링_테스팅_디버깅;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
     *  에서 옵저버 패턴이 자주 사용된다. 버튼 같은 GUI 컴포넌트에 옵저버를 설정할 수 있다. 그리고 사용자가 버튼을 클릭하면 옵저버에 알림이 전달되고 정해진
     *  동작을 수행한다. 다른 예시로는 주식의 가격 변동에 반응하는 다수의 거래자와 같은 경우에도 옵저버 패턴을 사용할 수 있다.
     *
     *  실제 코드로 옵저버 패턴이 어떻게 동작하는지 살펴보자. 옵저버 패튼으로 트위터 같은 커스터마이즈된 알림 시스템을 설계하고 구현할 수 있다. 다양한
     *  신문 매체가 뉴스 트윗을 구독하고 있으며, 특정 키워드를 포함하는 트윗이 등록되면 알림을 받고 싶어한다.
     *  우선 다양한 옵저버를 그룹화할 Observer 인터페이스가 필요하다. Observer 인터페이스는 새로운 트윗이 있을 때 주제(Feed)가 호출될 수 있도록
     *  notify라는 하나의 메소드를 제공한다.
     */
    interface Observer {
        void notify(String tweet);
    }
    class NYTimes implements Observer {
        @Override
        public void notify(String tweet) {
            if( tweet != null && tweet.contains("money") ){
                System.out.println("Breaking news in NY " + tweet);
            }
        }
    }
    class Guardian implements Observer {
        @Override
        public void notify(String tweet) {
            if( tweet != null && tweet.contains("queen") ){
                System.out.println("Yet more news from London " + tweet);
            }
        }
    }
    class LeMonde implements Observer {
        @Override
        public void notify(String tweet) {
            if( tweet != null && tweet.contains("wine") ){
                System.out.println("Today cheese, wine and news! " + tweet);
            }
        }
    }
    /**
     * 주제도 구현해야한다. 주제는 registerObserver로 새로운 옵저버를 등록하고 notifyObservers로 트윗의 옵저버에 이를 알린다.
     */
    interface Subject {
        void registerObserver(Observer observer);
        void notifyObserver(String tweet);
    }
    class Feed implements Subject {
        private final List<Observer> observers = new ArrayList<>();
        @Override
        public void registerObserver(Observer observer) {
            this.observers.add(observer);
        }

        @Override
        public void notifyObserver(String tweet) {
            observers.forEach(observer -> observer.notify(tweet));
        }
    }
    {
        Feed f = new Feed();
        f.registerObserver(new NYTimes());
        f.registerObserver(new Guardian());
        f.registerObserver(new LeMonde());
        f.notifyObserver("The queen said her favourite book is Modern Java in Action");
        /**
         * Pub-Sub과 Observer의 차이점은
         * Pub-Sub은 MessageBroker, EventBus 존
         * Observer패턴은 Observer와 Subject가 서로를 인지하지만 Pub-Sub패턴의 경우 서로를 전혀 몰라도 상관없습니다.
         * [https://jistol.github.io/software%20engineering/2018/04/11/observer-pubsub-pattern/]
         */
    }
    /**
     *          > 람다 표현식으로 ObserverPattern 구현하기
     */
    {
        Feed f = new Feed();
        f.registerObserver((String tweet) -> {
            if( tweet != null && tweet.contains("money") ){
                System.out.println("Breaking news in NY " + tweet);
            }
        });
        f.registerObserver((String tweet) -> {
             if( tweet != null && tweet.contains("queen") ){
                System.out.println("Yet more news from London " + tweet);
            }
        });
        f.registerObserver((String tweet) -> {
            if( tweet != null && tweet.contains("wine") ){
                System.out.println("Today cheese, wine and news! " + tweet);
            }
        });
        f.notifyObserver("The queen said her favourite book is Modern Java in Action");
    }
    /**
     *  위의 코드는 세 개의 옵저버를 명시적으로 인스턴스화 하지 않고 람다 표현식을 직접 절달해서 실행할 동작을 지정할 수 있다. 그렇다면 항상 람다를 사용해야
     *  할까? 물론 항상은 아니다. 위 예제는 비교적 간단하니 다행이지만 오히려 가독성을 해칠 수도 있다. 따라서 너무 복잡하다면 기존의 클래스로 명시적 구현을
     *  하는 것이 나을 수도 있다.
     *
     *
     *
     *              > 9.2.4 의무 체인
     *
     *  작업 처리 객체의 체이닝을 만들 때 의무 체인 패턴을 사용한다. 한 객체가 어떤 작업을 처리한 다음에 다른 객체로 결과를 전달하고, 다른 객체도 해야할 작업을
     *  다 처리한 다음 다른 객체로 전달하는 식이다. 일반적으로 다음으로 처리할 객체 정보를 유지하는 필드를 포함하는 작업 처리 추상 클래스로 의무 체인 패턴을
     *  구성한다. 작업 처리 객체가 자신의 작업을 끝냈으면 다음 작업 처리 객체로 결과를 전달한다.
     */
    public abstract class ProcessingObject<T>{
        protected ProcessingObject<T> successor;
        public void setSuccessor(ProcessingObject<T> successor){
            this.successor = successor;
        }
        public T handle(T input){
            T r = handleWork(input);
            if(successor != null){
                return successor.handle(r);
            }
            return r;
        }
        abstract protected T handleWork(T input);
    }
    /**
     *   잘 생각해보면 템플릿 메소드 디자인 패턴이 사용되었음을 알 수 있다. handle 메소드는 일부 작업을 어떻게 처리해야 할지 전체적으로 기술한다.
     *   Processing Object 클래스를 상속받아 handleWork 메소드를 구현해야 다영한 종류의 작업 처리 객체를 만들 수 있다.
     *
     *   이 패턴을 어떻게 상요할지 예시를 보자.
     */
    public class HeaderTextProcessing extends ProcessingObject<String>{
        @Override
        protected String handleWork(String text) {
            return "From Raul, Mario and Alan: "+ text;
        }
    }
    public class SpellCheckerProcessing extends ProcessingObject<String>{
        @Override
        protected String handleWork(String text) {
            return text.replaceAll("labda", "lambda");
        }
    }
    /**
     * 두 작업 처리 객체를 연결해서 작업 체인을 만들 수 있다.
     */
    ProcessingObject<String> p1 = new HeaderTextProcessing();
    ProcessingObject<String> p2 = new SpellCheckerProcessing();
    {
        p1.setSuccessor(p2);
        String result = p1.handle("Aren 't labdas really sexy?1");
        System.out.println(result);
    }
    /**
     *          > 람다 표현식 사용
     *  작업 처리 객체를 Function<String,String>, 더 정확히 표현하자면 UnaryOperator<String> 형식의 인스턴스로 표현할 수 있다. andThen
     *  메소드로 이들 함수를 조합해서 체인을 만들 수 있다.
     *
     *  unary > 단항
     */
    UnaryOperator<String> headerProcessing = (String text) -> "From Raul, Mario and Alan : " + text;
    UnaryOperator<String> spellCheckerProcessing = (String text) -> text.replace("labda", "lambda");
    Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
    String result = pipeline.apply("Aren't labdas really sexy?!!");
    /**
     *
     *          > 9.2.5 팩토리
     *  인스턴스화 로직을 클라이언트에 노출하지 않고 객체를 만들 때 팩토리 디자인 패턴을 사용한다. 예를 들어 우리가 은행에서 일하고 있다고 하면 대출, 채권,
     *  주식 등 다양한 상품을 만들어야 한다고 가정하자. 그럼 상품을 만들어주는 것을 담당하는 Factory 클래스가 필요하다.
     */
    public interface Product{
        public void earnMoney();
    }
    public class Loan implements Product{
        @Override
        public void earnMoney() {

        }
    }
    public class Stock implements Product{

        @Override
        public void earnMoney() {

        }
    }
    public class Bond implements Product{

        @Override
        public void earnMoney() {

        }
    }
    public class ProductFactory {
        public /*static*/ Product createProduct(String name){
            switch (name){
                case "loan" :return new Loan();
                case "stock" :return new Stock();
                case "bond" :return new Bond();
                default: throw new RuntimeException("No such Product "+name);
            }
        }

    }
    /**
     * 여기서 Loan, Stock, Bond 모두 Product의 서브 형식이다. createProduct 메소드는 생산된 상품을 설정하는 로직을 포함할 수 있다.
     * 이는 부가적인 기능일 뿐 위 코드의 진짜 장점은 생성자와 설정을 외부로 노출하지 않음으로써 클라이언트가 단순하게 상품을 생산할 수 있다는 것이다.
     */
    Product p = new ProductFactory().createProduct("loan");
    /**
     *      > 람다 표현식
     *  생성자도 메소드 참조처럼 접근할 수 있다. 아래는 Loan을 생성하는 코드이다.
     */
    Supplier<Product> loanSupplier = Loan::new;
    Product loan = loanSupplier.get();
    /**
     * 상품명을 생성자로 연결하는 Map을 만들어서 코드를 재구현할 수도 있다.
     */
   Map<String, Supplier<Product>> productList = Map.of("loan", Loan::new, "stock", Stock::new, "bond", Bond::new);
    /**
     * 이제 Map을 이용해서 팩토리 디자인 패턴에서 했던 것처럼 다양한 상품을 인스턴스화 할 수 있다.
     */
    public Product createProductLambda( String name ){
        Supplier<Product> p = productList.get(name);
        if( p != null ) return p.get();
        throw new IllegalArgumentException("No such product " + name);
    }
    /**
     * 팩토리 패턴이 수행하던 작업이 자바 8의 새로운 기능으로 깔끔하게 정리했다. 하지만 팩토리 메소드 createProduct가 상품 생성자로 여러 인수를 전달하는 상황
     * 에서는 이 기법을 적용하기가 어렵다. 단순한 Supplier 함수형 인터페이스로는 이 문제를 해결할 수 없다.
     *
     * 예를 들어 세 인수 (Integer 둘, 문자열 하나)를 받는 상품의 생성자가 있다고 가정하자. 세 인수를 지원하려면 TriFunction이라는 특별한 함수형 인터페이스를
     * 만들어야 한다. 결국 다음 Map의 시그니쳐가 복잡해진다.
     */
    public interface TriFunction<T,U,V,R>{
        R apply(T t, U u, V v);
    }
    Map<String, TriFunction<Integer, Integer, String, Product>> map = new HashMap<>();
    /**
     *  지금까지 람다 표현식으로 코드 리팩토링하는 방법을 살펴봤다. 이번에는 새로 구현한 코드가 올바른지 어떻게 검증할 수 있는지 알라보자.
     *
     *
     *              > 9.3 람다 테스팅
     *  이제 람다 표현식을 실무에 적용해서 멋지고 간단한 코드를 구현할 수 있다. 하지만 개발자의 최종 업무 목표는 제대로 작동하는 코드를 구현하는 것이지
     *  깔끔한 코드를 구현하는 것이 아니다.
     *
     *  일반적으로 좋은 소프트웨어 공학자라면 프로그램이 의도대로 동작하는지 확인할 수 있는 단위 테스팅 을 진행한다. 우리는 소스 코드가 일부가 예산된 결과를
     *  도출할 것이라 단언하는 테스트 케이스를 구현한다.
     */
    public class Point{
        private final int x;
        private final int y;
        private Point (int x, int y){
            this.x = x;
            this.y = y;
        }
        public int getX(){return x;}
        public int getY(){return y;}
        public Point moveRightBy(int x){
            return new Point(this.x + x, this.y);
        }
    }
    @Test
    public void testMoveRightBy() throws Exception{
        Point p1 = new Point(5,5);
        Point p2 = p1.moveRightBy(10);
        assertEquals(15, p2.getX());
        assertEquals(5,p2.getY());
    }
    /**
     *          > 9.3.1 보이는 람다 표현식의 동작 테스팅
     *   moveRightBy는 public이므로 위 코드는 문제 없이 작동한다. 하지만 람다는 익명함수 이므로 테스트 코드 이름을 호출할 수 없다.
     *   따라서 필요하면 람다를 필드에 저장해서 재사용할 수 있으며, 람다의 로직을 테스트할 수 있다. 메소드를 호출하는 것처럼 람다를 사용할 수 있다.
     *   예를 들어 Point 클래스에 compareByXAndThenY라는 정적 필드를 추가했다고 가정하자. (compareByXAndThenY를 사용하면 메소드 참조로 생성한
     *   Comparator에 접근할 수 있다.)
     *
     *
     *      public class Point {
     *          public final static Comparator<Point> compareByXAndThenY = comparing(Poin::getX).thenComparing(Point::getY)
     *      }
     *
     *   람다 표현식은 함수형 인터페이스의 인스턴스를 생성한다는 사실을 기억하자 따라서 생성된 인스턴스의 동작으로 람다 표현식을 테스트할 수 있다.
     *   다음은 Comparator 객체 compareByXAndThenY에 다양한 인수로 compare 메소드를 호출하면서 예산대로 동작하는지 테스트하는 코드이다.
     *
     *     @Test
     *     public void testComparingTwoPoints() throws Exception{
     *         Point p1 = new Point(10, 15);
     *         Point p2 = new Point(10,20);
     *         int result = Point.compareByXAndThenY.compare(p1,p2)
     *         assertTrue(result < 0);
     *     }
     *
     *
     *              > 9.3.2 람다를 사용하는 메소드 동작에 집중하자
     *   람다의 목표는 정해진 동작을 다른 메소드에서 사용할 수 있도록 하나의 조각으로 캡슐화하는 것이다. 그러려면 세부 구현을 하는 람다 표현식을 공개하지
     *   않아야 한다. 람다 표현식을 사용하는 메소드의 동작을 테스트함으로써 람다를 공개하지 않으면서 람다 표현식을 검증할 수 있다.
     *
     *      public static List<Point> moveAllPointsRighBy(List<Point> points, int x_ {
     *          return point.stream().map(p->new Point(p.getX() + x, p.getY())).collect(toList());
     *      }
     *
     *   위 코드에서 p->new Point(p.getX() + x, p.getY())를 테스트 하는 부분은 없다. 그러나 메소드로서 동작을 확인할 수 있다.
     *
     *
     *              > 9.3.3 복잡한 람다를 개별 메소드로 분할하기
     *    테스트 코드에서 람다 표현식을 참조할 수 없다는 문제점에 직면하게 될 것이다. 그럼 복잡한 람다 표현식을 어떻게 테스트할 것인가? 해답은
     *    람다 표현식을 메소드 참조로 바꾸는 것이다. 그러면 일반 메소드를 테스트하듯이 람다 표현식을 테스트할 수 있다.
     *
     *
     *              > 9.3.4 고차원 함수 테스팅
     *     함수를 인수로 받거나 다른 함수를 반환하는 메소드(이를 고차원 함수라고 한다.)는 조금 더 사용하기 까다롭다. 메소드가 람다를 인수로 받는다면 다른
     *     람다로 메소드의 동작을 테스트할 수 있다.
     *
     *
     *              > 9.4 디버깅
     *     문제가 발생한 코드를 디버깅할 때 두 가지를 먼저 확인해야 한다.
     *      1. 스택 트레이스 : 어디서 멈췄고 왜 멈췄는지를 확인해야한다. 그러나 람다 표현식은 이름이 없기 때문에 복잡한 스택트레이스가 생성된다.
     */
    {
        List<Point> points = Arrays.asList(new Point(12, 2), null);
        points.stream().map(p->p.getX()).forEach(System.out::println);
    }
    /**
     *
     *      stackTrace를 살펴보면
     *              at Debugging.lambda$main$0(Debugging java:6) at Debugging$$Lambda$5/284720968.apply(Unknown.Source)
     *       라고 되어 있는데, (Debugging java:6)의 왼쪽 $0는 무슨 뜻일까? 메소드 참조를 사용하는 클래스와 같은 곳에 선언되어 있는 메소드를 참조
     *       할 때는 메소드 참조 이름이 스택트레이스에 나타난다. 이러한 이유로 스택트레이스으로는 한계가 있다.
     *
     *
     *      2. 로깅 : stream 등에서 peek 등으로 로깅을 하는 것으로 확인할 수 있다.
     */



}
