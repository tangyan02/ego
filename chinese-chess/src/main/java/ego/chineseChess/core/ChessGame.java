package ego.chineseChess.core;

import ego.chineseChess.entity.*;

import java.util.List;

public class ChessGame {

    private GameMap gameMap;

    private ScoreCache scoreCache = new ScoreCache();

    private int count = 0;

    public ChessGame(List<Unit> units) {
        gameMap = new GameMap(units);
    }

    public PlayResult play(Relation relation) {
        count = 0;
        int alpha = Integer.MIN_VALUE / 2;
        int beta = Integer.MAX_VALUE / 2;
        Move move = dfs(Config.level, relation, alpha, beta, null, null, null, false);
        return new PlayResult(move, count);
    }

    private Move dfs(int level, Relation relation, int alpha, int beta, Unit currentUnit, Integer currentX, Integer currentY, boolean check) {
        count++;
        //缓存检查，实际缓存命中率不足20%
//        Move cacheValue = scoreCache.get(gameMap, level, alpha, beta);
//        if (cacheValue != null) {
//            return cacheValue;
//        }
        if (level == 0 || check) {
            int value = ScoreCalculator.getScore(gameMap, relation);
            return new Move(currentUnit, currentX, currentY, value);
        }
        int currentMax = Integer.MIN_VALUE;
        List<MoveStep> moves = MoveRuler.getMoves(gameMap, relation);
        Move move = new Move();
        for (MoveStep moveStep : moves) {
            Unit unit = moveStep.unit;

            int fromX = unit.x;
            int fromY = unit.y;
            int toX = moveStep.x;
            int toY = moveStep.y;
            Unit targetUnit = gameMap.getUnit(toX, toY);
            //己方单位不攻击
            if (targetUnit != null) {
                if (targetUnit.relation == unit.relation) {
                    continue;
                }
            }
            //将军判断
            if (targetUnit != null && targetUnit.troop == Troop.JIANG) {
                check = true;
            }
            gameMap.move(unit, toX, toY);
            Move result = dfs(level - 1, relation.getOther(), -beta, -alpha, targetUnit, toX, toY, check);
            gameMap.undoMove(unit, fromX, fromY, targetUnit);
            check = false;
            int value = -result.value;
            if (value > currentMax) {
                currentMax = value;
                move.set(unit, toX, toY, value);
                if (value > alpha) {
                    alpha = value;
                    if (value > beta) {
                        //scoreCache.record(gameMap, level, alpha, beta, move);
                        return move;
                    }
                }
            }
        }
        //scoreCache.record(gameMap, level, alpha, beta, move);
        return move;
    }


}
