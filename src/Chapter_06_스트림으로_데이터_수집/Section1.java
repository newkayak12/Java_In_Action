package Chapter_06_스트림으로_데이터_수집;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Section1 {
    /**
     *      6. 스트림으로 데이터 수집
     *  스트림의 연산은 filter, map과 같은 중간 연산과 count, findFirst, forEach, reduce 등의 최종 연산으로 구분할 수 있다. 중간 연산은
     *  한 스트림을 다른 스트림으로 변환하는 연산으로서, 여러 연산을 연결할 수 있다. 중간 연산은 스트림 파이프라인을 구성하며, 스트림의 요소를 소비 하지 않는다.
     *  반면 최종 연산은 스트림의 요소를 소비하여 최종 결과를 도출한다. 이번 챕터에서는 최종 연산자 중 collect, 컬렉터로 할 수 있는 일을 알아볼 것이다. 
     *  
     *  
     *      6-1. 컬렉터란 무엇인가?
     *  Collector 인터페이스 구현은 스트림의 요소를 어떤 식으로 도출할지 지정한다. 예를 들어 toList를 Collector 인터페이스의 구현으로 사용했다. 여기서
     *  groupBy를 이용해서 그루핑을 할 수 있다.
     *  
     *      6-1-1 고급 리듀싱 기능을 수행하는 컬렉터
     *  collect는 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의할 수 있다는 점이 최대 강점이다. 구체적으로 설명해서 스트림에 collect를 호출하면
     *  스트림의 요소에 리듀싱 연산이 수행된다. 
     *  
     *      6-1-2 미리 정의된 컬렉터
     *  미리 정의된 컬렉터, 즉 groupingBy와 같이 Collectors 클래스에서 제공하는 팩토리 기능을 살펴보자. Collector에서 제공하는 메소드의 기능은
     *  크게 세 가지로 구분할 수 있다.
     *  
     *      1. 스트림 요소를 하나의 값으로 리듀스하고 요약
     *      2. 요소 그룹화 : 다수준으로 그룹화, 각가의 결과 서브그룹에 추가로 리듀싱 연산 적용, 다양한 컬렉터를 조합
     *      3. 요소 분할  
     *  
     *  
     *      6-2. 리듀싱과 요약
     *  
     *  일단 Collector 팩토리 클래스로 만든 컬렉터 인스턴스로 어떤 일을 할 수 있는지 살펴보자. 앞서 본 바와 같이 Stream.collect 메소드의 인수(컬렉터)
     *  로 스트림의 항목을 컬렉션으로 재구성 할 수 있다.
     *
     *  첫 번쨰 예제로 counting()이라는 팩토리 메소드가 반환하는 컬렉션을 계산해보자.
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
    long howManyDishes = specialMenu.stream().collect(Collectors.counting());
    /**
     *  Collectors의 couting은 다른 컬렉터와 함께 사용할 때 위력을 발휘한다.
     *
     *      6-2-1. 스트림 값에서 최대값과 최소값 검색
     */

}
