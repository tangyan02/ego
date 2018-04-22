package ego.chineseChess.entity;

public class Move {

    public Unit unit;
    public int x;
    public int y;
    public int value;

    public Move() {
    }

    public Move(Unit unit, int x, int y, int value) {
        this.unit = unit;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public void set(Unit unit, int x, int y, int value) {
        this.unit = unit;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Move{" +
                "unit=" + unit +
                ", x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }
}
