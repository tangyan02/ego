package ego.sudoku.helper;

import ego.sudoku.core.Config;

import java.util.Date;

public class ConsolePrinter {

    private long debugTime = new Date().getTime();

    public static void printMap(Integer[][] map) {
        for (int i = 1; i < Config.size; i++) {
            for (int j = 1; j < Config.size; j++) {
                if (map[i][j] != null) {
                    System.out.print(map[i][j]);
                }else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
