package Chapter_10_람다를_이용한_도메인_전용_언어;

import Chapter_10_람다를_이용한_도메인_전용_언어.lambda.LambdaOrderBuilder;
import Chapter_10_람다를_이용한_도메인_전용_언어.mixed.MixedBuilder;
import Chapter_10_람다를_이용한_도메인_전용_언어.nestedBuilder.NestedFunctionOrderBuilder;

import java.util.ArrayList;
import java.util.List;

public class Section1 {
    /**
     *      개발자들은 프로그래밍 언어도 언어라는 사실을 잊고는 한다. 언어의 주요 목표는 메시지를 명확하고 안정적인 방식으로 전달하는 것이다.
     *      프로그램은 사람들이 이해할 수 있도록 작성되어야 하는 것이 중요하며, 기기가 실행하는 것은 부차적이라는 말이 있다.
     *
     *      애플리케이션의 핵심 비지니스를 모델링하는 소프트웨어 영역에서 읽기 쉽고, 이해하기 쉬운 코드는 중요하다. 도메인 전용 언어(DSL)로 애플리케이션
     *      의 비즈니스 로직을 표현함으로써 이 문제를 해결할 수 있다. DSL은 작은, 범용이 아닌 특정 도메인을 대상으로 만들어진 특수 프로그래밍 언어이다.
     *
     *
     *
     *              >  10.1 도메인 전용 언어
     *
     *     DSL은 특정 비즈니스 도메인의 문제를 해결하려고 만든 언어이다. 예시로 회계 전용 소프트웨어 애플리케이션을 개발한다고 했을 때 다양한 개념이
     *     표현되어야 한다. 이러한 문제를 표현할 수 있는 DSL를 만들수 이싿. 자바에서는 도메인을 표현할 수 있는 클래스와 메소드 집합이 필요하다.
     *     DSL이란 특정 비즈니스 도메인을 인터페이스로 만는 API라고 할 수 있다.
     *
     *     DSL은 범용 프로그래밍 언어가 아닌 특정 도메인에 국한되는 언어이다. 그러므로 DSL는 해당 문제만 해결하는 것에 집중하면 된다. 저수준 구현 세부
     *     사항 메소드는 클래스의 비공개로 만들어서 저수준 구현 세부 내용은 숨길 수 있다. 그렇게 하면 사용자 친화적인 DSL을 만들 수 있다. DSL은 아래의
     *     두 가지 필요성을 생각하면서 개발하여야 한다.
     *
     *          1. 의사 소통 : 코드의 의도가 명확히 전달되어야 하므로 프로그래머가 아닌 사람도 이해할 수 있어야 한다. 이런 방식으로 코드가 비즈니스
     *          요구 사항에 부합하는지 알 수 있다.
     *
     *          2. 가독성: 항상 다른 사람이 이해할 수 있게 구현해야한다.
     *
     *
     *              > 10.1.1 DSL의 장점과 단점
     *     DSL은 만병통치약이 아니다. DSL은 코드의 비즈니스 의도를 명확하게 하고 가독성을 높힌다는 장점이 있지만 DSL은 구현은 코드이므로 올바로 검증하고
     *     유지보수해야하는 책임이 따른다. 따라서 DSL의 장점과 비용을 모두 확인해야만 프로젝트에 DSL을 추가하는 것이 투자대비 긍정적인 결과를 가져올지
     *     평가할 수 있다.
     *
     *          1. 간결함 : API는 비즈니스 로직을 간편하게 캡슐화하므로 반복을 피할 수 있고 코드를 간결하게 만들 수 있다.
     *          2. 가독성 : 도메인 영역의 용어를 사용하므로 비 도메인 전문가도 코드를 쉽게 이해할 수 있다. 결과적으로 다양한 조직 구성원 간에
     *          코드와 도메인 영역이 공유될 수 있다
     *          3. 유지보수 : 잘 설계된 DSL로 구현한 코드는 쉽게 유지보수하고 바꿀 수 있다. 유지보수는 비즈니스 관련 코드 즉, 가장 빈번히 바뀌는 애플리
     *          케이션 부분에 특지 중요하다.
     *          4. 높은 수준의 추상화 : DSL은 도메인과 같은 추상화 수준에서 동작하므로 도메인의 문제와 직접적으로 관련되지 않은 세부 사항을 숨긴다.
     *          5. 집중 : 비즈니스 도메인의 규칙을 표현할 목적으로 설계된 언어이므로 프로그래머가 특정 코드에 집중할 수 있다. 결과적으로 생산성이 좋아진다.
     *          6. 관심사 분리 : 지정된 언어로 비즈니스 로직을 표현함으로 애플리케이션의 인프라구조와 관련된 문제와 독립적으로 비즈니스 관련된 코드에서
     *          집중하기가 용이하다. 결과적으로 유지보수가 쉬운 코드를 구현한다.
     *
     *     반면, DSL로 인해 아래와 같은 단점도 있다.
     *
     *          1. DSL 설계의 어려움 : 간결하게 제한적인 언어에 도메인 지식을 담는 것이 쉽지 않다.
     *          2. 개발 : 코드 DSL을 추가하는 작업은 초기 프로젝트에 많은 비용과 시간이 소모되는 작업이다. 또한, DSL 유지보수와 변경은 프로젝트에 부담을 주는 요소이다.
     *          3. 추가 우회 계층 : DSL은 추가적인 계층으로 도메인 모델을 감싸며 이 때 계층을 최대한 작게 만들어 성능 문제를 회피한다.
     *          4. 새로 배워야 하는 언어 : 여러 개를 동시에 사용 하는 것은 부담을 가중시킨다. 또한 이들이 유기적으로 작동하게 하는 것은 어렵다.
     *          5. 호스팅 언어의 한계 : 자바는 꽤나 문법이 엄경한 편이다. 이런 경우는 사용자 친화적으로 DSL을 만들기 어렵다. 그나마 람다로 이를 해소할 수 있다.
     *
     *
     *              > 10.1.2 JVM에서 이용할 수 있는 다른 DSL 해결책
     *     DSL의 카테고리를 구분하는 방법은 마틴 파울러가 소개한 방법으로 내부 DSL, 외부 DSL을 나누는 것이다. 내부 DSL(임베디드 DSL이라고 부른다.)
     *     은 순수 자바 코드 같은 기존 호스팅 언어 기반으로 구현하는 반면, standAlone이라고 불리는 외부 DSL은 호스팅 언어와는 독립적인 자체 문법을 가진다.
     *
     *
     *          > 내부 DSL
     *     역사적으로 자바는 유연성이 떨어지는 문법으로 읽기 쉽고, 간단하고 표현력있는 DSL을 만드는데 한계가 있었다. 람다가 등장하면서 이 부분이 일부
     *     해소됐다. 람다를 활용하면 익명 내부 클래스를 사용해서 DSL을 구현하는 것보다. 가성비를 유지하는 DSL를 만들 수 있다. 자바를 이용하면
     *
     *          1. 기존 자바 언어를 이용하면 외부 DSL에 비해 새로운 패턴과 기술을 익혀 DSL을 구현하는 노력이 현저히 준다.
     *          2. 순수 자바로 DSL을 구현하면 나머지 코드와 함께 DSL을 컴파일 할 수 있다. 따라서 외부 컴파일러, 툴을 이용할 필요가 없다.
     *          3. 기존에 있던 자바 지식으로 구현하기 떄문에 새로운 언어를 배울 필요가 없다.
     *          4. 한 개의 언어로 한 개의 도메인 또는 여러 도메인을 대응하지 못해 추가로 DSL을 개발해야하는 상화엥서 자바를 이용하면 DSL을 쉽게 합칠
     *          수 있다. (다중 DSL)
     *
     *          > 다중 DSL
     *     JVM 위에서 실행되는 언어의 수가 꽤 많기에 이를 이용하면 여러 언어에 대응할 수 있다.
     *
     *
     *          > 외부 DSL
     *     외부 DSL을 개발하는 가장 큰 장점은 무한한 유연성이다. 우리에게 필요한 특성을 제공하는 언어를 설계할 수 있다는 것이 장점이다. 하지만 분리로
     *     DSL과 호스트 언어 사이의 인공 계층이 생기므로 양날의 검이된다.
     *
     *
     *                  > 10.2 최신 자바 API의 작은 DSL
     *     자바의 새로운 기능의 장점을 적용한 첫 API는 네이티브 자바 API 자신이다. 자바 8 이전의 네이티브 자바는 이미 한 개의 추상 메소드를 가진 인터
     *     페이스를 갖고 있었다. 그러나 익명 구현 클래스를 구현하려면 불필요한 코드가 추가되어야 한다. 람다와 메소드 참조는 이 과정을 축약해준다.
     *
     *
     *                  > 10.2.1 스트림 API는 컬렉션을 조작하는 DSL
     *     Stream 인터페이스는 네이티브 자바 API에 작은 내부 DSL을 적용한 좋은 예시이다. 사실 Stream은 컬렉션의 항목을 필터, 정렬, 변환, 그룹화
     *     , 조작하는 DSL이라고 볼 수 있다.
     *
     *
     *
     *                  > 10.2.2 데이터를 수집하는 DSL인 Collectors
     *     Stream 인터페이스를 데이터 리스트를 조작하는 DSL로 간주할 수 있음을 확인했다. 마찬가지로 Collector 인터페이스는 데이터 수집을 수행하는
     *     DSL로 볼 수 있다.
     *
     *
     *                  > 10.3 자바로 DSL을 만드는 패턴과 기법
     */
    public class Stock {
        private String symbol;
        private String market;

        public String getSymbol() {
            return symbol;
        }
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
        public String getMarket() {
            return market;
        }
        public void setMarket(String market) {
            this.market = market;
        }
    }
    public enum Type {BUY, SELL}
    public class Trade {
        private Type type;
        private Stock stock;
        private int quantity;
        private double price;

        public Type getType() {
            return type;
        }
        public void setType(Type type) {
            this.type = type;
        }
        public Stock getStock() {
            return stock;
        }
        public void setStock(Stock stock) {
            this.stock = stock;
        }
        public int getQuantity() {
            return quantity;
        }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        public double getPrice() {
            return price;
        }
        public void setPrice(double price) {
            this.price = price;
        }
    }
    public class Order {
        private String customer;
        private List<Trade> trades = new ArrayList<>();

        public void addTrade(Trade trade){
            trades.add(trade);
        }
        public String getCustomer() {
            return customer;
        }
        public void setCustomer(String customer) {
            this.customer = customer;
        }
        public List<Trade> getTrades() {
            return trades;
        }
        public void setTrades(List<Trade> trades) {
            this.trades = trades;
        }
    }

    {
        Order order = new Order();
        order.setCustomer("BigBank");
        Trade trade1 = new Trade();
        trade1.setType(Type.BUY);

        Stock stock1 = new Stock();
        stock1.setSymbol("IBM");
        stock1.setMarket("NYSE");

        trade1.setStock(stock1);
        trade1.setPrice(125.00);
        trade1.setQuantity(80);
        order.addTrade(trade1);

        Trade trade2 = new Trade();
        trade2.setType(Type.BUY);

        Stock stock2 = new Stock();
        stock2.setSymbol("GOOGLE");
        stock2.setMarket("NASDAQ");

        trade2.setStock(stock2);
        trade2.setPrice(375.00);
        trade2.setQuantity(50);
        order.addTrade(trade2);
    }
    /**
     *     굉장히 장황하다.
     *
     *              > 10.3.1 메소드 체인
     *    메소드 체인을 이용하면 한 개의 메소드 호출 채인으로 거래 주문을 저으이할 수 있다.
     */


    {
        Chapter_10_람다를_이용한_도메인_전용_언어.Order order = MethodChainingOrderBuilder.forCustomer("BigBank")
                .buy(80).stock("IBM").on("NYSE").at(125.00)
                .sell(50).stock("Google").on("NASDAQ").at(375.00)
                .end();
    }
    /**
     * 이와 같이 체이닝을 할 수 있다. 그러나 구현이 복잡하고 특히 빌더를 구현해야하는 것이 문제이다. 상위 수준의 빌더를 하위 수준의 빌더와
     * 연결할 때 많은 접착 코드가 필요하다. 도메인의 객체의 중첩 구조와 일치하게 들려쓰기를 강제하는 방법도 없다.
     *
     *
     *              > 10.3.2 중첩된 함수 이용
     *
     *
     *    중첩된 함수 DSL 패턴은 다른 함수 안에 함수를 이용해 도메인 모델을 만든다.
     */
    {
        Chapter_10_람다를_이용한_도메인_전용_언어.Order order = NestedFunctionOrderBuilder.order("BigBank",
                NestedFunctionOrderBuilder.buy(80,
                        NestedFunctionOrderBuilder.stock("IBM",
                                NestedFunctionOrderBuilder.on("NYSE")),
                        NestedFunctionOrderBuilder.at(125.00)),
                NestedFunctionOrderBuilder.sell(50,
                        NestedFunctionOrderBuilder.stock("GOOGLE",
                                NestedFunctionOrderBuilder.on("NASDAQ")),
                        NestedFunctionOrderBuilder.at(375.00))
        );
//        static import를 하면 당연히 깔끔해진다.
    }
    /**
     *    하지만 괄호가 많다는 점이 문제이다. 더욱이 인수 목록을 정적 메소드에 넘겨줘야한다는 제약도 있다. 도메인 객체에
     *    선택 사항 필드가 있다면 인수를 생략할 수도 있으므로 이 가능성도 염두해서 오버로드를 구현해야한다.
     *    마지막으로 인수의 의미가 이름이 아니라 위치에 의해서 정의된다. 그나마 at(), on() 같은 더미 메소드로 이 문제를 완화시킬 수는 있다.
     *
     *
     *              > 10.3.3 람다 표현식을 이용한 함수 시퀀싱
     *
     */
    {
        Chapter_10_람다를_이용한_도메인_전용_언어.Order order = LambdaOrderBuilder.order(o -> {
            o.forCustomer("BigBank");
            o.buy(t -> {
                t.quantity(80);
                t.price(125.00);
                t.stock(s -> {
                    s.symbol("IBM");
                    s.market("NYSE");
                });
            });

            o.sell(t -> {
                t.quantity(50);
                t.price(375.00);
                t.stock(s -> {
                    s.symbol("Google");
                    s.market("NASDAQ");
                });
            });
        });
    }
    /**
     * 이렇게 람다 표현식을 받아 실행해서 도메인 모델을 만들어낼 수 있다. 이 패턴은 이전 두 가지 DSL 형식의 두 가지 장점을 더한다.
     * 메소드 체인 패턴처럼 플루언트 방식으로 거래 주문을 정의할 수 있다. 또한 중첩 함수 형식처럼 다양한 람다 표현식의 중첩 수준과
     * 비슷하게 도메인 객체의 계층 구조를 유지한다.
     *
     * 그러나 많은 설정코드, DSL 자체가 람다 표현식에 의한 잡음의 영향을 받는다는 단점이 있다.
     *
     *
     *              > 10.3.4 조합하기
     */
    {
         Chapter_10_람다를_이용한_도메인_전용_언어.Order order = MixedBuilder.forCustomer("BigBank",
                 MixedBuilder.buy( t -> t.quantity(80).stock("IBM").on("NYSE").at(125.00)),
                 MixedBuilder.sell( t -> t.quantity(50).stock("GOOGLE").on("NASDAQ").at(125.00)));
    }
    /**
     *
     *   이렇게 세 가지 패턴을 혼용해 가독성있는 DSL을 만들 수 있다.
     *
     *
     *              > 10.4 Java8의 DSl
     *   지금까지의 내용을 요약하면 이렇다.
     *
     *   *** DSL 패턴의 장점과 단점
     *
     *      패턴 이름                   장점                                      단점
     *      메소드 체인      1. 메소드 이름이 키워드 인수 역할을 한다.        1. 구현이 장황하다.
     *                    2. 선택형 파라미터와 잘 동작한다.              2. 빌드를 연결하는 접착 코드가 필요하다.
     *                    3. DSL 사용자가 정해진 순서로 메소드를          3.들여쓰기 규칙으로만 도메인 객체 계층을 정의한다.
     *                    호출하도록 강제할 수 있다.
     *                    4. 정적 메소드를 최소화하거나 없앨 수 있다.
     *                    5. 문법적 잡음을 최소화한다.
     *
     *      중첩 함수       1. 구현의 장황함을 줄일 수 있다.                1. 정적 메소드의 사용이 빈번
     *                    2. 함수 중첩으로 도메인 객체 계층을 반영          2. 이름이 아닌 위치로 인수를 정의한다.
     *                                                             3. 선택형 파라미터를 처리할 떄 메소드 오버로드가 필요하다.
     *
     *      람다를 이용한    1. 선택형 파라미터와 잘 동작                   1. 구현이 장황하다.
     *      함수 시퀀싱     2. 정적 메소드 최소화/ 없앨 수 있다.            2. 람다 표현식으로 문법적 잡음이 DSL에 존재한다.
     *                   3. 람다 중첩으로 도메인 객체 계층을 반영한다.
     *                   4. 빌더의 접착 코드가 없다.
     */
}
