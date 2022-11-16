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

        ArithmeticCell a3 = new ArithmeticCell("A3");
        SimpleCell a2 = new SimpleCell("A2");
        SimpleCell a1 = new SimpleCell("A1");
        a1.subscribe(a3::setRight);
        a2.subscribe(a3::setLeft);
        a1.onNext(10);
        a2.onNext(20);
        a1.onNext(15);

/*********************************************************************************************************************/

        a1 = new SimpleCell("A1");
        a2 = new SimpleCell("A2");
        a3 = new ArithmeticCell("A3");
        SimpleCell a4 = new SimpleCell("A4");
        ArithmeticCell a5 = new ArithmeticCell("A5");

        a1.subscribe(a3::setRight);
        a2.subscribe(a3::setLeft);
        a3.subscribe(a5::setLeft);
        a4.subscribe(a5::setRight);

        a1.onNext(10);
        a2.onNext(20);
        a1.onNext(15);
        a4.onNext(1);
        a4.onNext(3);

        
    }
}
