package Chapter_08_컬렉션_API_개선;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.Map.entry;

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
     *    }
     *
     *
     *          > 8.2.2 replaceAll 메소드
     *  List 인터페이스의 replaceAll을 사용해서 리스트의 각 요소를 새로운 요소로 바꿀 수 있다.
     *
     *  referenceCodes.stream().map(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1))
     *  .toCollect(Collectors.toList()).forEach(System.out::println);
     *
     * 위 코드는 새 문자열 컬렉션을 만든다. 그러나 우리가 원하는 것은 기존 컬렉션을 바꾸는 것이다. ListIterator 객체를 이용하면 이 문제를 해결할 수 있다.
     *
     *  for( ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext()) {
     *      String code = iterator.next()
     *      iterator.set(Character.toUpperCase(code.charAt(0) + code.subString(1));
     *  }
     *
     *  그러나 문제가 또 발생한다. 일단 코드가 복잡해지는 것은 둘째치고 컬렉션 객체를 Iterator 객체와 혼용하면 반복, 컬렉션 변경이 동시에 이뤄지면서
     *  문제를 일으킬 수 있다. 자바 8에서 추가된 replaceAll을 사용하면 문제를 최소한으로 줄이고 요소를 바꿀 수 있다.
     *
     *      referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.subString(1));
     *
     *
     *              > 8.3 Map의 다양한 메소드
     *          >8.3.1 forEach
     *  Map에서 K,V를 반복하면서 확인하는 작업은 귀찮은 일 중 하나이다. Map.Entry<K,V>의 반복자를 사용해서 맵의 항목 집합을 반복할 수 있다.
     *
     *      for(Map.Entry<String, Integer> entry: ageOfFriends.entrySet()){
     *          String friend = entry.getKey();
     *          Integer age = entry.getValue();
     *          Sysout.out.println(friend + " is " + age + " years old);
     *      }
     *
     *  자바 8부터는 BiConsumer를 인수로 받는 ForEach 메소드를 지원한다.
     *
     *      ageOfFriend.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
     *
     *          > 8.3.2 정렬 메소드
     *  다음 두 개의 새로운 유틸리티를 이용하면 맵 항목을 키 또는 값을 기준으로 정렬할 수 있다.
     *
     *      1. Entry.comparingByValue
     *      2. Entry.comparingByKey
     */
    Map<String,String> favouriteMovies = Map.ofEntries(entry("A", "Apple"), entry("D", "Design")
            , entry("B", "Black"));
    {
        favouriteMovies.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(System.out::println);
        favouriteMovies.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(System.out::println);
    }
    /**
     *
     *                      {
     *                            > HashMap 성능
     *                         자바 8에서는 HashMap의 내부 구조를 바꿔서 성능을 개선했다. 기존에 맵의 항목은 키로 생성한
     *                         해시 코드로 접근할 수 있는 버켓에 저장했다. 많은 키가 같은 해시코드를 반환하는 상황이 되면
     *                         O(n)의 시간이 LinkedList로 버킷을 반환해야 하므로 성능이 저하된다.
     *
     *                         최근에는 버킷이 너무 커질 경우 이를 O(log(n))의 시간이 소요되는 정렬된 트리를 이용해 동적으로
     *                         치환해 충돌이 일어나는 요소 반환 성능을 개선했다. 하지만 키가 String, Number 클래스 같은
     *                         Comparable의 형태여야만 정렬된 트리가 지원된다.
     *                      }
     *
     *  여기서 요청한 키가 맵에 존재하지 않을 때 이를 어떻게 처리하느냐도 흔히 발생하는 문제이다. getOrDefault 메소드로 이를 쉽게 해결할 수 있다.
     *
     *
     *
     *              > 8.3.3 getOrDefault
     *  기존에는 찾으려는 값이 없으면 Null이 반환됐다. NullPointerException을 방지하려면 따로 Null인지 확인하는 코드는 굉장한 피로감을 줬었다. 그러나
     *  getOrDefault를 사용하면 이 문제를 쉽게 해결할 수 있다. 이 메소드는 첫 번째 인수로 키, 두 번째 인수로 default 값을 받으며, 키가 존재하지
     *  않으면 기본값을 반환한다.
     */
    Map<String,String> map = new HashMap<>();
    {
        map.getOrDefault("HELLO", "HELLO");
    }
    /**
     *
     *              > 8.3.4 계산 패턴
     *  맵에 키가 존재하는지 여부에 따라 어떤 동작을 실행하고 결과를 저장해야하는 상황이 필요할 때가 있다. 예를 들어서 키를 이용해서 값 비싼 동작을 실행한
     *  결과를 캐싱하려한다. 키가 존재하면 결과를 다시 계산할 필요가 없다. 이때 아래의 세 연산이 도움을 준다.
     *
     *      1. computeIfAbsent : 제공된 키에 해당하는 값이 없으면(혹은 Null), 키를 이용해서 새 값을 계산하고 Map에 추가한다.
     *      2. computeIfPresent : 제공된 키가 존재하면 새 값을 계산하고 맵에 추가한다.
     *      3. compute: 제공된 키로 새 값을 계산하고 맵에 저장한다.
     *
     *   정보를 캐싱할 때 computeIfAbsent를 활용할 수 있다. 파일 집합의 각 행을 파싱해서 SHA-256을 계산한다고 가정해보자. 기존에 이미 데이터를
     *   처리했다면 이 값을 다시 계산할 필요가 없다.
     *   맵을 이용해서 캐시를 구현했다고 가정하면 아래와 같이 MessageDigest 인스턴스로 SHA-256 해시를 계산할 수 있다.
     */
    Map<String, byte[]> dataToHash = new HashMap<>();
    MessageDigest messageDigest;

    {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     *    이제 데이터를 반복하면서 결과를 캐싱한다.
     */
    private byte[] calculateDigest(String key){
        return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
    }
    {
        URI uri = URI.create("/Users/sanghyeonkim/Downloads/port/javaInAction/JavaInAction/src/Charter_08_컬렉션_API_개선/test.txt");
        Path paths = Paths.get(uri);
        List<String> lines = null;
        try {
            lines = Files.readAllLines(paths);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lines.stream()
        .forEach(line-> dataToHash.computeIfAbsent(line, this::calculateDigest));
    }
    /**
     *  여러 값을 저장하는 맵을 처리할 때도 이 패턴을 유용하게 활용할 수 있다. Map<K,List<v>>에 요소를 추가하려면 항목이 초기화되어 있는지 확인해야한다.
     */
    Map<String,List<String>> friendsToMovies = Map.of("Raphael", Arrays.asList("Decision to leave"));
    String friend = "Raphael";
    List<String> movies = friendsToMovies.get(friend);
    {
        if(movies == null){//리스트 초기화 확인
            movies = new ArrayList<>();
            friendsToMovies.put(friend, movies);
        }
    }
    /**
     *  computeIfAbsent는 키가 존재하지 않으면 값을 계산해 맵에 추가하고 키가 존재하면 기존 값을 반환한다. 위의 복잡한 코드를 조금 더 간단하게
     *  바꿀 수 있다.
     */
    {
        friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>()).add("StarWars");
        System.out.println(friendsToMovies);
        Function<String,List<String>> a = name -> new ArrayList<>();
    }
    /**
     *   computeIfPresent 메소드는 현재 키와 관련된 값이 맵에 존재하며, Null이 아닐 때만 새 값을 계산한다. 값을 만드는 함수가 NULL을 반환하면
     *   현재 매핑을 맵에서 제거한다. 하지만 매핑을 제거할 때는 remove 메소드를 오버라이드하는 것이 더 적합하다.
     *
     *
     *              >8.3.5 삭제 패턴
     *    제공된 키에 해당하는 맵 항목을 제거하는 remove는 이미 알고 있을 것이다. 자바 8에서는 키가 특정한 값과 연관되었을 때만 항목을 제공하는
     *    오버로드 메소드를 제공한다.
     */
    public boolean removeTest(String key, String value){
        if(favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)){
            favouriteMovies.remove(key);
            return true;
        } else {
            return false;
        }
    }
    {
        String key = "Raphael";
        String value = "APPSTORE";
        removeTest(key, value);
        /**
         * 오버로드 메소드를 사용하면 코드를 간결하게 구현할 수 있다.
         */
        favouriteMovies.replace(key, value);
    }
    /**
     *
     *
     *              >8.3.6 교체 패턴
     *  맵의 항목을 바꾸는데 사용할 수 있는 두 개의 메소드가 맵에 추가됐다.
     *      1. replaceAll: BiFunction을 적용한 결과로 각 항목의 값을 교체한다. 이 메소드는 List의 replaceAll과 비슷한 동적을 한다.
     *      2. replace: 키가 존재하면 맵의 값을 바꾼다. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드된 메소드도 있다.
     */
    {
        Map<String, String> favouriteMovies = new HashMap<>();
        favouriteMovies.put("Raphael", "starWars");
        favouriteMovies.put("Olivia", "jamesBond");
        favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
        System.out.println(favouriteMovies);
    }
    /**
     *  replace는 한 개의 맵에서만 사용할 수 있다. 두 개의 맵에서 값을 합치거나 값을 바꿔야한다면 어떻게 할까?
     *
     *
     *
     *              > 8.3.7 합침
     *   만약 두 그룹의 연락처를 포함하는 두 개의 맵을 합친다고 가정하다. 그럼 아래와 같이 putAll을 사용할 수 있다.
     */
    {
        Map<String, String> family = Map.of("Teo", "StarWars", "Cristina", "JamesBond");
        Map<String, String> friend = Map.of("Raphael", "StarWars", "James", "JamesBond");
        Map<String,String> everyOne = new HashMap<>(family);
        everyOne.putAll(friend);
    }
    /**
     *   중복된 키가 없다면 위 코드는 잘 동작한다. 값을 조금 더 유연하게 합쳐야 한다면 새로운 merge 메소드를 이용할 수 있다. 이 메소드는 중복된 키를 어떻게
     *   합칠지 결정하는 BiFunction을 인수로 받는다. family와 friends 두 맵 모두에 Cristina가 다른 영화 값으로 존재한다고 가정해보자
     */
    {
        Map<String, String> family = Map.of("Teo", "StarWars", "Cristina", "JamesBond");
        Map<String, String> friend = Map.of("Raphael", "StarWars", "Cristina", "JamesBond");
        /**
         *  forEach, merge로 키가 같아서 생기는 충돌을 해결할 수 있다.
         */
        Map<String,String> everyOne = new HashMap<>(family);
        friend.forEach((k,v)->everyOne.merge(k,v, (a,b)->a+"&"+b));
        System.out.println(everyOne);
    }
    /**
     *   "지정한 키와 연관된 값이 없거나 값이 없거나 값이 NULL이면 merge는 키를 NULL이 아닌 값과 연결한다. 아니면 merge는 연결된 값을 주어진
     *   매핑 함수의 [결과] 값으로 대치하거나 결과가 NULL이면 [항목]을 제공한다."
     *
     *  merge를 이용해서 초기화 검사를 구현할 수도 있다.
     */
    {
        Map<String, Long> moviesToCount = new HashMap<>();
        String movieName = "jamesBond";
        Long count = moviesToCount.get(movieName);
        if(count == null ){
            moviesToCount.put(movieName,1L);
        } else {
            moviesToCount.put(movieName,count + 1L);
        }

        /**
         * 위 코드를 아래의 코드로 대체할 수 있다.
         */
        moviesToCount.merge(movieName, 1L, (key, value) -> value + 1L);

        /**
         *           > 8.4 개선된 ConcurrentHashMap
         *  ConcurrentHashMap은 동시성에 친화적인 HashMap이다. ConcurrentHashMap은 내부 자료구조의 특정 부분만 잠궈서 동시성을 첨가했고,
         *  갱신 작업을 허용한다. 따라서 동기화된 Hashtable 버전에 비해서 읽기 쓰기 연산 성능이 월등하다.
         *  (*** 표준 HashMap은 비동기로 동작한다.)
         *
         *     > 8.4.1 리듀스, 검색
         *
         *  ConcurrentHashMap은 스트림에서 선보였던 세 가지 새로운 연산을 지원한다.
         *
         *          1. forEach
         *          2. reduce
         *          3. search
         *
         *   자세하게 들어가면 아래와 같다.
         *
         *          1. 키, 값으로 연산(forEach, reduce, search)
         *          2. 키로 연산(forEachKey, reduceKeys, searchKeys)
         *          3. 값으로 연산(forEachValue, reduceValues, searchValues)
         *          4. Map.Entry 객체로 연산(forEachEntry, reduceEntries, searchEntires)
         *   이들 연산은 ConcurrentHashMap의 상태를 잠그지 않고 연산을 수행한다. 따라서 이들 연산에 제공한 함수는 계산이 진행되는 동안 바뀔 수 있는
         *   객체, 값, 순서 등에 의존하지 않아야 한다.
         *
         *   또한 이들 연산에 병령성 기준값(threshold)을 지정해야 한다. 맵의 크기가 주어진 기준값보다 작으면 순차적으로 연산을 실행한다. 기준값을
         *   1로 지정하면 공통 쓰레드 풀을 이용해서 병렬성을 극대화 한다. Long.MAX_VALUE를 기준값으로 하면 싱글 쓰레드 연산을 실행한다.
         */

        ConcurrentHashMap<String,Long> map = new ConcurrentHashMap<>();
        long parallelismThreshold = 1;
        Optional<Long> maxValue = Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
        /**
         *  int, long, doulbe 등의 기본값에는 전용 each reduce 연산이 제공되므로 reduceValuesToInt, reduceKeysToLong을 이용하면
         *  박싱 없이 효율적으로 작업할 수 있다.
         *
         *
         *
         *       > 8.4.2 계수
         *   ConcurrentHashMap 클래스는 맵의 매핑 개수를 반환하는 mappingCount 메소드를 제공한다. 기존의 size 메소드 대신 새 코드에서는 int를
         *   반환하는 mappingCount 메소드를 사용하는 것이 좋다. 그래야 매핑 개수가 int 범위를 넘어서는 이후 상황을 대처할 수 있기 떄문이다.
         *
         *      > 8.4.3 집합뷰
         *   ConcurrentHashMap 클래스는 ConcurrentHashMap을 집합 뷰로 변환하는 KeySet이라는 새 메소드를 제공한다. 맵을 바꾸면 집합도 바뀌고
         *   집합을 바꾸면 맵도 영향을 받는다. newKeySet이라는 새 메소드로 ConcurrentHashMap으로 유지되는 집합을 만들 수도 있다.
         */
    }
}
