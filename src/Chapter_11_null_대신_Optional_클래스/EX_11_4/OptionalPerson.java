package Chapter_11_null_대신_Optional_클래스.EX_11_4;

import java.util.Optional;

public class OptionalPerson {
    private Optional<OptionalCar> car;

    public Optional<OptionalCar> getCar() {
        return car;
    }
}
