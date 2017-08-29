package core;

import entity.Counter;
import entity.Point;
import enumeration.Color;
import helper.MapDriver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Analyzer {

    private GameMap gameMap;

    private List<Point> points;

    private Score score;

    private Color color;

    private Counter counter;

    private static int directX[] = {0, 1, 1, 1};

    private static int directY[] = {1, 1, 0, -1};

    private Set<Point> fiveAttack;

    private Set<Point> fourAttack;

    private Set<Point> threeOpenAttack;

    private Set<Point> fourDefence;

    private Set<Point> threeDefence;

    private Set<Point> twoAttack;

    private Set<Point> notKey;

    public Analyzer(GameMap gameMap, Color color, List<Point> points, Score score, Counter counter) {
        long time = System.currentTimeMillis();
        this.gameMap = gameMap;
        this.points = points;
        this.score = score;
        this.color = color;
        getAndDefence();
        counter.analyzeTime += System.currentTimeMillis() - time;
    }

    private void getAndDefence() {
        fiveAttack = new HashSet<>();
        fourAttack = new HashSet<>();
        threeOpenAttack = new HashSet<>();
        fourDefence = new HashSet<>();
        threeDefence = new HashSet<>();
        twoAttack = new HashSet<>();
        notKey = new HashSet<>();

        points.forEach(point -> {
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
                                if (tailColor == Color.NULL && headColor == Color.NULL) {
                                    threeOpenAttack.add(point);
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
                                threeDefence.add(point);
                            }
                        }
                    }
                    if (score.getColorCount(otherColor)[x][y][i] == 0 && score.getColorCount(color)[x][y][i] == 1) {
                        twoAttack.add(point);
                    }
                    x += directX[i];
                    y += directY[i];
                    if (!GameMap.reachable(x, y)) {
                        break;
                    }
                }
            }
        });

        threeOpenAttack.removeAll(fourAttack);

        twoAttack.removeAll(fourAttack);
        twoAttack.removeAll(fourDefence);
        twoAttack.removeAll(threeOpenAttack);
        twoAttack.removeAll(threeDefence);

        notKey = new HashSet<>(points);
        notKey.removeAll(fiveAttack);
        notKey.removeAll(fourAttack);
        notKey.removeAll(fourDefence);
        notKey.removeAll(threeDefence);
        notKey.removeAll(threeOpenAttack);
        notKey.removeAll(twoAttack);
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

    public static void main(String[] args) {
        GameMap gameMap = new GameMap(MapDriver.readMap());
        Score score = new Score();
        score.init(gameMap, Color.WHITE);
        Analyzer analyzer = new Analyzer(gameMap, Color.WHITE, gameMap.getNeighbor(), score, new Counter());

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
    }
}
