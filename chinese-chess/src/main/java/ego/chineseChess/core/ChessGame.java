package ego.chineseChess.core;

import ego.chineseChess.entity.Unit;

import java.util.List;

public class ChessGame {

    private GameMap gameMap;

    public ChessGame(List<Unit> units) {
        gameMap = new GameMap(units);
    }


}
