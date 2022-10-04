package Chpater_04_스트림_소개;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Section1 {
    /**
     *          > 스트림이란
     *  스트림을 이용하면 선언형(즉, 데이터를 처리하는 임시 구현 코드)으로 컬렉션이 데이터를 처리할 수 있다.(질의로 표현) 또한, 스트림을 이용하면 멀티
     *  쓰레드를 구현하지 않아도 데이터를 투명하게 병렬로 처리할 수 있다. 또한 filter, sort, map, collect와 같은 고수준 빌딩 블록(hight-level
     *  building block)으로 이루어져 있으므로 특정 쓰레딩 모델에 제한되지 않고 자유롭게 어떤 상황이든 사용할 수 있다. 또한 추가적으로 데이터 처리 과정을
     *  병렬화하면서 쓰레드와 락을 걱정할 필요가 없어진다. (.parallelStream())
     *
     *  자바의 스트림 API 특징을 정리하면
     *  1. 선언형: 더 간결하고 가독성이 좋아진다.
     *  2. 조립할 수 있음 : 유연성이 좋아진다.
     *  3. 병렬화 : 성능이 좋아진다.
     *
     *          > 스트림 시작하기
     *  스트림이란 정확히 뭘까? '스트림이란 데이터 처리 연산을 지원하도록 소스에서 추출된 연속적인 요소(Sequence of Elements)'로 정의할 수 있다.
     *  1. 연속된 요소 : 컬렉션과 마찬가지로 스트림은 특정 요소 형식으로 이뤄진 연속된 값 집합의 인터페이스를 제공한다. 컬렉션은 자료구조이므로 컬렉션에서는
     *  시간, 공간 복잡성과 관련된 요소 저장 및 접근 연산이 주를 이룬다. 반면 스트림은 filter, sorted, map과 같이 표현 계산식이 주를 이룬다.
     *
     *  2. 소스 : 스트림은 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비한다. 정렬되니 컬렉션으로 스트림을 생성하면 정렬은 유지된다.
     *
     *  3. 데이터 처리 연산 : 스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다. 예를 들어 filter, map,
     *  reduce, find, match, sort 등으로 데이터를 조작할 수 있다. 스트림 연산은 순차/ 병렬로 실행할 수 있다.
     *
     *  4. 파이프라이닝(PipeLining) : 대부분의 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프 라인을 구성할 수 있도록 스트림을 반환한다. 또한
     *  laziness, short-circuit과 같은 최적화도 얻을 수 있다.
     *
     *  5. 내부 반복 : 반복자를 통해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원한다.
     *
     *  이러한 특징들을 예제로 확인하면
     */
        {
            class Menu{
                private Integer calories;
                private String name;

                public Integer getCalories() {
                    return calories;
                }

                public String getName() {
                    return name;
                }

                public Menu(Integer calories, String name) {
                    this.calories = calories;
                    this.name = name;
                }
            }
            List<Menu> menus = Arrays.asList(new Menu(360, "Pizza"), new Menu(500, "Chicken"),
                    new Menu(1, "Gum"), new Menu(20, "Rice"), new Menu(60, "Water"));

            List<String> threeHighCaloricDishName =  menus.stream()
                    .filter(dish -> dish.getCalories() > 300)
                    .map(Menu::getName)
                    .limit(3)
                    .collect(Collectors.toList());
            System.out.println(threeHighCaloricDishName);
        }
}
