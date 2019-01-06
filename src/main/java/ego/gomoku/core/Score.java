package ego.gomoku.core;

import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;
import ego.gomoku.helper.ConsolePrinter;
import ego.gomoku.helper.MapDriver;

class Score {

    private static int directX[] = {0, 1, 1, 1};
    private static int directY[] = {1, 1, 0, -1};

    //使用动态规划
    //定义：    f[i][j][k] 表示以i,j为终点，在方向k上的统计
    //转移方程: f[i][j][k] = a[i][j][k] + f[i-dx[k]][j-dy[k]][k]
    private int[][][] blackCount = new int[Config.size][Config.size][4];
    private int[][][] whiteCount = new int[Config.size][Config.size][4];

    private int value = 0;

    void init(GameMap gameMap, Color aiColor) {
        value = 0;
        blackCount = new int[Config.size][Config.size][4];
        whiteCount = new int[Config.size][Config.size][4];
        for (int i = 0; i < Config.size; i++) {
            for (int j = 0; j < Config.size; j++) {
                Point point = new Point(i, j);
                Color color = gameMap.getColor(point);
                if (color != Color.NULL) {
                    setColor(point, color, Color.NULL, aiColor);
                }
            }
        }
    }

    int getMapScore() {
        return value;
    }

    void setColor(Point point, Color color, Color forwardColor, Color aiColor) {
        for (int i = 0; i < 4; i++) {
            int x = point.getX() - directX[i];
            int y = point.getY() - directY[i];
            for (int k = 0; k < 5; k++) {
                x += directX[i];
                y += directY[i];
                int headX = x - directX[i] * 4;
                int headY = y - directY[i] * 4;
                if (!GameMap.reachable(headX, headY)) {
                    continue;
                }
                if (!GameMap.reachable(x, y)) {
                    continue;
                }
                if (forwardColor == Color.NULL) {
                    value -= getValueByCount(blackCount[x][y][i], whiteCount[x][y][i], aiColor);
                    if (color == Color.BLACK) {
                        blackCount[x][y][i]++;
                    }
                    if (color == Color.WHITE) {
                        whiteCount[x][y][i]++;
                    }
                    value += getValueByCount(blackCount[x][y][i], whiteCount[x][y][i], aiColor);
                }
                if (forwardColor != Color.NULL) {
                    value -= getValueByCount(blackCount[x][y][i], whiteCount[x][y][i], aiColor);
                    if (color == Color.NULL) {
                        if (forwardColor == Color.BLACK) {
                            blackCount[x][y][i]--;
                        }
                        if (forwardColor == Color.WHITE) {
                            whiteCount[x][y][i]--;
                        }
                    }
                    value += getValueByCount(blackCount[x][y][i], whiteCount[x][y][i], aiColor);
                }
            }
        }
    }

    private int getValueByCount(int blackCount, int whiteCount, Color color) {
        int ONE = 4;
        int TWO = 20;
        int THREE = 40;
        int FOUR = 90;
        float weight = 1.0f;

        int valueWhite = 0;
        if (blackCount == 0) {
            if (whiteCount == 1)
                valueWhite += ONE;
            if (whiteCount == 2)
                valueWhite += TWO;
            if (whiteCount == 3)
                valueWhite += THREE;
            if (whiteCount == 4)
                valueWhite += FOUR;
        }

        int valueBlack = 0;
        if (whiteCount == 0) {
            if (blackCount == 1)
                valueBlack += ONE;
            if (blackCount == 2)
                valueBlack += TWO;
            if (blackCount == 3)
                valueBlack += THREE;
            if (blackCount == 4)
                valueBlack += FOUR;
        }

        int value = 0;
        if (color == Color.BLACK) {
            return valueBlack - (int) (valueWhite * weight);
        }
        if (color == Color.WHITE) {
            return valueWhite - (int) (valueBlack * weight);
        }
        return value;
    }

    int[][][] getColorCount(Color color) {
        if (color == Color.BLACK) {
            return blackCount;
        }
        if (color == Color.WHITE) {
            return whiteCount;
        }
        return null;
    }

    public static void main(String[] args) {
        Color[][] map = MapDriver.readMap("cases/fourTop.txt");
        GameMap gameMap = new GameMap(map);
        ConsolePrinter.printMap(gameMap);
        Score score = new Score();
        score.init(gameMap, Color.BLACK);
        System.out.println(score.getMapScore());
        for (int k = 0; k < 4; k++) {
            for (int i = 0; i < Config.size; i++) {
                for (int j = 0; j < Config.size; j++) {
                    System.out.print(score.whiteCount[i][j][k]);
                }
                System.out.println();
            }
            System.out.println();
        }

        map = MapDriver.readMap("cases/fourButton.txt");
        gameMap = new GameMap(map);
        ConsolePrinter.printMap(gameMap);
        score = new Score();
        score.init(gameMap, Color.BLACK);
        System.out.println(score.getMapScore());
        for (int k = 0; k < 4; k++) {
            for (int i = 0; i < Config.size; i++) {
                for (int j = 0; j < Config.size; j++) {
                    System.out.print(score.whiteCount[i][j][k]);
                }
                System.out.println();
            }
            System.out.println();
        }

    }
}
