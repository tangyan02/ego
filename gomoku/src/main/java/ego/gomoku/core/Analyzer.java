package ego.gomoku.core;

import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;
import ego.gomoku.helper.ConsolePrinter;
import ego.gomoku.helper.MapDriver;

import java.util.*;

public class Analyzer {

    private boolean basic = false;

    private GameMap gameMap;

    private List<Point> points;

    private Score score;

    private Color color;

    private static int directX[] = {0, 1, 1, 1};

    private static int directY[] = {1, 1, 0, -1};

    private Set<Point> fiveAttack;

    private Set<Point> fourAttack;

    private Set<Point> threeOpenAttack;

    private Set<Point> fourDefence;

    private Set<Point> threeDefence;

    private Set<Point> twoAttack;

    private Set<Point> notKey;

    //更复杂的分析

    private Map<Point, Integer> fourCloseDefence;

    private Set<Point> doubleFourCloseDefence;

    private Map<Point, Integer> threeOpenDefence;

    private Set<Point> doubleThreeOpenDefense;

    private Set<Point> fourCloseAndOpenThreeDefense;

    Analyzer(GameMap gameMap, Color color, List<Point> points, Score score, boolean basic) {
        this.gameMap = gameMap;
        this.points = points;
        this.score = score;
        this.color = color;
        this.basic = basic;
        getAttackAndDefence();
    }

    private void getAttackAndDefence() {
        fiveAttack = new HashSet<>();
        fourAttack = new HashSet<>();
        threeOpenAttack = new HashSet<>();
        fourDefence = new HashSet<>();
        threeDefence = new HashSet<>();

        if (!basic) {
            twoAttack = new HashSet<>();
            notKey = new HashSet<>(points);
            fourCloseDefence = new HashMap<>();
            threeOpenDefence = new HashMap<>();
            doubleThreeOpenDefense = new HashSet<>();
            doubleFourCloseDefence = new HashSet<>();
            fourCloseAndOpenThreeDefense = new HashSet<>();
        }

        points.forEach(this::addAnalyze);

        removeRepeat();
    }

    private void removeRepeat() {
        threeOpenAttack.removeAll(fourAttack);

        if (!basic) {
            twoAttack.removeAll(fourAttack);
            twoAttack.removeAll(fourDefence);
            twoAttack.removeAll(threeOpenAttack);
            twoAttack.removeAll(threeDefence);

            notKey.removeAll(fiveAttack);
            notKey.removeAll(fourAttack);
            notKey.removeAll(fourDefence);
            notKey.removeAll(threeDefence);
            notKey.removeAll(threeOpenAttack);
            notKey.removeAll(twoAttack);
        }
    }

    private void addAnalyze(Point point) {
        for (int i = 0; i < 4; i++) {
            int x = point.getX();
            int y = point.getY();
            Color otherColor = color.getOtherColor();
            for (int k = 0; k < 5; k++) {
                if (score.getColorCount(otherColor)[x][y][i] == 0 && score.getColorCount(color)[x][y][i] == 4) {
                    fiveAttack.add(point);
                }
                if (score.getColorCount(otherColor)[x][y][i] == 0 && score.getColorCount(color)[x][y][i] == 3) {
                    fourAttack.add(point);
                }
                if (score.getColorCount(otherColor)[x][y][i] == 4 && score.getColorCount(color)[x][y][i] == 0) {
                    fourDefence.add(point);
                }
                if (score.getColorCount(otherColor)[x][y][i] == 0 && score.getColorCount(color)[x][y][i] == 2) {
                    if (k != 0 && k != 4) {
                        int headX = x - directX[i] * 4;
                        int headY = y - directY[i] * 4;
                        if (GameMap.reachable(headX, headY)) {
                            Color headColor = gameMap.getColor(headX, headY);
                            Color tailColor = gameMap.getColor(x, y);
                            if (headColor == Color.NULL && tailColor == Color.NULL) {
                                int sideX = x + directX[i];
                                int sideY = y + directY[i];
                                if (GameMap.reachable(sideX, sideY)) {
                                    Color sideColor = gameMap.getColor(sideX, sideY);
                                    if (sideColor == Color.NULL) {
                                        threeOpenAttack.add(point);
                                    }
                                }
                                sideX = headX - directX[i];
                                sideY = headY - directY[i];
                                if (GameMap.reachable(sideX, sideY)) {
                                    Color sideColor = gameMap.getColor(sideX, sideY);
                                    if (sideColor == Color.NULL) {
                                        threeOpenAttack.add(point);
                                    }
                                }
                            }
                            if (headColor == Color.NULL && tailColor != Color.NULL) {
                                int sideX = x + directX[i];
                                int sideY = y + directY[i];
                                if (GameMap.reachable(sideX, sideY)) {
                                    Color sideColor = gameMap.getColor(sideX, sideY);
                                    if (sideColor == Color.NULL) {
                                        threeOpenAttack.add(point);
                                    }
                                }
                            }
                            if (headColor != Color.NULL && tailColor == Color.NULL) {
                                int sideX = headX - directX[i];
                                int sideY = headY - directY[i];
                                if (GameMap.reachable(sideX, sideY)) {
                                    Color sideColor = gameMap.getColor(sideX, sideY);
                                    if (sideColor == Color.NULL) {
                                        threeOpenAttack.add(point);
                                    }
                                }
                            }
                        }
                    }
                }
                if (score.getColorCount(otherColor)[x][y][i] == 3 && score.getColorCount(color)[x][y][i] == 0) {
                    int headX = x - directX[i] * 4;
                    int headY = y - directY[i] * 4;
                    if (GameMap.reachable(headX, headY)) {
                        Color headColor = gameMap.getColor(headX, headY);
                        Color tailColor = gameMap.getColor(x, y);
                        if (headColor != Color.NULL && tailColor == Color.NULL) {
                            if (gameMap.getColor(x - directX[i], y - directY[i]) != Color.NULL) {
                                int sideX = headX - directX[i];
                                int sideY = headY - directY[i];
                                if (GameMap.reachable(sideX, sideY)) {
                                    Color sideColor = gameMap.getColor(sideX, sideY);
                                    if (sideColor == Color.NULL) {
                                        threeDefence.add(point);
                                    }
                                }
                            }
                        }
                        if (tailColor != Color.NULL && headColor == Color.NULL) {
                            if (gameMap.getColor(headX + directX[i], headY + directY[i]) != Color.NULL) {
                                int sideX = x + directX[i];
                                int sideY = y + directY[i];
                                if (GameMap.reachable(sideX, sideY)) {
                                    Color sideColor = gameMap.getColor(sideX, sideY);
                                    if (sideColor == Color.NULL) {
                                        threeDefence.add(point);
                                    }
                                }
                            }
                        }
                        if (headColor == Color.NULL && tailColor == Color.NULL) {
                            int sideX = x + directX[i];
                            int sideY = y + directY[i];
                            if (GameMap.reachable(sideX, sideY) &&
                                    gameMap.getColor(sideX, sideY) == Color.NULL) {
                                threeDefence.add(point);
                            }
                            sideX = headX - directX[i];
                            sideY = headY - directY[i];
                            if (GameMap.reachable(sideX, sideY) &&
                                    gameMap.getColor(sideX, sideY) == Color.NULL) {
                                threeDefence.add(point);
                            }
                        }
                    }
                }
                //更多的分析
                if (!basic) {
                    if (score.getColorCount(otherColor)[x][y][i] == 0 && score.getColorCount(color)[x][y][i] == 1) {
                        twoAttack.add(point);
                    }
                    if (score.getColorCount(otherColor)[x][y][i] == 3 && score.getColorCount(color)[x][y][i] == 0) {
                        //双四
                        if (fourCloseDefence.containsKey(point)) {
                            if (fourCloseDefence.get(point) != i) {
                                doubleFourCloseDefence.add(point);
                            }
                        }
                        //三四
                        if (threeOpenDefence.containsKey(point)) {
                            if (threeOpenDefence.get(point) != i) {
                                fourCloseAndOpenThreeDefense.add(point);
                            }
                        }
                        fourCloseDefence.put(point, i);
                    }
                    if (score.getColorCount(otherColor)[x][y][i] == 2 && score.getColorCount(color)[x][y][i] == 0) {
                        int headX = x - directX[i] * 4;
                        int headY = y - directY[i] * 4;
                        if (k != 0 && k != 4) {
                            if (GameMap.reachable(headX, headY)) {
                                Color headColor = gameMap.getColor(headX, headY);
                                Color tailColor = gameMap.getColor(x, y);
                                if (headColor == Color.NULL && tailColor == Color.NULL) {
                                    int sideX = x + directX[i];
                                    int sideY = y + directY[i];
                                    if (GameMap.reachable(sideX, sideY)) {
                                        Color sideColor = gameMap.getColor(sideX, sideY);
                                        if (sideColor == Color.NULL) {
                                            //双三
                                            if (threeOpenDefence.containsKey(point)) {
                                                if (threeOpenDefence.get(point) != i) {
                                                    doubleThreeOpenDefense.add(point);
                                                }
                                            }
                                            //四三
                                            if (fourCloseDefence.containsKey(point)) {
                                                if (fourCloseDefence.get(point) != i) {
                                                    fourCloseAndOpenThreeDefense.add(point);
                                                }
                                            }
                                            threeOpenDefence.put(point, i);
                                        }
                                    }
                                    sideX = headX - directX[i];
                                    sideY = headY - directY[i];
                                    if (GameMap.reachable(sideX, sideY)) {
                                        Color sideColor = gameMap.getColor(sideX, sideY);
                                        if (sideColor == Color.NULL) {
                                            //双三
                                            if (threeOpenDefence.containsKey(point)) {
                                                if (threeOpenDefence.get(point) != i) {
                                                    doubleThreeOpenDefense.add(point);
                                                }
                                            }
                                            //四三
                                            if (fourCloseDefence.containsKey(point)) {
                                                if (fourCloseDefence.get(point) != i) {
                                                    fourCloseAndOpenThreeDefense.add(point);
                                                }
                                            }
                                            threeOpenDefence.put(point, i);
                                        }
                                    }
                                }
                                if (headColor == Color.NULL && tailColor != Color.NULL) {
                                    int sideX = x + directX[i];
                                    int sideY = y + directY[i];
                                    if (GameMap.reachable(sideX, sideY)) {
                                        Color sideColor = gameMap.getColor(sideX, sideY);
                                        if (sideColor == Color.NULL) {
                                            //双三
                                            if (threeOpenDefence.containsKey(point)) {
                                                if (threeOpenDefence.get(point) != i) {
                                                    doubleThreeOpenDefense.add(point);
                                                }
                                            }
                                            //四三
                                            if (fourCloseDefence.containsKey(point)) {
                                                if (fourCloseDefence.get(point) != i) {
                                                    fourCloseAndOpenThreeDefense.add(point);
                                                }
                                            }
                                            threeOpenDefence.put(point, i);
                                        }
                                    }
                                }
                                if (headColor != Color.NULL && tailColor == Color.NULL) {
                                    int sideX = headX - directX[i];
                                    int sideY = headY - directY[i];
                                    if (GameMap.reachable(sideX, sideY)) {
                                        Color sideColor = gameMap.getColor(sideX, sideY);
                                        if (sideColor == Color.NULL) {
                                            //双三
                                            if (threeOpenDefence.containsKey(point)) {
                                                if (threeOpenDefence.get(point) != i) {
                                                    doubleThreeOpenDefense.add(point);
                                                }
                                            }
                                            //四三
                                            if (fourCloseDefence.containsKey(point)) {
                                                if (fourCloseDefence.get(point) != i) {
                                                    fourCloseAndOpenThreeDefense.add(point);
                                                }
                                            }
                                            threeOpenDefence.put(point, i);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                x += directX[i];
                y += directY[i];
                if (!GameMap.reachable(x, y)) {
                    break;
                }
            }
        }
    }

    public Set<Point> getFiveAttack() {
        return fiveAttack;
    }

    public Set<Point> getFourAttack() {
        return fourAttack;
    }

    public Set<Point> getThreeOpenAttack() {
        return threeOpenAttack;
    }

    public Set<Point> getFourDefence() {
        return fourDefence;
    }

    public Set<Point> getThreeDefence() {
        return threeDefence;
    }

    public Set<Point> getTwoAttack() {
        return twoAttack;
    }

    public Set<Point> getNotKey() {
        return notKey;
    }

    public Map<Point, Integer> getFourCloseDefence() {
        return fourCloseDefence;
    }

    public Set<Point> getDoubleFourCloseDefence() {
        return doubleFourCloseDefence;
    }

    public Map<Point, Integer> getThreeOpenDefence() {
        return threeOpenDefence;
    }

    public Set<Point> getDoubleThreeOpenDefense() {
        return doubleThreeOpenDefense;
    }

    public Set<Point> getFourCloseAndOpenThreeDefense() {
        return fourCloseAndOpenThreeDefense;
    }

    public static void main(String[] args) {
//        GameMap gameMap = new GameMap(MapDriver.readMap("cases/blackCombo.txt"));
//        GameMap gameMap = new GameMap(MapDriver.readMap("cases/analyze.txt"));
        GameMap gameMap = new GameMap(MapDriver.readMap());
        ConsolePrinter.printMap(gameMap);
        Score score = new Score();
        score.init(gameMap, Color.BLACK);
        Analyzer analyzer = new Analyzer(gameMap, Color.BLACK, gameMap.getNeighbor(), score, false);
        ConsolePrinter.printMapWithPoints(gameMap, analyzer.getFourCloseAndOpenThreeDefense());

        System.out.println("FIVE A");
        System.out.println(analyzer.getFiveAttack());
        System.out.println("FOUR A");

        System.out.println(analyzer.getFourAttack());

        System.out.println("FOUR D");
        System.out.println(analyzer.getFourDefence());

        System.out.println("THREE A");
        System.out.println(analyzer.getThreeOpenAttack());

        System.out.println("THREE D");
        System.out.println(analyzer.getThreeDefence());

        System.out.println("TWO A");
        System.out.println(analyzer.getTwoAttack());

        System.out.println("OTHER");
        System.out.println(analyzer.getNotKey());

        System.out.println("C FOUR D");
        System.out.println(analyzer.getFourCloseDefence());

        System.out.println("2C FOUR D");
        System.out.println(analyzer.getDoubleFourCloseDefence());

        System.out.println("C THREE D");
        System.out.println(analyzer.getThreeOpenDefence());

        System.out.println("2C THREE D");
        System.out.println(analyzer.getDoubleThreeOpenDefense());

        System.out.println("C THREE D and C FOUR D");
        System.out.println(analyzer.getFourCloseAndOpenThreeDefense());
    }
}
