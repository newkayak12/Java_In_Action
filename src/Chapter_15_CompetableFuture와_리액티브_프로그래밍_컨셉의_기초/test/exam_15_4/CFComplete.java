package Chapter_15_CompetableFuture와_리액티브_프로그래밍_컨셉의_기초.test.exam_15_4;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CFComplete {
    public static Integer f(int x) {
        return Math.multiplyExact(x, 2000);
    }

    public static Integer g(int x) {
        return Math.incrementExact(200);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        executorService.submit(() -> a.complete(f(x)));
        int b = g(x);
        /**
         * executorService.submit(() -> a.complete(g(x)));
         * int b = f(x);
         * 이렇게도 가능
         */
        System.out.println(a.get() + b);
        executorService.shutdown();

        Long end = System.currentTimeMillis();
        System.out.println("Running time : " + (end - start));
    }
}

