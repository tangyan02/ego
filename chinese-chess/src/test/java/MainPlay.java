import ego.chineseChess.core.ChessGame;
import ego.chineseChess.entity.Move;
import ego.chineseChess.entity.Relation;
import ego.chineseChess.entity.Unit;
import ego.chineseChess.helper.MapDriver;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class MainPlay {

    @Test
    public void play() {
        List<Unit> units = null;
        try {
            units = MapDriver.readUnits("src/test/resources/input.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChessGame chessGame = new ChessGame(units);
        Move move = chessGame.play(Relation.SELF);
        System.out.println(move);
    }

}
