package Chapter_02_동작_파라미터화_코드_전달;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Section1 {
    /**
     *      > 변화하는 요구 사항에 대응하기
     *  동작 파라미터화(behavior parameterization)를 사용하면 자주 바뀌는 요구 사항에 효과적으로 대응할 수 있다. 동작 파라미터화란 아직 어떻게 실행할지
     *  결정하지 않은 코드 블록을 의미한다. 이 코드 블록은 나중에 프로그램에서 호출한다. 즉, 코드 블록의 실행은 나중으로 미뤄진다. 예를 들어 나중에 실행될
     *  메소드의 인수로 코드 블록을 전달할 수 있다. 결과적으로 코드 블록에따라 메소드의 동작 파라미터화 된다. 예를 들어 컬렉션을 처리할 때 아래와 같은 메소드를
     *  구현한다고 가정해보자.
     *      1. 리스트의 모든 요소에 대해서 '어떤 동작'을 수행할 수 있음
     *      2. 리스트 관련 작업을 끝낸 다음에 '어떤 동작'을 수행할 수 있음
     *      3. 에러가 발생하면 '정해진 어떤 다른 동작'을 수행할 수 있음
     *
     *        > 동작 파라미터화
     *  파라미터화로 이처럼 다양한 기능을 수행할 수 있다. 만약 요구에 대응하기 위해서 매번 메소드를 만들고 또 새로운 요청이 생기면 파라미터를 만들고 메소드를
     *  수정하고 하는 작업은 굉장히 고된 일이다. 이를 더 현명하게 대처하는 방법은 참/거짓을 판단하는 함수 (Predicate)를 놓고 조건에 따라 다르게 동작하도록
     *  하면 더욱 간편해질 것이다. 이를 Strategy 패턴이라고 한다. Strategy 패턴은 알고리즘을 캡슐화하는 알고리즘 패밀리를 정의해둔 다음 런타임에
     *  알고리즘을 선택하는 기법이다. 이러한 전략으로 메소드가 다양한 동작을 받아서 내부적으로 다양한 동작을 수행할 수 있다. 이렇게 동작 파라미터화, 즉
     *  메소드가 다양한 동작을 받아서 내부적으로 다양한 동작을 수행할 수 있다. 더 나아가 동작을 익명 클래스로 구현하면 따로 클래스를 만들고 메소드를 구현하는
     *  것보다 간편해진다.
     */
        interface ApplePredicate extends Predicate {}
        public List<Integer> filterApple(List<Integer> inventory, ApplePredicate test){
            return null;
        }
        List<Integer> target = new ArrayList<>();
        List<Integer> redApples = filterApple(target, new ApplePredicate(){
            @Override
            public boolean test(Object o) {
                return false;
            }
//            구현
        });
    /**
     *      > 익명 클래스
     *   위와 같은 익명 클래스(구현 객체)는 자바의 지역 클래스(블록 내부에 선언된 클래스)와 비슷한 개념이다. 익명 클래스는 말 그대로 이름이 없는 클래스이다. 익명 클래
     *   스를 이용하면 클래스 선언과 인스턴스화를 동시에 할 수 있다. 즉, 즉석에서 필요한 구현을 만들어서 사용할 수 있다. 예시는 위와 같다.
     *
     *      > 람다 표현식
     *   위 코드를 더 간단하게 람다로 표현할 수도 있다.
     */
        Integer equalsTarget = 1;
        List<Integer> redLambda = filterApple(target, (Object apple)-> apple.equals(equalsTarget));

}

