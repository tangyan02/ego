package ego.chineseChess.core;

import ego.chineseChess.entity.Relation;
import ego.chineseChess.entity.Unit;

import java.util.HashSet;

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
        if (relation == Relation.OPPONENT) {
            sum = -sum;
        }
        return sum;
    }

}
