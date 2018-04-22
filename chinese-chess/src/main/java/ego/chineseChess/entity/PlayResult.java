package ego.chineseChess.entity;

public class PlayResult {

    public Move move;
    public int count;

    public PlayResult(Move move, int count) {
        this.move = move;
        this.count = count;
    }

    @Override
    public String toString() {
        return "PlayResult{" +
                "move=" + move +
                ", count=" + count +
                '}';
    }
}
