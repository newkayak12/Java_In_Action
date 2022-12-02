package Chapter_19_함수형_프로그래밍_기법;

import java.util.Objects;

public class TrainJourney {
    public int price;
    public TrainJourney onward;

    public TrainJourney(int price, TrainJourney onward) {
        this.price = price;
        this.onward = onward;
    }

    static TrainJourney link (TrainJourney a, TrainJourney b){
        if(Objects.isNull(a)) return b;

        TrainJourney t = a;
        while(Objects.nonNull(t.onward)){
            t = t.onward;
        }
        t.onward = b;
        return a;
    }

    static TrainJourney append(TrainJourney a, TrainJourney b){
        return Objects.isNull(a) ? b : new TrainJourney(a.price, append(a.onward, b));
    }
}
