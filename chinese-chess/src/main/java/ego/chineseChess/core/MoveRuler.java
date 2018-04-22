package ego.chineseChess.core;

import ego.chineseChess.entity.Point;
import ego.chineseChess.entity.Relation;
import ego.chineseChess.entity.Troop;
import ego.chineseChess.entity.Unit;

import java.util.ArrayList;
import java.util.List;

public class MoveRuler {

    private final static int[] dx = {0, 0, 1, -1};
    private final static int[] dy = {1, -1, 0, 0};

    private final static int[] dxMa = {1, 2, 1, 2, -1, -2, -1, -2};
    private final static int[] dyMa = {2, 1, -2, -1, 2, 1, -2, -1};

    private final static int[] dxMaBarrier = {0, 1, 0, 1, 0, -1, 0, -1};
    private final static int[] dyMaBarrier = {1, 0, -1, 0, 1, 0, -1, 0};

    private final static int[] dxXiang = {2, 2, -2, -2};
    private final static int[] dyXiang = {-2, 2, 2, -2};

    private final static int[] dxXiangBarrier = {1, 1, -1, -1};
    private final static int[] dyXiangBarrier = {-1, 1, 1, -1};

    private final static int[] dxShi = {1, 1, -1, -1};
    private final static int[] dyShi = {-1, 1, 1, -1};

    public static List<Point> getMovePoint(GameMap gameMap, Unit unit) {
        List<Point> points = new ArrayList<>();
        if (unit.troop == Troop.CHE) {
            for (int direct = 0; direct < 4; direct++) {
                int x = unit.x;
                int y = unit.y;
                while (true) {
                    x += dx[direct];
                    y += dy[direct];
                    if (!gameMap.moveOrAttackAble(x, y)) {
                        break;
                    }
                    points.add(new Point(x, y));
                    if (gameMap.getUnit(x, y) != null) {
                        break;
                    }
                }
            }
        }
        if (unit.troop == Troop.MA) {
            for (int i = 0; i < 8; i++) {
                int x = unit.x + dxMa[i];
                int y = unit.y + dyMa[i];
                if (!gameMap.moveOrAttackAble(x, y)) {
                    continue;
                }
                int barrierX = unit.x + dxMaBarrier[i];
                int barrierY = unit.y + dyMaBarrier[i];
                if (gameMap.getUnit(barrierX, barrierY) != null) {
                    continue;
                }
                points.add(new Point(x, y));
            }
        }
        if (unit.troop == Troop.XIANG) {
            for (int i = 0; i < 4; i++) {
                int x = unit.x + dxXiang[i];
                int y = unit.y + dyXiang[i];
                if (!gameMap.moveOrAttackAble(x, y)) {
                    continue;
                }
                if (!inSide(x, unit.relation)) {
                    continue;
                }
                int barrierX = unit.x + dxXiangBarrier[i];
                int barrierY = unit.y + dyXiangBarrier[i];
                if (gameMap.getUnit(barrierX, barrierY) != null) {
                    continue;
                }
                points.add(new Point(x, y));
            }
        }
        if (unit.troop == Troop.SHI) {
            for (int i = 0; i < 4; i++) {
                int x = unit.x + dxShi[i];
                int y = unit.y + dyShi[i];
                if (!gameMap.moveOrAttackAble(x, y)) {
                    continue;
                }
                if (!inBase(x, y, unit.relation)) {
                    continue;
                }
                points.add(new Point(x, y));
            }
        }
        if (unit.troop == Troop.JIANG) {
            for (int i = 0; i < 4; i++) {
                int x = unit.x + dx[i];
                int y = unit.y + dy[i];
                if (!gameMap.moveOrAttackAble(x, y)) {
                    continue;
                }
                if (!inBase(x, y, unit.relation)) {
                    continue;
                }
                points.add(new Point(x, y));
            }
            //攻击对方阵营将
            int x = unit.x;
            int y = unit.y;
            while (true) {
                x = unit.relation == Relation.SELF ? x - 1 : x + 1;
                if (!gameMap.moveAble(x, y)) {
                    if (gameMap.inMap(x, y)) {
                        Unit target = gameMap.getUnit(x, y);
                        if (target.troop == Troop.JIANG) {
                            points.add(new Point(x, y));
                        }
                    }
                    break;
                }
            }
        }
        if (unit.troop == Troop.PAO) {
            for (int direct = 0; direct < 4; direct++) {
                int x = unit.x;
                int y = unit.y;
                while (true) {
                    x += dx[direct];
                    y += dy[direct];
                    if (!gameMap.inMap(x, y)) {
                        break;
                    }
                    if (gameMap.getUnit(x, y) != null) {
                        //找个目标攻击
                        while (true) {
                            x += dx[direct];
                            y += dy[direct];
                            if (!gameMap.inMap(x, y)) {
                                break;
                            }
                            if (gameMap.getUnit(x, y) != null) {
                                if (gameMap.attackAble(x, y)) {
                                    points.add(new Point(x, y));
                                }
                                break;
                            }
                        }
                        break;
                    } else {
                        //普通移动
                        points.add(new Point(x, y));
                    }
                }
            }
        }
        if (unit.troop == Troop.BING) {
            int x = unit.relation == Relation.SELF ? unit.x - 1 : unit.x + 1;
            int y = unit.y;
            if (gameMap.moveOrAttackAble(x, y)) {
                points.add(new Point(x, y));
            }
            if (!inSide(unit.x, unit.relation)) {
                if (gameMap.moveOrAttackAble(x, y - 1)) {
                    points.add(new Point(x, y - 1));
                }
                if (gameMap.moveOrAttackAble(x, y + 1)) {
                    points.add(new Point(x, y + 1));
                }
            }
        }
        return points;
    }

    /**
     * 是否在自己的河内
     */
    private static boolean inSide(int x, Relation relation) {
        if (relation == Relation.SELF && x >= 5) {
            return true;
        }
        if (relation == Relation.OPPONENT && x <= 4) {
            return true;
        }
        return false;
    }

    /**
     * 是否在自己的基地内
     */
    private static boolean inBase(int x, int y, Relation relation) {
        if (relation == Relation.SELF) {
            if (x >= 7 && x <= 9 && y >= 3 && y <= 5) {
                return true;
            }
        }
        if (relation == Relation.OPPONENT) {
            if (x >= 0 && x <= 2 && y >= 3 && y <= 5) {
                return true;
            }
        }
        return false;
    }


}
