import ego.chineseChess.core.ChessGame;
import ego.chineseChess.entity.Unit;
import ego.chineseChess.helper.MapReader;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class MainPlay {

    @Test
    public void play() {
        List<Unit> units = null;
        try {
            units = MapReader.readUnits("src/test/resources/input.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChessGame chessGame = new ChessGame(units);
        System.out.println("OK");
    }

}
