package Chapter_17_리액티브_프로그래밍.test_17_2_2;

import java.util.Random;

public class TempInfo {
    public static final  Random random = new Random();

    private final String town;
    private final int temp;

    public TempInfo(String town, int temp) {
        this.town = town;
        this.temp = temp;
    }

    public static TempInfo fetch(String town){//팩토리 메소드로 TempInfo 인스턴스를 만든다.
        if( random.nextInt(10) == 0 ) throw new RuntimeException("Error!"); //10분의 1확률로 가져오기 실패
        return new TempInfo(town, random.nextInt(100)); // 0~99사이 온도 반환
    }

    @Override
    public String toString() {
        return  town + " : " + temp;
    }

    public String getTown() {
        return town;
    }

    public int getTemp() {
        return temp;
    }
}
