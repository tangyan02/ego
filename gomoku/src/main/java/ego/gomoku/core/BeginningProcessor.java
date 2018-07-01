package ego.gomoku.core;

import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;
import ego.gomoku.helper.ConsolePrinter;
import ego.gomoku.helper.MapDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BeginningProcessor {

    static Point getBeginningRandomPoint(Color[][] map, Color aiColor) {
        if (aiColor == Color.BLACK) {
            int count = 0;
            int mid = Config.size / 2;
            Point whitePoint = null;
            for (int i = 0; i < Config.size; i++)
                for (int j = 0; j < Config.size; j++) {
                    if (map[i][j] != Color.NULL) {
                        count++;
                        if (map[i][j] == Color.WHITE)
                            whitePoint = new Point(i, j);
                    }
                }
            if (count != 2 || whitePoint == null) {
                return null;
            }
            if (map[mid][mid] != Color.BLACK) {
                return null;
            }
            if (whitePoint.getX() > mid + 1 || whitePoint.getX() < mid - 1) {
                return null;
            }
            if (whitePoint.getY() > mid + 1 || whitePoint.getY() < mid - 1) {
                return null;
            }
            List<Point> neighbor = new ArrayList<>();
            for (int i = mid - 2; i <= mid + 2; i++)
                for (int j = mid - 2; j <= mid + 2; j++) {
                    //去除通用劣势点
                    if (i == mid + 2 && j == mid + 2)
                        continue;
                    if (i == mid + 2 && j == mid - 2)
                        continue;
                    if (i == mid - 2 && j == mid + 2)
                        continue;
                    if (i == mid - 2 && j == mid - 2)
                        continue;
                    if (map[i][j] == Color.NULL) {
                        neighbor.add(new Point(i, j));
                    }
                }

            //去除斜指劣势点
            if (whitePoint.getX() != mid && whitePoint.getY() != mid) {
                int x = mid + (mid - whitePoint.getX());
                int y = mid + (mid - whitePoint.getY());
                remove(neighbor, x, y);
            }

            //去除直指劣势点
            if (whitePoint.getX() == mid) {
                int y1 = mid + (mid - whitePoint.getY());
                remove(neighbor, mid - 2, y1);
                remove(neighbor, mid - 1, y1);
                remove(neighbor, mid + 1, y1);
                remove(neighbor, mid + 2, y1);

                int y2 = whitePoint.getY();
                remove(neighbor, mid - 2, y2);
                remove(neighbor, mid + 2, y2);

                int y3 = whitePoint.getY() == mid - 1 ? mid + 2 : mid - 2;
                remove(neighbor, mid - 1, y3);
                remove(neighbor, mid + 1, y3);
            }

            if (whitePoint.getY() == mid) {
                int x1 = mid + (mid - whitePoint.getX());
                remove(neighbor, x1, mid - 2);
                remove(neighbor, x1, mid - 1);
                remove(neighbor, x1, mid + 1);
                remove(neighbor, x1, mid + 2);

                int x2 = whitePoint.getX();
                remove(neighbor, x2, mid - 2);
                remove(neighbor, x2, mid + 2);

                int x3 = whitePoint.getX() == mid - 1 ? mid + 2 : mid - 2;
                remove(neighbor, x3, mid - 1);
                remove(neighbor, x3, mid + 1);
            }

            return neighbor.get(new Random().nextInt(neighbor.size()));
        }
        if (aiColor == Color.WHITE) {
            int count = 0;
            Point blackPoint = null;
            for (int i = 0; i < Config.size; i++)
                for (int j = 0; j < Config.size; j++) {
                    if (map[i][j] != Color.NULL) {
                        count++;
                        if (map[i][j] == Color.BLACK)
                            blackPoint = new Point(i, j);
                    }
                }
            if (count != 1 || blackPoint == null) {
                return null;
            }
            List<Point> resultList = new ArrayList<>();
            for (int i = blackPoint.getX() - 1; i <= blackPoint.getX() + 1; i++)
                for (int j = blackPoint.getY() - 1; j <= blackPoint.getY() + 1; j++) {
                    if (GameMap.reachable(i, j)) {
                        if (map[i][j] == Color.NULL) {
                            resultList.add(new Point(i, j));
                        }
                    }
                }
            if (resultList.isEmpty()) {
                return null;
            }
            return resultList.get(new Random().nextInt(resultList.size()));
        }
        return null;
    }


    private static void remove(List<Point> list, int x, int y) {
        Point result = null;
        for (Point point : list) {
            if (point.getX() == x && point.getY() == y)
                result = new Point(x, y);
        }
        if (result != null)
            list.remove(result);
    }

    static public void main(String[] args) {
        Color[][] map = MapDriver.readMap("cases/beginning2.txt");
        Point point = getBeginningRandomPoint(map, Color.WHITE);
        assert map != null;
        assert point != null;

        map[point.getX()][point.getY()] = Color.WHITE;
        ConsolePrinter.printMap(new GameMap(map));
    }
}
