package Charter_08_컬렉션_API_개선;

public class Section1 {
    /**
     *          > 8.1 컬렉션 팩토리
     * 자바 9에서는 작은 컬렉션 객체를 쉽게 만들 수 있는 방법을 제공한다. 예를 들면 Arrays.asList( element... )와 같은 경우이다.
     * 고정 크기의 리스트를 만들었으므로 요소를 갱신할 수는 있지만 새 요소를 추가하거나 요소를 삭제할 수는 없다. 만약 요소를 추가/삭제하려고 하면
     * Unsupported OperationException이 발생한다.
     *
     *      > UnsupportedOperationException 예외 발생
     * 내부적으로 고정된 크기의 변환할 수 있는 배열로 구현되었기 때문에 발생하는 에러이다. set같은 경우는 이러한 메소드가 없으므로 Set으로 만들고자
     * 한다면 new HashSet<>(Arrays.asList( ... ))와 같이 만들 수 있다. 또는 스트림 API를 사용할 수도 있다. Stream.of(...).collect(Collectors.toSet());
     * 하지만 두 방법 모두 우아한 방법이 아니다. 내부적으로 불필요한 객체 할당을 필요로한다. 그리고 결과는 변환할 수 있는 집합이된다.
     *
     *          > 8.1.1 리스트(List) 팩토리
     * List.of 팩토리 메소드를 사용하면 간단하게 리스트를 만들 수 있다. 그러나 역시 추가하려고 하면 java.lang.UnsupportedOperationException이
     * 발생한다. 사실 변경할 수 없는 리스트가 만들어지기 때문이다. set() 메소드로 아이템을 마꾸려고 해도 비슷한 예외가 발생한다. 따라서 set 메소드로도
     * 리스트를 바꿀 수 없다. 이러한 불변성은 상황에 따라 장점이 될 수 있다. 그러나 유의해야 할 점은 요소 자체가 변하는 것을 막을 방법이 없다는 것이다.
     * 리스트를 바꿔야하는 상황이라면 직접 리스트를 만들면 된다.
     *
     *          > 8.1.2 집합(Set) 팩토리
     *  List.of와 비슷한 방법으로 불변 집합을 만들 수 있다. Set.of( ... )이다. 중복된 요소를 제공해 집합을 만들려고 하면 중복된 요소가 있다는 설명과
     *  함께 IllegalArgumentException이 발생한다. 집합은 오직 고유의 요소만 포함할 수 있다는 원칙을 상기시킨다.
     *
     *
     *          > 8.1.3 맵 팩토리
     *  맵을 만드는 것은 리스트나 집합을 만드는 것에 비해 조금 복잡한데 맵을 만들려면 키와 값이 있어야 하기 때문이다. 자바 9에서는 두 가지 방법으로 바꿀 수
     *  없는 맵을 초기화할 수 있다. Map.of 팩토리 메서드에 키와 값을 번갈아 제공하는 방법으로 맵을 만들 수 있다. Map.of("key","value",...) 메소드를
     *  사용하는 것은 열 개 이하의 키와 값 쌍을 가진 작은 맵을 만들 때 유용하다.
     *
     *  그 이상의 맵에서는 Map.Entry<K,V> 객체를 인수로 받으며 가변 인수로 구현된 Map.ofEntries 팩토리 메소드를 사용하는 것이 좋다.
     *  Map.ofEntries ( entry("K",v), entry("K",V)) Map.entry는 Map.Entry 객체를 만드는 새로운 팩토리 메소드이다.
     *
     *
     *          > 8.2 리스트와 집합 처리
     *
     *  자바 8에서는 List, Set 인터페이스에 다음과 같은 메소드가 추가됐다.
     *  removeIf: Predicate를 만족하는 요소를 제거한다.
     *  replaceAll: 리스트에서 사용할 수 있는 기능으로 UnaryOperator 함수를 이용해 요소를 바꾼다.
     *  sort: List 인터페이스에서 제공하는 기능으로 리스트를 정렬한다.
     *
     *  이들 메소드는 호출한 컬렉션 자체를 바꾼다. 새로운 결과를 만드는 스트림 동작과 달리 이들 메소드는 기존 컬렉션을 바꾼다. 왜 이런 메소드가 추가됐을까?
     *  컬렉션을 바꾸는 동작은 에러를 유발하며 복잡하다. 자바8에서는 removeIf, replaceAll을 추가하면서 이러한 문제를 해결하고자 했다.
     *
     *
     *          > 8.2.1 removeIf 메소드
     *   다음은 숫자로 시작되는 참조 코드를 가진 트랜잭션을 삭제하는 코드이다.
     *
     *      for (Transaction transaction : transactions) {
     *          if(Character.isDigit(transaction.getReferenceCode().charAt(0)){
     *              transactions.remove(transaction);
     *          }
     *      }
     *
     *    무엇이 문제일까? 코드는 ConcurrentModificationException을 일으킨다. for-Each는 Iterator를 사용하므로
     *    for ( Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
     *        Transaction transaction = iterator.next();
     *        if(Character.isDigit(transaction.getReferenceCode().charAt(0)))) {
     *            transactions.remove(transaction); //-> 반복하면서 별도의 두 객체를 통해 컬렉션을 바꿈
     *        }
     *    }
     *    즉,
     *
     *      1. Iterator 객체, next().hasNext()를 이용해 소스를 질의한다.
     *      2. Collection 객체 자체, remove()를 호출해서 요소를 삭제한다.
     *
     *    결과적으로 반복자의 상태는 컬렉션 상태와 서로 동기화되지 않는다. Iterator 객체를 명시적으로 사용하고 그 객체의 remove() 메소드를 사용해서
     *    이 문제를 해결할 수 있다.
     *
     *    for ( Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
     *          Transaction transaction = iterator.next();
     *          if(Character.isDigit(transaction.getReferenceCode().charAt(0)))) {
     *              iterator.remove();
     *          }
     *     }
     */
}
