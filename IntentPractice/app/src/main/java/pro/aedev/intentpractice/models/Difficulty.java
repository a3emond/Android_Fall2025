package pro.aedev.intentpractice.models;

public enum Difficulty {
    EASY(3, 5),
    MEDIUM(6, 8),
    HARD(9, 12);

    private final int min;
    private final int max;

    Difficulty(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() { return min; }
    public int getMax() { return max; }
}
