package Chapter_19_함수형_프로그래밍_기법;

import java.util.function.DoubleUnaryOperator;

public class Currying {

    static DoubleUnaryOperator curriedConverter(double f, double b  ) {
        return (double x) -> x * f + b;
    }

}
