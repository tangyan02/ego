package ego.chineseChess.core;

import ego.chineseChess.entity.Point;
import ego.chineseChess.entity.Relation;
import ego.chineseChess.entity.Troop;
import ego.chineseChess.entity.Unit;

import java.util.HashSet;
import java.util.List;

public class ScoreCalculator {

    public static int getScore(GameMap gameMap, Relation relation) {
        int sum = 0;
        HashSet<Unit> units = gameMap.getUnits();
        for (Unit unit : units) {
            if (unit.relation == Relation.SELF) {
                sum += unit.troop.getValue();
            }
            if (unit.relation == Relation.OPPONENT) {
                sum -= unit.troop.getValue();
            }
        }
        int attackScore = 0;
        for (Unit unit : units) {
            List<Point> points = MoveRuler.getMovePoint(gameMap, unit);
            for (Point point : points) {
                Unit target = gameMap.getUnit(point.x, point.y);
                if (target != null) {
                    if (target.troop == Troop.JIANG) {
                        continue;
                    }
                    attackScore += target.troop.getValue() / 100;
                }
            }
        }
        sum += attackScore;
        if (relation == Relation.OPPONENT) {
            sum = -sum;
        }
        return sum;
    }

}
