package ego.gomoku.core;

import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;
import ego.gomoku.helper.MapDriver;

import java.util.*;

public class GameMap {

    private int directX[] = {0, 1, 1, 1, 0, -1, -1, -1};
    private int directY[] = {1, 1, 0, -1, -1, -1, 0, 1};

    private Color[][] map;

    private Set<Point> neighbor = new HashSet<>();
    private Map<Point, List<Point>> historyAdd = new HashMap<>();

    private long hashCode = 0;
    private static long[][] weightBlack = new long[Config.size][Config.size];
    private static long[][] weightWhite = new long[Config.size][Config.size];

    static {
        Random random = new Random();
        for (int i = 0; i < Config.size; i++)
            for (int j = 0; j < Config.size; j++) {
                weightBlack[i][j] = random.nextLong();
                weightWhite[i][j] = random.nextLong();
            }
    }

    GameMap(Color[][] map) {
        this.map = map;
        for (int i = 0; i < Config.size; i++)
            for (int j = 0; j < Config.size; j++) {
                Color color = getColor(i, j);
                if (color != Color.NULL) {
                    updateNeighbor(new Point(i, j), color);
                    updateHashCode(i, j, color);
                }
            }
    }

    private static boolean reachable(Point point) {
        if (point.getX() < 0 || point.getX() >= Config.size)
            return false;
        if (point.getY() < 0 || point.getY() >= Config.size)
            return false;
        return true;
    }

    static boolean reachable(int x, int y) {
        if (x < 0 || x >= Config.size)
            return false;
        if (y < 0 || y >= Config.size)
            return false;
        return true;
    }

    public Color[][] getMap() {
        return map;
    }

    Color getColor(Point point) {
        if (!reachable(point)) {
            return null;
        }
        return map[point.getX()][point.getY()];
    }

    Color getColor(int x, int y) {
        return map[x][y];
    }

    void setColor(Point point, Color color) {
        if (color != Color.NULL) {
            updateHashCode(point.getX(), point.getY(), color);
        } else {
            updateHashCode(point.getX(), point.getY(), map[point.getX()][point.getY()]);
        }
        map[point.getX()][point.getY()] = color;
        updateNeighbor(point, color);
    }

    private void updateNeighbor(Point point, Color pointColor) {
        if (pointColor != Color.NULL) {
            List<Point> points = new ArrayList<>();
            neighbor.remove(point);
            for (int i = 0; i < 8; i++) {
                int x = point.getX() + directX[i];
                int y = point.getY() + directY[i];
                if (reachable(x, y)) {
                    Color color = getColor(x, y);
                    if (getColor(x, y) == Color.NULL) {
                        Point newPoint = new Point(x, y);
                        if (!neighbor.contains(newPoint)) {
                            neighbor.add(newPoint);
                            points.add(newPoint);
                        }
                    } else {
                        if (color == pointColor) {
                            int x1 = point.getX() + directX[i] * 3;
                            int y1 = point.getY() + directY[i] * 3;
                            int x2 = point.getX() - directX[i] * 2;
                            int y2 = point.getY() - directY[i] * 2;
                            Point point1 = new Point(x1, y1);
                            Point point2 = new Point(x2, y2);
                            if (reachable(x1, y1)) {
                                if (getColor(x1, y1) == Color.NULL) {
                                    if (!neighbor.contains(point1)) {
                                        neighbor.add(point1);
                                        points.add(point1);
                                    }
                                }
                            }
                            if (reachable(x2, y2)) {
                                if (getColor(x2, y2) == Color.NULL) {
                                    if (!neighbor.contains(point2)) {
                                        neighbor.add(point2);
                                        points.add(point2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            historyAdd.put(point, points);
        } else {
            List<Point> points = historyAdd.get(point);
            neighbor.removeAll(points);
            neighbor.add(point);
            historyAdd.remove(point);
        }
    }

    List<Point> getNeighbor() {
        return new ArrayList<>(neighbor);
    }

    Set<Point> getPointLinesNeighbor(Point point) {
        Set<Point> result = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            int x = point.getX();
            int y = point.getY();
            for (int k = 0; k < 4; k++) {
                x += directX[i];
                y += directY[i];
                if (!reachable(x, y)) {
                    break;
                }
                if (map[x][y] != Color.NULL)
                    continue;
                Point newPoint = new Point(x, y);
                if (neighbor.contains(newPoint)) {
                    result.add(newPoint);
                }
            }
        }
        return result;
    }

    long getHashCode() {
        return hashCode;
    }

    private void updateHashCode(int x, int y, Color color) {
        if (color != Color.NULL) {
            if (color == Color.BLACK) {
                hashCode ^= weightBlack[x][y];
            }
            if (color == Color.WHITE) {
                hashCode ^= weightWhite[x][y];
            }
        }
    }

    public static void main(String[] args) {
        Color[][] map = MapDriver.readMap("cases/normal.txt");
        GameMap gameMap = new GameMap(map);
        List<Point> points = gameMap.getNeighbor();
        long hashCode = gameMap.getHashCode();
        gameMap.setColor(points.get(0), Color.WHITE);
        gameMap.setColor(points.get(1), Color.BLACK);
        gameMap.setColor(points.get(0), Color.NULL);
        gameMap.setColor(points.get(1), Color.NULL);
        long hashCode2 = gameMap.getHashCode();
        assert hashCode == hashCode2;
        gameMap.getNeighbor();

        Set<Point> linePoints = gameMap.getPointLinesNeighbor(new Point(9, 6));
        assert linePoints.size() > 0;

    }

}
