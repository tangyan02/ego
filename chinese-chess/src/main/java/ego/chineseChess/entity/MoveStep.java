package ego.chineseChess.entity;

/**
 * 某一步骤的决策
 */
public class MoveStep {

    public Unit unit;
    public int x;
    public int y;
    /**
     * 步骤评分
     */
    public int value;

    public MoveStep() {
    }

    public MoveStep(Unit unit, int x, int y, int value) {
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
        return "MoveStep{" +
                "unit=" + unit +
                ", x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }


}
