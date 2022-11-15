package Chapter_15_CompetableFuture와_리액티브_프로그래밍_컨셉의_기초.test.exam_15_5_1;

public class SimpleCellExample {
    public static void main(String[] args) {
        SimpleCell c3 = new SimpleCell("C3");
        SimpleCell c2 = new SimpleCell("C2");
        SimpleCell c1 = new SimpleCell("C1");

        c1.subscribe(c3);
        c2.subscribe(c3);
        c1.onNext(10);
        c2.onNext(20);

        System.out.println("________________________\n");

        ArithmeticCell a3 = new ArithmeticCell("C3");
        SimpleCell2 a2 = new SimpleCell2("C2");
        SimpleCell2 a1 = new SimpleCell2("C1");
        a1.subscribe(a3::setRight);
        a2.subscribe(a3::setLeft);
        a1.onNext(10);
        a2.onNext(20);
        a1.onNext(15);


    }


}
