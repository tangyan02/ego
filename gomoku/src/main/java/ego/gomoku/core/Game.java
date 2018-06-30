package ego.gomoku.core;

import ego.gomoku.entity.ComboResult;
import ego.gomoku.entity.Counter;
import ego.gomoku.entity.Point;
import ego.gomoku.entity.Result;
import ego.gomoku.enumeration.Color;
import ego.gomoku.helper.ConsolePrinter;
import ego.gomoku.helper.WinChecker;

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

    private boolean timeOut;

    public void init(Color[][] map, Config config) {
        gameMap = new GameMap(map);
        this.config = config;
    }

    public Result search(Color color, boolean randomBegin) {
        timeOut = false;

        Result result = new Result();
        Cache cache = new Cache(config, gameMap);

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
            Point point = BeginningProcessor.getBeginningRandomPoint(gameMap.getMap(), color);
            if (point != null) {
                result.add(point, 0);
                return result;
            }
        }

        //分析初始棋盘
        Analyzer data = new Analyzer(gameMap, color, gameMap.getNeighbor(), score, false);
        //只有一个扩展点的情形直接返回
        List<Point> points = LevelProcessor.getExpandPoints(data, gameMap);
        if (points.size() == 1) {
            result.add(points.get(0), 0);
            return result;
        }
        //算杀
        Set<Point> losePoints = new HashSet<>();
        //三连胜利的记录，三连胜利不能过早判断，否则会少计算后面对方的四连
        Set<Point> threeWins = new HashSet<>();
        int comboLevel = config.comboDeep;

        //连击的限时迭代，并预留一秒
        startTime = System.currentTimeMillis();
        boolean otherWin = false;

        for (int i = 1; i <= comboLevel; i += 3) {
            //我方直接的连击
            ComboResult comboResult = comboProcessor.canKill(color, i, startTime, config.comboTimeOut);
            Point winTry = comboResult.point;
            if (winTry != null) {
                if (comboResult.fourWin) {
                    result.add(winTry, Integer.MAX_VALUE);
                    return result;
                } else {
                    threeWins.add(winTry);
                }
            }
            //对方连击
            for (Point point : points) {
                //已经确认失败的不再搜索
                if (losePoints.contains(point)) {
                    continue;
                }
                setColor(point, color, Color.NULL, color);
                comboResult = comboProcessor.canKill(color.getOtherColor(), i, startTime, config.comboTimeOut);
                winTry = comboResult.point;
                if (winTry != null) {
                    losePoints.add(point);
                }
                setColor(point, Color.NULL, color, color);
            }
            if (losePoints.size() == points.size()) {
                otherWin = true;
            }
            if (comboResult.timeOut) {
                if (Config.debug) {
                    System.out.println("combo time out");
                }
                break;
            }
            if (Config.debug) {
                System.out.printf("combo level %s finish", i);
                if (losePoints.size() == points.size()) {
                    System.out.print(" other win");
                }
                System.out.println();
            }
            result.setComboLevel(i);
        }
        threeWins.removeAll(losePoints);
        if (threeWins.size() > 0) {
            result.add(threeWins.iterator().next(), Integer.MAX_VALUE);
            return result;
        }

        //如果已经输了，则朴素搜索
        if (otherWin) {
            losePoints.clear();
        }
        //限时迭代，并预留一秒
        startTime = System.currentTimeMillis();
        //逐个计算，并记录
        counter.allStep = points.size();

        for (int level = 2; level <= config.searchDeep; level += 2) {
            int extreme = Integer.MIN_VALUE;
            Result currentResult = new Result();
            //把低层的最优解放到第一个处理
            if (result.getPoint() != null) {
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
                if (timeOut) {
                    if (Config.debug) {
                        System.out.println("time out");
                    }
                    setColor(point, Color.NULL, color, aiColor);
                    break;
                }
                counter.finishStep++;
                consolePrinter.printInfo(point, value);

                if (value >= extreme) {
                    extreme = value;
                    currentResult.add(point, value);

                    if (extreme == Integer.MAX_VALUE) {
                        currentResult.add(point, value);
                        setColor(point, Color.NULL, color, aiColor);
                        break;
                    }
                }
                setColor(point, Color.NULL, color, aiColor);
            }
            if (Config.debug) {
                if (!timeOut) {
                    System.out.printf("search level %s finish %n", level);
                    System.out.println(currentResult.getPoint());
                    System.out.println();
                }
            }
            if (!timeOut) {
                currentResult.setComboLevel(result.getComboLevel());
                currentResult.setSearchLevel(level);
                result = currentResult;
            }
            //如果只有一个点能下，则停止
            if (losePoints.size() == points.size() - 1) {
                break;
            }
            //如果已经用掉不少的时间，则停止
            if (System.currentTimeMillis() - startTime > config.searchTimeOut / 4) {
                break;
            }
        }

        return result;
    }

    private int dfsScore(int level, Color color, Integer parentMin, Integer parentMax) {
        //是否超时判断
        if (System.currentTimeMillis() - startTime > config.searchTimeOut) {
            timeOut = true;
        }
        if (timeOut) {
            return 0;
        }
        //叶子分数计算
        if (level == 0) {
            return getScore();
        }
        //计算扩展节点
        Analyzer data = new Analyzer(gameMap, color, gameMap.getNeighbor(), score, false);
        //输赢判定
        if (!data.getFiveAttack().isEmpty()) {
            if (color == aiColor) {
                return Integer.MAX_VALUE;
            }
            if (color == aiColor.getOtherColor()) {
                return Integer.MIN_VALUE;
            }
        }
        List<Point> points = LevelProcessor.getExpandPoints(data, gameMap);
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
