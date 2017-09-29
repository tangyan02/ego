package ego.gomoku.core;

import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;
import ego.gomoku.helper.ConsolePrinter;
import ego.gomoku.helper.MapDriver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class LevelProcessor {

    static List<Point> getExpandPoints(Analyzer data, GameMap gameMap) {
        List<Point> result = selectSet(data, gameMap);

        if (result.isEmpty()) {
            result.add(new Point(Config.size / 2, Config.size / 2));
            return result;
        }

        return result;
    }

    private static List<Point> selectSet(Analyzer data, GameMap gameMap) {
        //如果能连5，则连5
        if (!data.getFiveAttack().isEmpty()) {
            return new ArrayList<>(data.getFiveAttack());
        }
        //如果有对方冲4，则防冲4
        if (!data.getFourDefence().isEmpty()) {
            return new ArrayList<>(data.getFourDefence());
        }
        //如果有对方活3，则防活3或者冲四
        if (!data.getThreeDefence().isEmpty()) {
            return new ArrayList<Point>(data.getFourAttack()) {{
                addAll(data.getThreeDefence());
            }};
        }
        //如果对手有双四进攻点
        if (!data.getDoubleFourCloseDefence().isEmpty()) {
            Point deadPoint = data.getDoubleFourCloseDefence().iterator().next();
            Set<Point> linePoints = gameMap.getPointLinesNeighbor(deadPoint);
            Set<Point> result = new HashSet<>();
            result.add(deadPoint);
            result.addAll(data.getFourAttack());
            data.getFourCloseDefence().forEach((k, v) -> {
                if (linePoints.contains(k)) {
                    result.add(k);
                }
            });
            return new ArrayList<>(result);
        }
        //如果对手有三四进攻点
        if (!data.getFourCloseAndOpenThreeDefense().isEmpty()) {
            Point deadPoint = data.getFourCloseAndOpenThreeDefense().iterator().next();
            Set<Point> linePoints = gameMap.getPointLinesNeighbor(deadPoint);
            Set<Point> result = new HashSet<>();
            result.addAll(data.getFourAttack());
            result.add(deadPoint);
            data.getThreeOpenDefence().forEach((k, v) -> {
                if (linePoints.contains(k)) {
                    result.add(k);
                }
            });
            data.getFourCloseDefence().forEach((k, v) -> {
                if (linePoints.contains(k)) {
                    result.add(k);
                }
            });
            return new ArrayList<>(result);
        }
        //如果对手有双三进攻点
        if (!data.getDoubleThreeOpenDefense().isEmpty()) {
            Point deadPoint = data.getDoubleThreeOpenDefense().iterator().next();
            Set<Point> linePoints = gameMap.getPointLinesNeighbor(deadPoint);
            Set<Point> result = new HashSet<>();
            result.add(deadPoint);
            result.addAll(data.getFourAttack());
            result.addAll(data.getThreeOpenAttack());
            data.getThreeOpenDefence().forEach((k, v) -> {
                if (linePoints.contains(k)) {
                    result.add(k);
                }
            });
            return new ArrayList<>(result);
        }

        List<Point> result = new ArrayList<>();
        result.addAll(data.getFourAttack());
        result.addAll(data.getThreeOpenAttack());
        result.addAll(data.getTwoAttack());
        result.addAll(data.getNotKey());

        //如果节点过多则减少一部分
        while (result.size() > Config.nodeLimit) {
            result = result.subList(0, Config.nodeLimit);
        }
        return result;
    }

    public static void main(String[] args) {
        //        GameMap gameMap = new GameMap(MapDriver.readMap("cases/blackCombo.txt"));
//        GameMap gameMap = new GameMap(MapDriver.readMap("cases/expand.txt"));
        GameMap gameMap = new GameMap(MapDriver.readMap());
        Score score = new Score();
        score.init(gameMap, Color.BLACK);
        Analyzer analyzer = new Analyzer(gameMap, Color.BLACK, gameMap.getNeighbor(), score, false);
        List<Point> points = getExpandPoints(analyzer, gameMap);
        ConsolePrinter.printMapWithPoints(gameMap, points);
        System.out.println(points);
    }
}
