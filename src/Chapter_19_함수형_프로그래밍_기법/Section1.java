package Chapter_19_함수형_프로그래밍_기법;

import java.util.function.DoubleUnaryOperator;

public class Section1 {
    public static void main(String[] args) {
        /**
         *
         *                      > 19.1 함수는 모든 곳에 좆재한다.
         *  18장에서 함수형 프로그래밍이란 함수나 메소드가 수학의 함수처럼 동작함을, 즉 부작용 없이 동작함을 의미했다. 함수형 언어 프로그래머는 함수형
         *  프로그래밍이라는 용어를 좀 더 폭넓게 사용한다. 즉, 함수를 마치 일반값처럼 사용해서 인수로 전달하거나, 결과로 반환받거나, 자료 구조에 저장할
         *  수 있음을 의미한다. 일반값처럼 취급할 수 있는 함수를 일급 함수라고 한다. 바로 자바8이 이전 버전과 구별되는 특징 중 하나가 일급 함수를
         *  지원한다는 것이다. 자바 8은 :: 연산자로 메소드 레퍼런스를 만들거나 (int x) -> x + 1과 같은 람다 표현식으로 직접 함숫값을 표현해서
         *  메소드를 함숫값으로 사용할 수 있다. 자바 8에서는 다음과 같은 메소드 참조로  Integer.parseInt를 저장할 수 있다.
         *
         *          Functional<String,Integer> strToInt = Integer::parseInt;
         *
         *
         *                      > 19.1.1 고차원 함수
         *
         *  지금까지 함숫값을 자바 8 스트림 처리 연산으로 전달하거나 filterApples에 함숫 값으로  Apple::isGreenApple을 전달해서 동작 파라미터화를
         *  달성하는 용도로만 사용했다. 이는 함숫값 활용의 일부에 불과하다. 아래는 함수를 인수로 받아서 다른 함수로 반환하는 정적메소드 Comparator.compaing도
         *  있었다.
         *
         *
         *          Comparator<Apple> c = comparing(Apple::getWeight);
         *
         *   함수형 프로그래밍에서 Comparator.comparing과 같이 하나 이상의 동작을 수행하는 함수를 고차 함수(high-order functions)라고 한다.
         *
         *      1. 하나 이상의 함수를 인수로 받음
         *      2. 함수를 결과를 반환
         *
         *   자바 8에서는 함수를 인수로 전달할 수 있을 뿐 아니라 결과로 반환하고, 지역 변수로 할당하거나 구조체로 삽읿할 수 있으므로 자바 8의 함수도
         *   고차원 함수라고 할 수 있다.
         *
         *              {
         *                                          부작용과 고차원 함수
         *                7 장에서 스트림 연산으로 전달하는 함수는 부작용이 없어야 하며, 부작용을 포함하는 함수를 사용하면 문제가 발생한다는 사실을
         *                설명했다. (부작용을 포함하는 함수를 사용하면 부정확한 결과가 발생하거나 레이스 컨디션 떄문에 예상치 못한 결과가 발생할 수 있다.)
         *                고차원 함수를 적용할 떄도 같은 규칙이 적용된다. 고차원 함수나 메소드를 구현할 때 어떤 인수가 전달될지 알 수 없으므로
         *                인수가 부작용을 포함할 가능성을 염두에 두어야 한다. 함수를 인수로 받아서 사용하면서 코드가 정확히 어떤 작업을 수행하고
         *                프로그램의 상태를 어떻게 바꿀지 예측하기 어려워지며 디버깅도 어려워진다. 따라서 인수로 전달된 함수가 어떤 부작용을 포함하게
         *                될지 정확하게 문서화하는 것이 좋다. 물론 부작용이 없는게 최선일 것이다.
         *              }
         *
         *   이제 함수롤 모듈화하고 코드를 재사용하는 데 도움을 주는 기법인 커링을(currying)을 살펴보자.
         *
         *                          > 19.1.2 커링(Currying)
         *   대부분의 애플리케이션은 국제화를 지원해야하는데 이때 단위 변환 문제가 발생할 수 있다. 보통 변환 요소와 기준치 조정 요소가 단위 변환 결과를
         *   좌우한다. 예를 들어 섭씨를 화씨로 변환하는 공식이다.
         *
         *          CtoF(x) = x * 9 / 5 + 32
         *
         *   다음과 같은 패턴으로 단위를 표현할 수 있다.
         *
         *   1. 변환 요소를 곱한다.
         *   2. 기준치 조정 요소를 적용한다.
         *
         *   아래와 같은 메소드로 변환 패턴을 표현할 수 있다.
         *
         *   static double converter(double x, double f, double b) {
         *      return x * f + b;
         *   }
         *
         *
         *   여기서 x는 변환하려는 값, f는 변환 요소, b는 기준치 조정 요소이다. 온도뿐 아니라 킬로미터와 마일 등의 단위도 변환해야 한다. 세 개의 인수를 받는
         *   converter라는 메소드를 만들어 해결하는 방법이 있겠지만 인수에 변환 요소와 기준치를 넣는 일은 귀찮은 일이머 오타도 발생할 수 있다.
         *   각각의 변환 메소드를 따로 만드는 방법도 있지만 그러면 로직을 재활용하지 못한다는 단점이 있다.
         *
         *   기존 로직을 활용해서 변환기를 특정 상황에 적용할 수 있는 방법이 있다. 다음은 커링이라는 개념을 활용해서 한 개의 인수를 갖는 변환 함수를 생산하는
         *   팩토리를 정의하는 코드이다.
         *
         *                  static DoubleUnaryOperator curriedConverter( double f, double b  ) {
         *                      return (double x) -> x * f + b;
         *                  }
         */
        DoubleUnaryOperator convertCtoF = Currying.curriedConverter(1,2);
        DoubleUnaryOperator convertUSDtoGBP = Currying.curriedConverter(1,2);
        DoubleUnaryOperator convertKmtoMi = Currying.curriedConverter(1,2);
        System.out.println(convertCtoF.applyAsDouble(1));
        System.out.println(convertUSDtoGBP.applyAsDouble(1));
        System.out.println(convertKmtoMi.applyAsDouble(1));
        /**
         *          {
         *                                  커링의 이론적 정의
         *             커링 x와 y라는 두 인수를 받는 함수를 f를 한 개의 인수를 받는 g라는 함수로 대체하는 기법이다. 이때  g라는 함수 역시 하나의
         *             인수를 받는 함수를 반환한다. 함수 g와 원래 함수 f가 최종적으로 반환하는 값은 같다. 즉 f(x,y) = (g(x))(y)가 성립된다.
         *
         *             물론 이 과정을 일반화 할 수 있다. 이와 같은 여러 과정이 끝까지 완료되지 않은 상태를 가리켜 함수가 부분적으로 적용되었다고 한다.
         *          }
         *
         *
         *
         *                                  > 19.2 영속 자료 구조
         *
         *   함수형 프로그램에서 사용하는 자료 구조를 살펴보자. 함수형 프로그램에서는 함수형 자료구조, 불변 자료구조 등의 용어도 사용하지만 보통은 영속
         *   자료구조라고 부른다. (여기서 영속은 DB에서의 영속과는 다른 의미이다.) 함수형 메소드에서 전역 자료구조나 인수로 전달된 구조를 갱신할 수 없다.
         *   왜 그럴까? 자료 구조를 바꾼다면 같은 메소드를 두 번 호출했을 떄 결과가 달라지면서 참조 투명성에 위배되고 인수를 결과로 단순하게 매핑할 수
         *   있는 능력이 상실된다.
         *
         *
         *
         *                                  > 19.2.1 파괴적 갱신과 함수형
         *  자료구조를 갱신할 때 발생할 수 있는 문제를 확인해보자. A~B까지 기차여행을 의미하는 가변 TrainJourney 클래스가 있다고 치자. TrainJourney
         *  는 간단한 단방향 연결 리스트로 구현되며 여행 구간의 가격 등 상세 정보를 포함하는 int 필드를 포함하낟. 다음 코드에서 보이는 것처럼 기차여행에서는
         *  여러 TrainJourney 객체를 연결할 수 있는 onward(이어지는 여정을 의미)라는 필드가 필요하다. 직통, 마지막 여정은 onward가 null이 된다.
         *
         *
         *                          Chapter_19_함수형_프로그래밍_기법/TrainJourney.java
         *
         *   이때 X에서 Y까지 그리고 Y ~ Z까지 여행을 나타내는 별도의 TrainJourney가 있다고 가정하자 아마 두 개의 TrainJourney로 하나의 여행을
         *   만들 수 있을 것이다. 기존의 단순한 명령한 메소드라면 이 두 기차여행을 연결할 것이다.
         *
         *
         *                          static TrainJourney link (TrainJourney a, TrainJourney b){
         *         if(Objects.isNull(a)) return b;
         *
         *         TrainJourney t = a;
         *         while(Objects.nonNull(t.onward)){
         *             t = t.onward;
         *         }
         *         t.onward = b;
         *         return a;
         *     }
         *
         *   문제는 이러면 x-z를 저장할 것이고 x-y는 사라지게된다. 이렇게 자료구조를 바꾸면서 생기는 버그를 어떻게 처리할지 해결해야한다.
         *
         *   함수형에서는 이 같은 부작용을 수반하는 메소드를 제한하는 방식으로 문제를 해결한다. 계산 결과를 표현할 자료구조가 필요하면 기존 자료구조를
         *   갱신하지 않도록 새로운 자료구조를 만들어야 한다. 이는 표준 객체지향 프로그래밍의 관점에서도 좋은 기법이다. 함수형을 따르지 않는 프로그램의
         *   문제 중 하나는 부작용을 포함하는 코드와 이에 따른 주석을 프로그래머가 과도하게 남용할 수 있다는 것이다. 이같은 유지보수하는데 어려움을 가중시킨다.
         *
         *
         *              static TrainJourney append(TrainJourney a, TrainJourney b){
         *                  return Objects.isNull(a) ? b : new TrainJourney(a.price, append(a.onward, b));
         *              }
         *
         *    이 코드는 기존의 자료구조를 변경하지 않는다. 하지만 TrainJourney 전체를 새로 만들지도 않는다. 주의할 점은 사용자 역시 append의
         *    결과를 갱신하지 말아야한다는 것이다. 만약 결과를 갱신하면 시퀀스 b로 전달된 기차 정보도 바뀐다.
         *
         *
         *
         *                          > 19.2.2 트리를 사용한 다른 예제
         *    이번에는 다른 자료구조를 살펴보자. HashMap과 같은 인터페이스를 구현할 떄는 이진 탐색트리가 사용된다. Tree는 문자열 Key와 int Value를
         *    포함한다.
         *
         *    .... 생략
         *
         *
         *
         */
    }
}
