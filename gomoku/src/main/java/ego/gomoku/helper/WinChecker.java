package ego.gomoku.helper;

import ego.gomoku.core.Config;
import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;

public class WinChecker {

    private static int directX[] = {0, 1, 1, 1, 0, -1, -1, -1};
    private static int directY[] = {1, 1, 0, -1, -1, -1, 0, 1};

    public static Color win(Color[][] map) {
        for (int i = 0; i < Config.size; i++)
            for (int j = 0; j < Config.size; j++) {
                Point point = new Point(i, j);
                Color color = map[i][j];
                if (map[i][j] != Color.NULL) {
                    for (int direct = 0; direct < 4; direct++) {
                        if (checkColors(color, point, direct, 0, 4, map)) {
                            return color;
                        }
                    }
                }
            }
        return null;
    }

    public static boolean checkColors(Color color, Point point, int direct, int start, int end, Color[][] map) {
        int x = point.getX() + start * (directX[direct]);
        int y = point.getY() + start * (directY[direct]);
        for (int i = start; i <= end; i++) {
            if (!reachable(x, y)) {
                return false;
            }
            if (map[x][y] != color) {
                return false;
            }
            x += directX[direct];
            y += directY[direct];
        }
        return true;
    }

    private static boolean reachable(int x, int y) {
        if (x < 0 || x >= Config.size)
            return false;
        if (y < 0 || y >= Config.size)
            return false;
        return true;
    }

}
