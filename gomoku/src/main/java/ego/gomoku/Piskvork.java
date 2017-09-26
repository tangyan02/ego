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

    static Color[][] map = MapDriver.getEmptyMap();

    static int moveLimit = 30 * 1000;

    static int matchLimit = 1000 * 1000;

    static int pointsCount = 0;

    public static void main(String[] args) throws InterruptedException, IOException {
        sendMessageToPiskvork("started :)");
        try {
            while (true) {
                //获取命令和参数
                byte buffer[] = new byte[1024];
                int count = System.in.read(buffer);
                StringBuilder commandBuilder = new StringBuilder();
                StringBuilder paramBuilder = new StringBuilder();
                boolean commandFinish = false;
                for (int i = 0; i < count; i++) {
                    if (!commandFinish) {
                        if (buffer[i] <= 'Z' && buffer[i] >= 'A') {
                            commandBuilder.append((char) buffer[i]);
                        } else {
                            commandFinish = true;
                        }
                    } else {
                        if (buffer[i] > 0 && buffer[i] != '\n' && buffer[i] != '\r') {
                            paramBuilder.append((char) buffer[i]);
                        }
                        if (buffer[i] == '\n') {
                            //选择命令
                            String command = commandBuilder.toString();
                            String param = paramBuilder.toString();
                            doCommand(command, param);
                            commandBuilder = new StringBuilder();
                            paramBuilder = new StringBuilder();
                            commandFinish = false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            sendErrorToPiskvork("io error");
        } catch (Exception e) {
            sendErrorToPiskvork("unknown error " + e.toString() + " " + Arrays.toString(e.getStackTrace()));
        }
        byte buffer[] = new byte[1024];
        System.in.read(buffer);
    }

    private static void doCommand(String command, String param) throws IOException {
        sendDebugToPiskvork("receive<-" + command + " " + param);
        if (command.equals("START")) {
            commandStart(param);
        }
        if (command.equals("RESTART")) {
            commandRestart(param);
        }
        if (command.equals("BEGIN")) {
            commandBegin();
        }
        if (command.equals("TURN")) {
            commandTurn(param);
        }
        if (command.equals("BOARD")) {
            commandBoard();
        }
        if (command.equals("ABOUT")) {
            commandAbout();
        }
        if (command.equals("END")) {
            System.exit(0);
        }
        if (command.equals("INFO")) {
            commandInfo(param);
        }
    }

    private static void commandInfo(String param) {
        String[] inputs = param.split(" ");
        String key = inputs[0];
        if (key.equals("timeout_turn")) {
            int value = convertStringToInt(inputs[1]);
            moveLimit = value;
        }
        if (key.equals("time_left")) {
            int value = convertStringToInt(inputs[1]);
            matchLimit = value;
        }
        if (key.equals("timeout_match")) {
            int value = convertStringToInt(inputs[1]);
            matchLimit = value;
        }
    }

    private static void commandStart(String param) {
        int size = convertStringToInt(param);
        if (size >= 6) {
            sendCommandToPiskvork("OK");
        } else {
            sendErrorToPiskvork("unsupported");
        }
        Config.size = size;
    }

    private static void commandRestart(String param) {
        pointsCount = 0;
        sendCommandToPiskvork("OK");
    }

    private static void commandTurn(String param) throws IOException {
        String[] numbers = param.split(",");
        int x = convertStringToInt(numbers[0]);
        int y = convertStringToInt(numbers[1]);
        setPoint(new Point(x, y), Color.WHITE);
        GomokuPlayer player = new GomokuPlayer(map, Level.VERY_HIGH);
        long time = player.getThinkTime(matchLimit, moveLimit, pointsCount);
        sendDebugToPiskvork("think time " + time);
        Result result = player.playGomokuCup(Color.BLACK, time);
        setPoint(result.getPoint(), Color.BLACK);
        printPoint(result.getPoint());
    }

    private static void commandBegin() {
        pointsCount = 0;
        map = MapDriver.getEmptyMap();
        GomokuPlayer gomokuPlayer = new GomokuPlayer(map, Level.VERY_HIGH);
        Point point = gomokuPlayer.play(Color.BLACK).getPoint();
        setPoint(point, Color.BLACK);
        printPoint(point);
    }

    private static void commandBoard() throws IOException {
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
        setPoint(result.getPoint(), Color.BLACK);
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
        sendCommandToPiskvork(String.format("%s,%s", point.getX(), point.getY()));
    }

    private static void setPoint(Point point, Color color) {
        pointsCount++;
        map[point.getX()][point.getY()] = color;
    }

    private static void sendCommandToPiskvork(String message) {
        sendDebugToPiskvork("send->" + message);
        System.out.println(message);
    }

    private static void sendMessageToPiskvork(String message) {
        System.out.println("Message ego: " + message);
    }

    private static void sendDebugToPiskvork(String message) {
        System.out.println("Debug ego: " + message);
    }

    private static void sendErrorToPiskvork(String message) {
        System.out.println("Error ego: " + message);
    }
}
