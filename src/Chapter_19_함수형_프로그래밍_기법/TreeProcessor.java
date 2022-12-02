package Chapter_19_함수형_프로그래밍_기법;

import java.util.Objects;

public class TreeProcessor {
    public static int lookup(String k, int defaultVal, Tree t){
        if (Objects.isNull(t)) return defaultVal;
        if(k.equals(t.getKey())) return t.getVal();
        return lookup(k, defaultVal, k.compareTo(t.getKey()) < 0 ? t.getLeft() : t.getRight());
    }
}
