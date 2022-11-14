package Chapter_15_CompetableFuture와_리액티브_프로그래밍_컨셉의_기초.test.exam_15_2_3;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorServiceExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n\n---------A----------");
        work1();
        Thread.sleep(10_000);
        work2();

        System.out.println("\n\n---------B----------");

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        work1();
        scheduledExecutorService.schedule(
                ScheduledExecutorServiceExample::work2, 10, TimeUnit.SECONDS
        );
        scheduledExecutorService.shutdown();
    }

    public static void work1() {
        System.out.println("Hello from work1!");
    }

    public static void work2() {
        System.out.println("Hello from work2!");
    }
}
