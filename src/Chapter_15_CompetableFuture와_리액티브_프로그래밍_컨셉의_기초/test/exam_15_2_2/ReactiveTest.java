package Chapter_15_CompetableFuture와_리액티브_프로그래밍_컨셉의_기초.test.exam_15_2_2;

import java.util.function.IntConsumer;

public class ReactiveTest {
    static void f(int x, IntConsumer dealWithResult) {
        dealWithResult.accept(x);
    }

    public static void main(String[] args) {
        int x = 1337;
        Result result = new Result();
        f(x, (int y) -> {
            result.left = y;
            System.out.println((result.left + result.right));
        });
        f(x, (int z) -> {
            result.right = z;
            System.out.println((result.left + result.right));
        });
    }
}

class Result {
    public Integer left = 0;
    public Integer right = 0;


}
