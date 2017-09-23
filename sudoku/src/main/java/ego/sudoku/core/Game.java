package ego.sudoku.core;

import ego.sudoku.exception.NoAnswerException;
import ego.sudoku.exception.ValidFailException;

public class Game {

    private Integer[][] result = null;
    private boolean[][] row = new boolean[Config.size][Config.size];
    private boolean[][] column = new boolean[Config.size][Config.size];
    private boolean[][] block = new boolean[Config.size][Config.size];

    private boolean isInit = false;
    private boolean finish = false;

    public void init(Integer[][] map) throws ValidFailException {
        this.result = map;
        for (int i = 1; i < Config.size; i++)
            for (int j = 1; j < Config.size; j++) {
                if (result[i][j] != null) {
                    int code = getBlockCode(i, j);
                    int value = result[i][j];
                    if (row[i][value] || column[j][value] || block[code][value]) {
                        throw new ValidFailException();
                    }
                    row[i][value] = true;
                    column[j][value] = true;
                    block[getBlockCode(i, j)][value] = true;
                }
            }
        isInit = true;
    }

    public Integer[][] execute() throws NoAnswerException {
        if (!isInit) {
            throw new RuntimeException("未初始化");
        }

        dfs(1, 1);
        System.out.println(finish);
        if (!finish)
            throw new NoAnswerException();
        return result;
    }

    private void dfs(int x, int y) {
        if (finish) {
            return;
        }
        if (x == 10) {
            finish = true;
            return;
        }
        if (y == 10) {
            dfs(x + 1, 1);
            return;
        }
        if (result[x][y] != null) {
            dfs(x, y + 1);
            return;
        }
        int code = getBlockCode(x, y);
        for (int i = 1; i <= 9; i++) {
            if (row[x][i] || column[y][i] || block[code][i]) {
                continue;
            }
            result[x][y] = i;
            row[x][i] = true;
            column[y][i] = true;
            block[code][i] = true;
            dfs(x, y + 1);
            if (finish) {
                return;
            }
            result[x][y] = null;
            row[x][i] = false;
            column[y][i] = false;
            block[code][i] = false;
        }
    }

    private static int getBlockCode(int x, int y) {
        return ((x - 1) / 3) * 3 + (y - 1) / 3 + 1;
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 9; j++) {
                System.out.print(getBlockCode(i, j));
            }
            System.out.println();
        }
    }
}
