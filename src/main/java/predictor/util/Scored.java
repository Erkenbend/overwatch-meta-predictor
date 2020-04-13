package predictor.util;

public class Scored<T> implements Comparable<Scored<T>> {
    private T obj;
    private int score;

    public Scored(T obj, int score) {
        this.obj = obj;
        this.score = score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }

    public T getObj() {
        return this.obj;
    }

    @Override
    public int compareTo(Scored<T> other) {
        return this.score - other.score;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Scored)) {
            return false;
        }

        if (((Scored) other).obj == null) {
            return this.obj == null;
        }

        return (((Scored) other).obj.equals(this.obj) && ((Scored) other).score == this.score);
    }

    @Override
    public String toString() {
        return String.format("<%s, %d>", this.obj.toString(), this.score);
    }
}
