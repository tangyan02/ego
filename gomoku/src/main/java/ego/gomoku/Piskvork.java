package ego.gomoku;

import ego.gomoku.core.Config;
import ego.gomoku.core.Result;
import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;
import ego.gomoku.enumeration.Level;
import ego.gomoku.helper.MapDriver;
import ego.gomoku.player.GomokuPlayer;

import java.io.IOException;
import java.util.Arrays;

/**
 * 适配Piskvork的主类
 * 协议地址：http://petr.lastovicka.sweb.cz/protocl2en.htm
 */
public class Piskvork {

    public static void main(String[] args) throws InterruptedException, IOException {
        try {
            while (true) {
                //获取命令和参数
                byte buffer[] = new byte[1024];
                int count = System.in.read(buffer);
                StringBuilder commandBuilder = new StringBuilder();
                StringBuilder paramBuilder = new StringBuilder();
                int mid = 0;
                for (int i = 0; i < count; i++) {
                    if (buffer[i] <= 'Z' && buffer[i] >= 'A') {
                        commandBuilder.append((char) buffer[i]);
                    } else {
                        mid = i;
                        break;
                    }
                }
                for (int i = mid + 1; i < count; i++) {
                    if (buffer[i] > 0 && buffer[i] != '\n') {
                        paramBuilder.append((char) buffer[i]);
                    }
                }

                //选择命令
                String command = commandBuilder.toString();
                String param = paramBuilder.toString();
                if (command.equals("START")) {
                    commandStart(param);
                }
                if (command.equals("BEGIN")) {
                    commandBegin(param);
                }
                if (command.equals("BOARD")) {
                    commandBoard(param);
                }
                if (command.equals("ABOUT")) {
                    commandAbout();
                }
                if (command.equals("END")) {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR io error");
        } catch (Exception e) {
            System.out.println("ERROR unknown error " + e.toString() + " " + Arrays.toString(e.getStackTrace()));
        }
        byte buffer[] = new byte[1024];
        System.in.read(buffer);
    }

    private static void commandStart(String param) {
        int size = convertStringToInt(param);
        if (size >= 6) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR unsupported");
        }
        Config.size = size;
    }

    private static void commandBegin(String param) {
        Color[][] map = MapDriver.getEmptyMap();
        GomokuPlayer gomokuPlayer = new GomokuPlayer(map, Level.VERY_HIGH);
        Point point = gomokuPlayer.play(Color.BLACK).getPoint();
        printPoint(point);
    }

    private static void commandBoard(String board) throws IOException {
        Color[][] map = MapDriver.getEmptyMap();
        //协议只支持我方和对方，这里假定我方是黑棋
        Color aiColor = Color.BLACK;
        while (true) {
            byte buffer[] = new byte[1024];
            int count = System.in.read(buffer);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                if (buffer[i] > 0 && buffer[i] != '\n') {
                    builder.append((char) buffer[i]);
                }
            }
            String input = builder.toString().trim();
            if (input.equals("DONE")) {
                break;
            }

            String[] numbers = input.split(",");
            int x = convertStringToInt(numbers[0]);
            int y = convertStringToInt(numbers[1]);
            int colorCode = convertStringToInt(numbers[2]);
            //协议的1是我方，这里假定我方是黑棋
            Color color = colorCode == 1 ? Color.BLACK : Color.WHITE;
            map[x][y] = color;
        }

        GomokuPlayer player = new GomokuPlayer(map, Level.VERY_HIGH);
        Result result = player.play(aiColor);
        printPoint(result.getPoint());
    }

    private static void commandAbout() {
        System.out.println(" name=\"EGO\", version=\"4.5.1\", author=\"Tang Yan\", country=\"CHN\"");
    }

    private static int convertStringToInt(String value) {
        value = value.trim();
        int result = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c <= '9' && c >= '0') {
                result *= 10;
            }
            result += value.charAt(i) - '0';
        }
        return result;
    }

    private static void printPoint(Point point) {
        System.out.printf("%s, %s\n", point.getX(), point.getX());
    }
}
