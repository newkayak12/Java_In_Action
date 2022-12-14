package Chapter_12_새로운_날짜와_시간_API;

import java.sql.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.*;
import java.util.Locale;

public class Section1 {
    /**
     *          > 12.1 LocalDate, LocalTime Instant, Duration, Period 클래스
     *          > 12.1.1 LocalDate, LocalTime
     *
     *  LocalDate 인스턴스는 시간을 제외한 날짜를 표현하는 불변 객체이다. 특히 LocalDate 객체는 어떤 시간대 정보도 포함하지 않는다. 정적 팩토리 메소드
     *  of로 LocalDate 인스턴스를 만들 수 있다.
     */
    LocalDate date = LocalDate.of(2022, 11, 7);
    int year = date.getYear();
    Month month = date.getMonth();
    int day = date.getDayOfMonth();
    DayOfWeek dow = date.getDayOfWeek();
    int len = date.lengthOfMonth();
    boolean leap = date.isLeapYear();
    /**
     *
     * 팩토리 메소드 now는 시스템 시계 정보를 이용해서 현재 날짜 정보를 얻는다.  LocalDate today = LocalDate.now();
     * 또한 get 메소드에 TemporalField를 전달해서 정보를 얻는 방법도 있다. TemporalField는 시간 관련 객체에서 어떤 필드의 값에 접근할지
     * 정의하는 인터페이스이다. Enumeration ChronoField는 TemporalField 인터페이스를 정의한다.
     */
    int yearChrono = date.get(ChronoField.YEAR);
    int monthChrono = date.get(ChronoField.MONTH_OF_YEAR);
    int dayChrono = date.get(ChronoField.DAY_OF_MONTH);
    /**
     *              > 12.1.2 날짜와 시간 조합
     *  LocalDateTime은 LocalDate와 LocalTime을 쌍으로 갖는 복합 클래스이다. 즉, LocalDateTime은 날짜와 시간을 모두 표현할 수 있다.
     */
    LocalDateTime dt1 = LocalDateTime.of(2017, Month.APRIL, 21, 13, 45, 20);
    LocalDate date1 = dt1.toLocalDate();
    LocalTime time1 = dt1.toLocalTime();
    /**
     *              > 12.1.3 Instant 클래스: 기계의 날짜와 시간
     *   기계는 이와 주, 날짜, 시, 분으로 시간을 표현하기 어렵다. java.time.Instant 클래스에는 이와 같은 연속적인, 기계적인 관점에서 시간을 표현한다.
     *   즉, Instant 클래스는 Unix Epoch 시간을 기준으로 특정 시점까지 시간을 초로 표현한다.
     *
     *   팩토리 메소드 ofEpochSecond에 초를 넘겨서 Instant 클래스 인스턴스를 만들 수 있다. Instant 클래스는 나노초의 정밀도를 제공한다. 또한
     *   오버로드된 ofEpochSecond 메소드 버전에서는 두 번쨰 인수를 이용해서 나노초 단위로 시간을 보정할 수 있다. 두 번째 인수에서는 0 ~ 999,999,999
     *   사이의 값을 지정할 수 있다.
     */
    Instant instant1 = Instant.ofEpochSecond(3);
    Instant instant2 = Instant.ofEpochSecond(3, 0);
    Instant instant3 = Instant.ofEpochSecond(2, 1_000_000_000);
    Instant instant4 = Instant.ofEpochSecond(4, -1_000_000_00);
    /**
     *   Instant도 사람이 읽을 수 있는 시간을 표시하는 now 메소드를 제공한다. 하지만 Instant가 어디까지나 기계 전용 유틸리티라는 점을 기억하자.
     *   즉, Instant는 초, 나노초 정보를 포함한다. 따라서 Instant를 사람이 이해하기에는 난해하다.
     */
    int instantDay = Instant.now().get(ChronoField.DAY_OF_MONTH);
    /**
     *
     *              > 12.1.4 Duration과 Period 정의
     *   지금까지의 모든 클래스는 Temporal 인터페이스를 구현하는데, Temporal 인터페이스는 특정 시간을 모델링하는 객체의 값을 어떻게 읽고 조작할지 정
     *   의한다. 지금까지 다양한 Temporal 인스턴스를 만드는 방법을 살펴봤다. 이번에는 두 시간 객체 사이의 지속시간 duration을 만들어보자. Duration
     *   클래스의 정적 팩토리 메소드 between으로 두 시간 객체 사이의 지속 시간을 만들 수 있다.
     */
    Duration d1 = Duration.between(LocalTime.now(), LocalTime.now().plusHours(10));
    Duration d2 = Duration.between(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
    Duration d3 = Duration.between(Instant.now(), Instant.now().plusSeconds(900_000_000));
    /**
     *   Duration의 between에는 LocalDate를 전달할 수 없다. 년, 월, 일로 시간을 표현할 때는 Period를 사용한다.
     */
    Period tenDays = Period.between(LocalDate.now(), LocalDate.now().plusDays(10));
    /**
     *   Duration, Period는 자신의 인스턴스를 만들 수 있도록 다양한 팩토리 메소드를 제공한다.
     */
    Duration threeMinutes = Duration.ofMinutes(3);
    Duration threeMinutes2 = Duration.of(3, ChronoUnit.MINUTES);

    Period tenDays1 = Period.ofDays(10);
    Period threeWeeks = Period.ofWeeks(3);
    Period twoYearsSixMonthOneDay = Period.of(2, 6, 1);
    /**
     *              > 12.2 날짜 조정, 파싱, 포매팅
     *   withAttribute 메소드로 LocalDate를 바꾼 버전을 간단하게 만들 수 있다. 모든 메소드는 기존 객체를 바꾸지 않는다.
     */
    LocalDate dat1 = LocalDate.of(2022, 11, 7);
    LocalDate dat2 = dat1.withYear(1);
    LocalDate dat3 = dat1.withDayOfMonth(25);
    LocalDate dat4 = dat3.with(ChronoField.MONTH_OF_YEAR, 2);

    /**
     *      마지막 with는 get과 쌍을 이룬다. 이 두 메소드는 날짜, 시간 모든 클래스가 구현하는 Temporal에 정의되어 있다. 어떤 Temporal 객체가 지정된
     *      필드를 지원하지 않으면 UnsupportedTemporalTypeException이 발생한다. 예를 들어 Instant에 ChronoField.MONTH_OF_YEAR을
     *      사용하거나 LocalDate에 ChronoField.NANO_OF_SECOND를 사용하면 예외가 발생한다.
     *
     *
     *              > 12.2.1 TemporalAdjusters 사용하기
     *
     *      조금 더 복잡한 날짜 조정은 오버로드된 버전의 with 메소드에 조금 더 다양한 동작을 할 수 있도록하는 기능을 제공하는 TemporalAdjuster를
     *      전달하는 방법으로 해결할 수 있다.
     */
    LocalDate da1 = LocalDate.of(2014, 3, 18);
    LocalDate da2 = da1.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)); // 2014-03-23
    LocalDate da3 = da2.with(TemporalAdjusters.lastDayOfMonth()); //2014-03-31
    /**
     *  dayOfWeekInMonth : 서수 요일에 해당하는 날짜를 반환하는 TemporalAdjuster를 반환
     *  firstDayOfMonth : 현재 달의 첫 번쨰 날짜를 반환하는 TemporalAdjuster를 반환
     *  firstDayOfNextMonth: 다음 달의 첫 번째 날짜를 반환하는 TemporalAdjuster를 반환
     *  firstDayOfNextYear : 내년의 첫 번쨰 날짜를 반환하는 TemporalAdjuster를 반환
     *  firstDayOfYear : 올해의 첫 번째 날짜를 반환하는 TemporalAdjuster를 반환
     *  firstInMonth : 현재 달의 첫 번쨰 요일에 해당하는 날짜를 반환하는 TemporalAdjuster를 반환
     *
     *  lastDayOfMonth : 현재 달의 마지막 날짜를 반환하는TemporalAdjuster를 반환
     *  lastDayOfNextMonth: 다음 달의 마지막 날짜를 반환하는 TemporalAdjuster를 반환
     *  firstDayOfNextYear : 내년의 마지막 날짜를 반환하는 TemporalAdjuster를 반환
     *  lastDayOfYear : 올해의 마지막 날짜를 반환하는 TemporalAdjuster를 반환
     *  lastInMonth : 현재 달의 마지막 요일에 해당하는 날짜를 반환하는 TemporalAdjuster를 반환
     *
     *  next/ previous : 현재 날짜 이후로 지정한 요일이 처음/이전으로 나타내는 날짜를 반환하는
     *
     *
     *
     *
     *      이 외에도 필요한 기능이 없다면 TemporalAdjuster를 이용하면 복잡한 날짜 조정 기능을 직관적으로 해결할 수 있다. 그뿐만 아니라 필요한 기능이
     *      없다면 비교적 손쉽게 구현할 수 있다.
     *
     *      @functionalInterface
     *      public interface TemporalAdjuster {
     *          Temporal adjustInto(Temporal temporal)
     *      }
     *
     *      TemporalAdjustor 인터페이스 구현을 Temporal을 다른 Temporal로 변환할지 정의하는 식이다.
     *
     */
    class NextWorkingDay implements TemporalAdjuster {

        @Override
        public Temporal adjustInto(Temporal temporal) {
            DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int dayToAdd = 1;
            if( dow == DayOfWeek.FRIDAY ) dayToAdd = 3;
            else if( dow == DayOfWeek.SATURDAY ) dayToAdd = 2;
            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        }
    }
    /**
     *
     *                  > 12.2.2 날짜와 시간 객체 출력 파싱
     *     날짜와 시간 관련 작업에서 포매팅, 파싱은 서로 떼려야 뗄 수 없다. 심지어 포매팅과 파싱 전용 패키지인 java.time.format이 추가될 정도이다.
     *     이 패키지에서 가장 중요한 클래스는 DateTimeFormatter이다. 정적 팩토리 메소드와 상수를 이용해서 손쉽게 포매터를 만들 수 있다.
     *     DateTimeFormatter 클래스는 BASIC_ISO_DATE와 ISO_LOCAL_DATE 등의 상수를 미리 정의 하고 있다. DateTimeFormatter를 이용해서
     *     날짜나 시간을 특정 형식의 문자열로 만들 수도 있다.
     */
    LocalDate date11 = LocalDate.of(2014, 3, 18);
    String s1 = date11.format(DateTimeFormatter.BASIC_ISO_DATE);
    String s2 = date11.format(DateTimeFormatter.ISO_LOCAL_DATE);
    /**
     *      반대로 날짜나 시간을 표현하는 문자열을 파싱해서 날짜 객체를 다시 만들 수 있다. 날짜와 시간 API에서 특정 시점이나 간격을 표현하는 모든 클래스의
     *      팩토리 메소드 parse를 이용해서 문자열을 날짜 객체로 만들 수 있다.
     */
    LocalDate date12 = LocalDate.parse("20140318", DateTimeFormatter.BASIC_ISO_DATE);
    LocalDate date13 = LocalDate.parse("2014-03-18", DateTimeFormatter.ISO_LOCAL_DATE);
    /**
     *      기존의 java.util.DateFormat 클래스와 달리 모든 DateTimeFormatter는 쓰레드에서 안전하게 사용할 수 있는 클래스다. 또한 다음 예제처럼
     *      특정 패턴으로 포매터를 만들 수 있는 정적 팩토리 메소드도 제공한다.
     */
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate date14 = LocalDate.of(2014, 3, 18);
    String formattedDate = date14.format(formatter);
    LocalDate date15 = LocalDate.parse(formattedDate, formatter);
    /**
     *      LocalDate의 format 메소드는 요청 패턴에 해당하는 문자열을 생성한다. 그리고 정적 메소드 parse는 같은 포매터를 적용해서 생성된 문자열을
     *      파싱함으로써 다시 날짜를 생성한다.
     */
    DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
    LocalDate date16 = LocalDate.of(2014,11,18);
    String formattedDate2 = date16.format(italianFormatter);
    LocalDate date17 = LocalDate.parse(formattedDate2, italianFormatter);
    /**
     *      DateTimeFormatterBuilder로 복잡한 포매터를 정의해서 조금 더 세부적으로 포매터를 제어할 수 있다. 즉, DateTimeFormatterBuilder
     *      클래스로 대소문자를 구분하는 파싱, 관대한 규칙을 적용하는 파싱, 패딩, 포매터 등 선택사항을 활용할 수 있다.
     */
    DateTimeFormatter italianFormat = new DateTimeFormatterBuilder()
            .appendText(ChronoField.DAY_OF_MONTH)
            .appendLiteral(". ")
            .appendText(ChronoField.MONTH_OF_YEAR)
            .appendLiteral(" ")
            .appendText(ChronoField.YEAR)
            .parseCaseInsensitive()
            .toFormatter(Locale.ITALIAN);
    /**
     *
     *              > 12.3다양한 시간대와 캘린더 활용 방법
     *   지금까지 살펴본 모든 클래스는 시간대와 관련된 정보가 없었다. 새로운 날짜와 시간 API의 편리함 중 하나는 시간대를 간단하게 처리할 수 있다는 점이다.
     *   기존의 java.util.ZoneID 클래스가 새롭게 등장했다. 새로운 클래스를 이용하면 서머타임과 같은 복잡한 사항이 자동으로 처리된다. 날짜와 시간 API
     *   에서 제공하는 다른 클래스와 마찬가지로 ZoneId는 불변 클래스이다.
     *
     *
     *              > 12.3.1 시간대 사용하기
     *   표준 시간이 같은 지역을 묶어서 시간대 규칙 집합을 정의한다. ZoneRules 클래스에는 40개 정도의 시간대가 있다. ZoneId의 getRules를 이용해서
     *   해당 시간대의 규정을 획득할 수 있다.
     *
     *      ZoneId romeZone = ZoneId.of("Europe/Rome");
     */
    LocalDate ldate = LocalDate.of(2014, Month.APRIL, 18);
    ZoneId romeZone = ZoneId.of("Europe/Rome");
    ZonedDateTime zdt1 = date.atStartOfDay(romeZone);
    LocalDateTime ldt = LocalDateTime.of(2014, Month.APRIL, 18, 13, 49);
    ZonedDateTime zdt2 = ldt.atZone(romeZone);
    Instant instant = Instant.now();
    ZonedDateTime zdt3 = instant.atZone(romeZone);
    /**
     *      2014 - 05 - 14 T 15 : 33 : 04.941 +01:00[Europe/London]
     *      |--LocalDate --|--LocalTime--|
     *      |------ LocalDateTime ------|---- ZoneId -------------|
     *      |-----------------ZonedDateTime-----------------------|
     *
     *
     *
     */
}
