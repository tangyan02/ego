package ego.gomoku.core;

import ego.gomoku.entity.Counter;
import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;
import ego.gomoku.exception.TimeOutException;
import ego.gomoku.helper.ConsolePrinter;
import ego.gomoku.helper.WinChecker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {

    private GameMap gameMap;

    private Counter counter = new Counter();

    private ConsolePrinter consolePrinter = new ConsolePrinter();

    private ComboProcessor comboProcessor = new ComboProcessor();

    private Color aiColor;

    private Config config;

    private Score score = new Score();

    private long startTime;

    public void init(Color[][] map, Config config) {
        gameMap = new GameMap(map);
        this.config = config;
    }

    public Result search(Color color, boolean randomBegin) {
        //减少一秒的停止预估
        config.searchTimeOut -= 1000;

        Result result = new Result();
        Cache cache = new Cache(config, gameMap, counter);

        aiColor = color;
        if (WinChecker.win(gameMap.getMap()) != null) {
            return null;
        }
        //初始化
        consolePrinter.init(counter);
        score.init(gameMap, aiColor);
        comboProcessor.init(gameMap, score, counter, cache);

        //判断是否随机开局
        if (randomBegin) {
            Point point = BeginningProcessor.getBeginningRandomPoint(gameMap.getMap());
            if (point != null) {
                result.add(point, 0);
                return result;
            }
        }

        //只有一个扩展点的情形直接返回
        Analyzer data = new Analyzer(gameMap, color, gameMap.getNeighbor(), score, counter);
        List<Point> points = LevelProcessor.getExpandPoints(data);
        if (points.size() == 1) {
            result.add(points.get(0), 0);
            return result;
        }
        //算杀
        Set<Point> losePoints = new HashSet<>();
        int comboLevel = config.comboDeep;

        //连击的限时迭代，并预留一秒
        startTime = System.currentTimeMillis() - 1000;
        boolean currentColorEnd = false;
        try {
            for (int i = 1; i <= comboLevel; i += 2) {
                //我方直接的连击
                if (!currentColorEnd) {
                    ComboResult comboResult = comboProcessor.canKill(color, i, startTime, config.comboTimeOut);
                    Point winTry = comboResult.point;
                    //连击树已经搜完的情形
                    if (winTry == null && !comboResult.reachLastLevel) {
                        currentColorEnd = true;
                        System.out.println("current combo end");
                    }
                    if (winTry != null) {
                        result.add(winTry, Integer.MAX_VALUE);
                        return result;
                    }
                }
                //对方连击
                Set<Point> endOtherPoint = new HashSet<>();
                for (Point point : points) {
                    //已经确认失败的不再搜索
                    if (losePoints.contains(point)) {
                        continue;
                    }
                    //已经搜到极限的不再搜
                    if (endOtherPoint.contains(point)) {
                        continue;
                    }
                    gameMap.setColor(point, color.getOtherColor());

                    ComboResult comboResult = comboProcessor.canKill(color, i, startTime, config.comboTimeOut);
                    Point winTry = comboResult.point;
                    if (winTry == null && !comboResult.reachLastLevel) {
                        endOtherPoint.add(point);
                    }
                    if (winTry != null) {
                        losePoints.add(point);
                    }

                    gameMap.setColor(point, Color.NULL);
                }
                if (Config.debug) {
                    System.out.printf("combo level %s finish\n", i);
                }
            }
        } catch (TimeOutException ignored) {
            if (Config.debug) {
                System.out.println("combo time out");
            }
        }

        //限时迭代，并预留一秒
        startTime = System.currentTimeMillis() - 1000;
        //逐个计算，并记录
        counter.allStep = points.size();
        try {
            for (int level = 4; level <= config.searchDeep; level += 2) {
                int extreme = Integer.MIN_VALUE;
                Result currentResult = new Result();
                //把低层的最优解放到第一个处理
                if (result.point != null) {
                    points.remove(result.getPoint());
                    points.add(0, result.getPoint());
                }
                for (Point point : points) {
                    setColor(point, color, Color.NULL, aiColor);
                    int value;
                    if (!losePoints.contains(point)) {
                        value = dfsScore(level - 1, color.getOtherColor(), null, extreme);
                    } else {
                        value = Integer.MIN_VALUE;
                    }

                    counter.finishStep++;
                    consolePrinter.printInfo(point, value);

                    if (value >= extreme) {
                        extreme = value;
                        currentResult.add(point, value);

                        if (extreme == Integer.MAX_VALUE) {
                            currentResult.add(point, value);
                            break;
                        }
                    }
                    setColor(point, Color.NULL, color, aiColor);
                }
                if (Config.debug) {
                    System.out.printf("search level %s finish %n%n", level);
                }
                result = currentResult;
                //如果已经用掉一半的时间，则停止
                if (System.currentTimeMillis() - startTime > config.searchTimeOut / 2) {
                    break;
                }
            }
        } catch (TimeOutException ignored) {
            if (Config.debug) {
                System.out.println("time out");
            }
        }
        return result;
    }

    private int dfsScore(int level, Color color, Integer parentMin, Integer parentMax) throws TimeOutException {
        //是否超时判断
        if (System.currentTimeMillis() - startTime > config.searchTimeOut) {
            throw new TimeOutException();
        }
        //叶子分数计算
        if (level == 0) {
            return getScore();
        }
        //计算扩展节点
        Analyzer data = new Analyzer(gameMap, color, gameMap.getNeighbor(), score, counter);
        //输赢判定
        if (!data.getFiveAttack().isEmpty()) {
            if (color == aiColor) {
                return Integer.MAX_VALUE;
            }
            if (color == aiColor.getOtherColor()) {
                return Integer.MIN_VALUE;
            }
        }
        List<Point> points = LevelProcessor.getExpandPoints(data);
        //遍历扩展节点
        int extreme = color == aiColor ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Point point : points) {
            setColor(point, color, Color.NULL, aiColor);
            if (color == aiColor) {
                int value = dfsScore(level - 1, color.getOtherColor(), null, extreme);
                if (value > parentMin) {
                    setColor(point, Color.NULL, color, aiColor);
                    return value;
                }
                if (value > extreme) {
                    extreme = value;
                    //如果能赢了，则直接剪掉后面的情形
                    if (extreme == Integer.MAX_VALUE) {
                        setColor(point, Color.NULL, color, aiColor);
                        return extreme;
                    }
                }
            }
            if (color != aiColor) {
                int value = dfsScore(level - 1, color.getOtherColor(), extreme, null);
                if (value < parentMax) {
                    setColor(point, Color.NULL, color, aiColor);
                    return value;
                }
                if (value < extreme) {
                    extreme = value;
                    //如果已经输了，则直接剪掉后面的情形
                    if (extreme == Integer.MIN_VALUE) {
                        setColor(point, Color.NULL, color, aiColor);
                        return extreme;
                    }
                }
            }
            setColor(point, Color.NULL, color, aiColor);
        }
        return extreme;
    }

    private void setColor(Point point, Color color, Color forwardColor, Color aiColor) {
        score.setColor(point, color, forwardColor, aiColor);
        gameMap.setColor(point, color);
    }

    private int getScore() {
        counter.count++;
        return score.getMapScore();
    }
}
