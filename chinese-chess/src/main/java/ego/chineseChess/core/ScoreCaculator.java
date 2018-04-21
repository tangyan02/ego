package ego.chineseChess.core;

import ego.chineseChess.entity.Relation;
import ego.chineseChess.entity.Unit;

import java.util.HashSet;

public class ScoreCaculator {

    public static int getScore(GameMap gameMap) {
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

        return sum;
    }

}
