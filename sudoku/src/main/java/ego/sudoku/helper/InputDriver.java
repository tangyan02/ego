package ego.sudoku.helper;

import ego.sudoku.core.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InputDriver {

    private static String resourcePath = "sudoku/src/main/resources/";

    public static Integer[][] readMap() {
        return readMap("input.txt");
    }

    public static Integer[][] readMap(String filePath) {
        File file = new File(resourcePath + filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            int size = Config.size;
            Integer[][] map = new Integer[size][size];
            for (int i = 1; i < size; i++) {
                String tempString = "#" + reader.readLine();
                for (int j = 1; j < size; j++) {
                    switch (tempString.charAt(j)) {
                        case '.':
                            map[i][j] = null;
                            break;
                        default:
                            map[i][j] = (int) tempString.charAt(j) - '0';
                    }
                }
            }
            reader.close();
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }
}
