package ego.chineseChess.core;

import ego.chineseChess.entity.Move;
import ego.chineseChess.entity.Point;
import ego.chineseChess.entity.Relation;
import ego.chineseChess.entity.Unit;
import ego.chineseChess.helper.MapDriver;

import java.util.List;

public class ChessGame {

    private GameMap gameMap;

    public ChessGame(List<Unit> units) {
        gameMap = new GameMap(units);
    }

    public Move play(Relation relation) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        return dfs(Config.level, relation, alpha, beta, null, null, null);
    }

    private Move dfs(int level, Relation relation, int alpha, int beta, Unit currentUnit, Integer currentX, Integer currentY) {
        if (level == 0) {
            int value = ScoreCaculator.getScore(gameMap);
            MapDriver.printToConsole(gameMap);
            return new Move(currentUnit, currentX, currentY, value);
        }
        Move move = null;
        for (Unit unit : gameMap.getUnits()) {
            if (unit.relation != relation)
                continue;
            List<Point> points = MoveRuler.getMovePoint(gameMap, unit);
            for (Point point : points) {
                int fromX = unit.x;
                int fromY = unit.y;
                int toX = point.x;
                int toY = point.y;
                Unit targetUnit = gameMap.getUnit(toX, toY);
                gameMap.move(unit, toX, toY);
                Move result = dfs(level - 1, relation.getOther(), -beta, -alpha, targetUnit, toX, toY);
                gameMap.undoMove(unit, fromX, fromY, targetUnit);
                if (result == null) {
                    System.out.println("result is null");
                    continue;
                }
                int value = -result.value;
                if (value > alpha) {
                    alpha = value;
                    move = new Move(unit, toX, toY, value);
                    if (value > beta) {
                        return move;
                    }
                }
            }
        }
        return move;
    }


}
