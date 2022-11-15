package Chapter_15_CompetableFuture와_리액티브_프로그래밍_컨셉의_기초.test.exam_15_5_1;

public class ArithmeticCell extends SimpleCell2 {
    private int left;
    private int right;

    public ArithmeticCell(String name) {
        super(name);
    }

    public void setLeft(int left) {
        this.left = left;
        onNext(left + right);
    }

    public void setRight(int right) {
        this.right = right;
        onNext(right + left);
    }
}
