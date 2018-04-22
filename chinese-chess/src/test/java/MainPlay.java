import ego.chineseChess.core.ChessGame;
import ego.chineseChess.core.GameMap;
import ego.chineseChess.entity.PlayResult;
import ego.chineseChess.entity.Relation;
import ego.chineseChess.entity.Unit;
import ego.chineseChess.helper.MapDriver;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class MainPlay {

    boolean update = false;


    @Test
    public void play() throws IOException {
        String path = "src/test/resources/input.txt";
        List<Unit> units = null;
        try {
            units = MapDriver.readUnits(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChessGame chessGame = new ChessGame(units);
        PlayResult result = chessGame.play(Relation.SELF);
        if (update) {
            GameMap gameMap = new GameMap(units);
            gameMap.move(result.move.unit, result.move.x, result.move.y);
            MapDriver.writeUnits(path, gameMap);
        }
        System.out.println(result);
    }

}
