package ego.gomoku.core;

import ego.gomoku.entity.ComboResult;
import ego.gomoku.entity.Counter;
import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;
import ego.gomoku.enumeration.ComboTye;
import ego.gomoku.exception.TimeOutException;
import ego.gomoku.helper.MapDriver;
import ego.gomoku.helper.ConsolePrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComboProcessor {

    private GameMap gameMap;

    private Score score;

    private Cache cache;

    private ComboResult result;

    private int currentLevel;

    private long startTime;

    private long limitTime;

    public void init(GameMap gameMap, Score score, Counter counter, Cache cache) {
        this.gameMap = gameMap;
        this.score = score;
        this.cache = cache;
    }

    ComboResult canKill(Color targetColor, int level, long startTime, long limitTime) {
        if (level % 2 == 0) {
            level++;
        }
        this.startTime = startTime;
        this.limitTime = limitTime;
        currentLevel = level;
        //连击结果初始化
        result = new ComboResult();
        //计算我方四连杀
        result.point = null;
        cache.clear();
        dfsKill(gameMap, targetColor, targetColor,
                level, score, ComboTye.FOUR,
                null, null, null);
        if (result.point != null) {
            result.fourWin = true;
            return result;
        }

        //计算对手四连杀
        result.point = null;
        cache.clear();
        dfsKill(gameMap, targetColor.getOtherColor(), targetColor.getOtherColor(),
                level, score, ComboTye.FOUR,
                null, null, null);
        if (result.point != null) {
            result.point = null;
            result.fourWin = true;
            return result;
        }

        //计算我方三连杀
        result.point = null;
        cache.clear();
        dfsKill(gameMap, targetColor, targetColor,
                level, score, ComboTye.THREE,
                null, null, null);
        result.fourWin = false;
        return result;
    }

    private boolean dfsKill(GameMap gameMap, Color color, Color targetColor,
                            int level, Score score, ComboTye comboTye,
                            Set<Point> nextRange, Set<Point> oldRange, Point lastPoint) {
        //超时计算
        if (System.currentTimeMillis() - startTime > limitTime) {
            result.timeOut = true;
            return false;
        }
        //缓存
        Boolean cacheResult = cache.getComboResult();
        if (cacheResult != null) {
            return cacheResult;
        }

        if (level == 0) {
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
        Analyzer data = new Analyzer(gameMap, color, rangePoints, score, true);
        //如果对面形成活三，则转换会冲四
        if (comboTye == ComboTye.THREE) {
            if (color == targetColor && !data.getThreeDefence().isEmpty()) {
                comboTye = ComboTye.FOUR;
            }
        }
        if (color == targetColor) {
            if (data.getFiveAttack().size() > 0) {
                if (level == currentLevel) {
                    result.point = data.getFiveAttack().iterator().next();
                }
                return returnValue(true);
            }
            List<Point> points = getComboAttackPoints(data, comboTye);
            for (Point point : points) {
                setColor(point, color, Color.NULL, targetColor, score, gameMap);
                Set<Point> newNextRange = gameMap.getPointLinesNeighbor(point);
                boolean value = dfsKill(gameMap, color.getOtherColor(), targetColor, level - 1, score, comboTye, newNextRange, nextRange, point);
                if (level == currentLevel && value) {
                    result.point = point;
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
                Set<Point> newNextRange = gameMap.getPointLinesNeighbor(point);
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

    public static void main(String[] args) throws TimeOutException {
//        Color[][] colors = MapDriver.readMap("cases/blackCombo.txt");
        Color[][] colors = MapDriver.readMap();
        GameMap gameMap = new GameMap(colors);
        ConsolePrinter.printMap(gameMap);
        Score score = new Score();
        Color color = Color.WHITE;
        score.init(gameMap, color);
        long time = System.currentTimeMillis();
        Config config = new Config();
        config.comboDeep = 15;
        ComboProcessor comboProcessor = new ComboProcessor();
        comboProcessor.init(gameMap, score, new Counter(), new Cache(config, gameMap));
        System.out.println(comboProcessor.canKill(color, config.comboDeep, System.currentTimeMillis(), config.comboTimeOut).point);
        System.out.println(System.currentTimeMillis() - time + "ms");
    }
}
