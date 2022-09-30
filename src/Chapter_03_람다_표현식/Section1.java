package Chapter_03_람다_표현식;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

public class Section1 {
    /**
     *               >  람다란?
     *  람다 표현식은 메소드로 전달할 수 있는 익명 함수를 단수화한 것이라고 할 수 있다. 람다 표현식은 이름은 없지만, 파라미터 리스터, 바디, 리턴 타입
     *  발생할 수 있는 예외 리스느틑 가질 수 있다.
     *
     *      1. 익명
     *      -> 보통의 메소드와 달리 이름이 없으므로 익명이라고 표현한다. 구현해야할 코드에 대한 고려 사항이 줄어든다.
     *      2. 함수
     *      -> 람다는 메소드처럼 특정 클래스에 종속되지 않으므로 함수라고 부른다. 하지만 메소드처럼 파라미터 리스트, 바디, 반환 형식, 가능한 예외 리스트를
     *      포함한다.
     *      3. 전달
     *      -> 람다 표현식을 메소드로 인수로 전달하거나 변수로 저장할 수 있다.
     *      4. 간결성
     *      -> 익명 클래스처럼 많은 자질구레한 코드를 구현할 필요가 없다.
     *
     *   람다 표현식은 아래와 같다.
     *
     *      (Apple a1, Apple a2)   ->    a1.getWeight().compareTo(a2.getWeight());
     *      ⌞    람다 파라미터    ⌟  화살표   ⌞               람다 바디                 ⌟
     *
     *      1. 파라미터 리스트
     *      -> 람다에서 사용할 파라미터를 담고 있다.
     *      2. 화살표
     *      -> 화살표는 람다의 파라미터 리스트와 바디를 구분한다.
     *      3. 람다 바디
     *      -> 실질적인 동작을 하는 부분이다.
     *
     *   람다 표현법의 예시로는
     *   1. () -> {}        : 파라미터가 없으며 void를 반환하는 람다표현이다. public void run() {}과 같다.
     *   2. () -> "String"  : 파라미터가 없으며, 문자열을 반환한다.
     *   3. () -> {return "IronMan"}    : 파라미터가 없으며 (명시적으로 return을 통해서) 문자열을 반환하는 표현식이다.
     *
     *   람다 예제로는
     *   1. 불리언 표현식     : (List<String> list) -> list.isEmpty()
     *   2. 객체 생성        : () -> new Apple(10)
     *   3. 객체에서 소비     : (Apple a) -> { System.out.println(a.getWeight())};
     *   4. 객체에서 선택/추출 : (String s) -> s.length();
     *   5. 두 값을 조합     : (int a, int b) -> a * b;
     *   6. 두 객체 비교     : (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
     *
     *              > 어디에, 어떻게 람다를 사용할 수 있을까?
     *   람다는 함수형 인터페이스라는 문맥에서 람다 표현식을 사용할 수 있다. 그럼 함수형 인터페이스란 무엇일까? 간단히 함수형 인터페이스는 정확히 하나의
     *   추상 메소드를 지정하는 인터페이스다. 자바 함수형 인터페이스로 Runnable, Comparator 등이 있다.
     */
    public interface Comparator<T> { // java.util.Comparator
        int compare(T o1, T o2);
    }
    public interface  Runnable { // java.lang.Runnable
        void run();
    }
    public interface ActionListener extends EventListener { // java.util.concurrent.Callable
        void actionPerformed(ActionEvent e);
    }
    public interface Callable<V> { // java.util.concurrent.Callable
        V call() throws Exception;
    }
    public interface PrivilegedAction<T> { // java.security.PrivilegedAction
        T run();
    }
    /**
     *   함수형 인터페이스로 뭘 할 수 있을까? 람다 표현식으로 함수형 인터페이스의 추상 메소드 구현을 직접 전달할 수 있으므로 전체 표현식을 함수형 인터페이스의
     *   인스턴스로 취급(기술적으로 따지면 인터페이스를 구현한 클래스의 인스턴스)할 수 있다.
     *
     *
     *              >  함수 디스크립터
     *    함수형 인터페이스의 추상 메소드 시그니처(Signature)는 람다 표현식의 시그니처를 가리킨다. 람다 표현식의 시그니처를 서술하는 메소드를 함수 디스크립터
     *    (function descriptor)라고 부른다. 예를 들어 Runnable 인터페이스의 유일한 추상 메소드 run은 인수와 반환값이 없으므로(void 반환)
     *    Runnable 인터페이스는 인수와 반환값이 없는 시그니처로 생각할 수 있다.
     *
     *    이러한 것이 어렵다면 '람다 표현식은 변수에 할당하거나 함수형 인터페이스를 인수로 받는 메소드로 전달할 수 있으며, 함수형 인터페이스의 추상 메소드와
     *    같은 시그니처를 갖는다는 사실을 기억하는 것'으로 충분하다.
     *
     *    그러면 '왜 함수형 인터페이스를 인수로 받는 메소드에만 람다를 사용할 수 있을까?' 자바 설계자들은 언어가 복잡해지지 않으며, 기존 프로그래머들이
     *    추상 메소드를 갖는 인터페이스에 이미 익숙하다는 점도 고려했다.
     *
     *      {
     *                @FunctionalInterface?
     *          새로운 자바 API를 살펴보면 함수형 인터페이스에 @FunctionalInterface 어노테이션이 있다. 이는 함수형 인터페이스임을 가리키는 어노테이션이다.
     *          @FunctionalInterface로 인터페이스를 선언하고 실제로 함수형이 아니면 컴파일 에러를 발생시킨다.
     *
     *      }
     *
     *                  > 람다 활용 : 실행 어라운드 패턴
     *    람다와 동작 파라미터화로 유연하고 간결한 코드를 구현하는데 도움을 주는 예시를 들면 자원 처리에 사용하는 순환 패턴(recurrent pattern)은 자원
     *    을 열고, 처리한 다음에, 자원을 닫는 순서로 이뤄진다. 설정(setup)과 정리(cleanup) 과정은 대부분 비슷하다. 즉, 실제 자원을 처리하는 코드를
     *    설정과 정리 두 가지 과정이 둘러싸는 형태를 갖는다. 같은 형식의 코드를 실행 어라운드 패턴(execute around pattern)이라고 한다. 더 나아가
     *    실용적으로 많이 쓸만한 것들을 자바에서는 미리 만들어뒀다.
     *
     *
     *                  > Predicate
     *     java.util.function.Predicate<T> 인터페이스는 test라는 추상메소드를 정의하며 test는 제네릭 형식의 T의 객체를 인수로 받아 Boolean을
     *     반환한다.
     */

        @FunctionalInterface
        public interface Predicate<T>{
            boolean test(T t);
        }
        Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
    /**
     *
     *                  > Consumer
     *  java.util.function.Consumer<T> 인터페이스는 제네릭 형식의 T 객체를 받아 void를 반환하는 accept라는 추상 메소드를 정의한다. T 형식의
     *  객체를 인수로 받아서 어떤 동작을 수행하고 싶을 때 Consumer 인터페이스를 사용할 수 있다.
     */

        @FunctionalInterface
        public interface Consumer<T> {
            void accept(T t);
        }
        public <T> void forEach(List<T> list, Consumer<T> c){
            for(T t: list){
                c.accept(t);
            }
        }
        {
            forEach(Arrays.asList(1,2,3,4,5), (Integer i)-> System.out.println(i));
        }
    /**
     *
     *              > Function
     *  java.util.function.Function<T,R> 인터페이스는 제네릭 형식 T를 인수로 받아서 제네릭 R 객체를 반환하는 추상메소드 apply를 정의한다.
     *  입력을 출력으로 매핑하는 람다를 정의할 때 Function 인터페이스를 활용할 수 있다.
     */

        @FunctionalInterface
        public interface Function<T,R>{
            R apply(T t);
        }
        public <T,R> List<R> map(List<T> list, Function<T,R> f){
            List<R> result = new ArrayList<>();
            for(T t: list){
                result.add(f.apply(t));
            }
            return result;
        }
        {
            List<Integer> l = map(Arrays.asList("lambdas", "in", "action"),
                    (String s) -> s.length()
            );
            System.out.println(l);
        }
    /**
     *          > 기타 특화 인터페이스
     *  자바의 모든 형식은 참조형 아니면 기본형이다. 하지만 제네릭 파라미터에는 참조형만 사용할 수 있다. 제네릭의 내부 구현 때문에 어쩔수 없다. 그래서
     *  자바는 기본형 -> 참조형으로 변환하는 박싱, 참조형 -> 기본형으로 변환하는 언박싱, 기본형, 참조형 간의 자동으로 박싱/언박싱을 진행하는 오토 박싱을
     *  지원한다. 하지만 이러한 편리함에는 비용이 수반된다. 기본형을 래핑하는 박싱 값은 힙에 저장되며, 메모리를 더 소비한다. 기본형을 가져올 때는 메모리를
     *  탐색하는 과정을 동반한다.
     *
     *  자바 8부터는 기본형 입출력을 사용하는 상황에서 오토박싱을 피할 수 있도록 원시 타입을 지원하는 함수형 인터페이스를 만들어뒀다.
     *
     */
        public interface IntPredicate{
            boolean test(int i);
        }
        {
            IntPredicate evenNumber = (int i)-> i%2 == 0;
            System.out.println(evenNumber.test(1000));
        }
    /**
     *  이외에도 Double~, Long~ 과 같이 형식명이 붙는 인터페이스 들이 있다. 또한 이러한 함수들을 우리가 직접 만들어서 사용할 수도 있다.
     */

}

