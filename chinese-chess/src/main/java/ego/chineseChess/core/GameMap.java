package ego.chineseChess.core;

import ego.chineseChess.entity.Point;
import ego.chineseChess.entity.Unit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameMap {

    private HashSet<Unit> units;

    private Unit[][] map = new Unit[Config.HEIGHT][Config.WIDTH];

    public GameMap(List<Unit> units) {
        this.units = new HashSet<>(units);
        units.forEach(unit -> map[unit.x][unit.y] = unit);
    }

    public void move(Unit unit, int x, int y) {
        if (map[x][y] != null) {
            units.remove(map[x][y]);
        }
        map[x][y] = unit;
        unit.x = x;
        unit.y = y;
    }

    public HashSet<Unit> getUnits() {
        return units;
    }

    public Unit getUnit(int x, int y) {
        return map[x][y];
    }

    public boolean movable(Unit unit, int x, int y) {
        if (x < 0 || x >= Config.HEIGHT)
            return false;
        if (y < 0 || y >= Config.WIDTH)
            return false;
        if (map[x][y] != null) {
            if (map[x][y].relation == unit.relation) {
                return false;
            }
        }
        return true;
    }
}
