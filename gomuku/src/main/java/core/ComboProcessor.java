package core;

import entity.Counter;
import entity.Point;
import enumeration.Color;
import enumeration.ComboTye;
import helper.ConsolePrinter;
import helper.MapDriver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ComboProcessor {

    private GameMap gameMap;

    private Score score;

    private Counter counter;

    private Config config;

    private Cache cache;

    private Point result;

    public void init(GameMap gameMap, Score score, Counter counter, Config config, Cache cache) {
        this.gameMap = gameMap;
        this.score = score;
        this.counter = counter;
        this.config = config;
        this.cache = cache;
    }

    Point canKill(Color targetColor) {
        //计算我方四连杀
        result = null;
        cache.clear();
        dfsKill(gameMap, targetColor, targetColor, config.comboDeep, score, ComboTye.FOUR, null, null, null);
        if (result != null)
            return result;

        //计算对手四连杀
        result = null;
        cache.clear();
        dfsKill(gameMap, targetColor.getOtherColor(), targetColor.getOtherColor(), config.comboDeep, score, ComboTye.FOUR, null, null, null);
        if (result != null)
            return null;

        //死算我方三连杀
        result = null;
        cache.clear();
        dfsKill(gameMap, targetColor, targetColor, config.comboDeep, score, ComboTye.THREE, null, null, null);
        return result;
    }

    private boolean dfsKill(GameMap gameMap, Color color, Color targetColor,
                            int level, Score score, ComboTye comboTye,
                            Set<Point> nextRange, Set<Point> oldRange, Point lastPoint) {
        //缓存
        Boolean cacheResult = cache.getComboResult();
        if (cacheResult != null) {
            return cacheResult;
        }

        if (level == 0) {
            counter.countCombo++;
            return returnValue(false);
        }
        //选取邻近的点计算连击
        List<Point> rangePoints;
        Set<Point> rangeSet = new HashSet<>();
        if (nextRange != null)
            rangeSet.addAll(nextRange);
        if (oldRange != null)
            rangeSet.addAll(oldRange);
        if (rangeSet.isEmpty()) {
            rangePoints = gameMap.getNeighbor();
        } else {
            rangeSet.remove(lastPoint);
            rangePoints = new ArrayList<>(rangeSet);
        }
        //分析选取的点
        Analyzer data = new Analyzer(gameMap, color, rangePoints, score, counter);
        //如果对面形成活三，则转换会冲四
        if (comboTye == ComboTye.THREE) {
            if (color == targetColor && !data.getThreeDefence().isEmpty()) {
                comboTye = ComboTye.FOUR;
            }
        }
        if (color == targetColor) {
            if (data.getFiveAttack().size() > 0) {
                counter.countCombo++;
                return returnValue(true);
            }
            List<Point> points = getComboAttackPoints(data, comboTye);
            for (Point point : points) {
                setColor(point, color, Color.NULL, targetColor, score, gameMap);
                Set<Point> newNextRange = gameMap.getPointLineNeibor(point);
                boolean value = dfsKill(gameMap, color.getOtherColor(), targetColor, level - 1, score, comboTye, newNextRange, nextRange, point);
                if (level == config.comboDeep && value) {
                    result = point;
                }
                if (value) {
                    setColor(point, Color.NULL, color, targetColor, score, gameMap);
                    return returnValue(true);
                }
                setColor(point, Color.NULL, color, targetColor, score, gameMap);
            }
            return returnValue(false);
        } else {
            if (data.getFiveAttack().size() > 0) {
                return returnValue(false);
            }
            List<Point> points = getComboDefencePoints(data, comboTye);
            //如果没有能防的则结束
            if (points.size() == 0) {
                return returnValue(false);
            }
            for (Point point : points) {
                setColor(point, color, Color.NULL, targetColor, score, gameMap);
                Set<Point> newNextRange = gameMap.getPointLineNeibor(point);
                boolean value = dfsKill(gameMap, color.getOtherColor(), targetColor, level - 1, score, comboTye, newNextRange, nextRange, point);
                if (!value) {
                    setColor(point, Color.NULL, color, targetColor, score, gameMap);
                    return returnValue(false);
                }
                setColor(point, Color.NULL, color, targetColor, score, gameMap);
            }
            return returnValue(true);
        }
    }

    private List<Point> getComboAttackPoints(Analyzer data, ComboTye comboTye) {
        //如果有对方冲4，则防冲4
        if (!data.getFourDefence().isEmpty()) {
            return new ArrayList<>(data.getFourDefence());
        }
        //如果有对方活3，冲四
        if (!data.getThreeDefence().isEmpty()) {
            return new ArrayList<>(data.getFourAttack());
        }
        List<Point> result = new ArrayList<>();
        result.addAll(data.getFourAttack());
        if (comboTye == ComboTye.THREE) {
            result.addAll(data.getThreeOpenAttack());
        }
        return result;
    }

    private List<Point> getComboDefencePoints(Analyzer data, ComboTye comboTye) {
        //如果有对方冲4，则防冲4
        if (!data.getFourDefence().isEmpty()) {
            return new ArrayList<>(data.getFourDefence());
        }
        if (comboTye == ComboTye.THREE) {
            //如果有对方活3，则防活3或者冲四
            if (!data.getThreeDefence().isEmpty()) {
                return new ArrayList<Point>(data.getThreeDefence()) {{
                    addAll(data.getFourAttack());
                }};
            }
        }
        return new ArrayList<>();
    }

    private boolean returnValue(boolean value) {
        cache.recordComboResult(value);
        return value;
    }

    private void setColor(Point point, Color color, Color forwardColor, Color aiColor, Score score, GameMap gameMap) {
        score.setColor(point, color, forwardColor, aiColor);
        gameMap.setColor(point, color);
    }

    public static void main(String[] args) {
        Color[][] colors = MapDriver.readMap("score/blackCombo.txt");
//        Color[][] colors = MapDriver.readMap();
        GameMap gameMap = new GameMap(colors);
        ConsolePrinter.printMap(gameMap);
        Score score = new Score();
        score.init(gameMap, Color.BLACK);
        long time = System.currentTimeMillis();
        Config config = new Config();
        config.comboDeep = 15;
        ComboProcessor comboProcessor = new ComboProcessor();
        comboProcessor.init(gameMap, score, new Counter(), config, new Cache(config, gameMap, new Counter()));
        System.out.println(comboProcessor.canKill(Color.BLACK));
        System.out.println(System.currentTimeMillis() - time + "ms");
    }
}
