package ego.chineseChess.entity;

public class Move {

    public Unit unit;
    public int x;
    public int y;

    public Move(Unit unit, int x, int y) {
        this.unit = unit;
        this.x = x;
        this.y = y;
    }
}
