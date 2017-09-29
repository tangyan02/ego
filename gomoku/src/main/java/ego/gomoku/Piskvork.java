package ego.gomoku;

import ego.gomoku.core.Config;
import ego.gomoku.entity.Result;
import ego.gomoku.entity.Point;
import ego.gomoku.enumeration.Color;
import ego.gomoku.enumeration.Level;
import ego.gomoku.helper.MapDriver;
import ego.gomoku.player.GomokuPlayer;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * 适配Piskvork的主类
 * 协议地址：http://petr.lastovicka.sweb.cz/protocl2en.htm
 */
public class Piskvork {

    static Color[][] map = MapDriver.getEmptyMap();

    static int moveLimit = 30 * 1000;

    static int matchLimit = 1000 * 1000;

    static int pointsCount = 0;

    static LinkedList<String> messageBuffer = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        sendMessageToPiskvork("started :)");
        new Thread(Piskvork::receiveMessage).start();
        while (true) {
            parseMessage();
        }
    }

    private static void parseMessage() {
        if (messageBuffer.size() > 0) {
            String message = messageBuffer.getFirst();
            int blankIndex = message.length();
            for (int i = 0; i < message.length(); i++) {
                if (message.charAt(i) == ' ' || message.charAt(i) == '\r' || message.charAt(i) == '\n') {
                    blankIndex = i;
                    break;
                }
            }
            String command = message.substring(0, blankIndex);
            String param = message.substring(blankIndex, message.length()).trim();
            messageBuffer.removeFirst();
            doCommand(command, param);
        }
    }

    private static void receiveMessage() {
        try {
            while (true) {
                //获取命令和参数
                byte buffer[] = new byte[1024];
                int count = System.in.read(buffer);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    if (buffer[i] > 0 && buffer[i] != '\n' && buffer[i] != '\r') {
                        builder.append((char) buffer[i]);
                    }
                    if (buffer[i] == '\n') {
                        //存入消息
                        sendDebugToPiskvork("receive<-" + builder.toString());
                        messageBuffer.addLast(builder.toString());
                        builder = new StringBuilder();
                    }

                }
            }
        } catch (IOException e) {
            sendErrorToPiskvork("io error");
        } catch (Exception e) {
            sendErrorToPiskvork("unknown error " + e.toString() + " " + Arrays.toString(e.getStackTrace()));
        }
    }

    private static void doCommand(String command, String param) {
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
        if (key.equals("max_memory ")) {
            int value = convertStringToInt(inputs[1]);
            Config.cacheSize = value / 10000;
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
        map = MapDriver.getEmptyMap();
        sendCommandToPiskvork("OK");
    }

    private static void commandTurn(String param) {
        String[] numbers = param.split(",");
        int x = convertStringToInt(numbers[0]);
        int y = convertStringToInt(numbers[1]);
        setPoint(new Point(x, y), Color.WHITE);
        GomokuPlayer player = new GomokuPlayer(map, Level.VERY_HIGH);
        long time = player.getThinkTime(matchLimit, moveLimit, pointsCount);
        sendDebugToPiskvork("think time " + time);
        Result result = player.playGomokuCup(Color.BLACK, time);
        setPoint(result.getPoint(), Color.BLACK);
        printResult(result);
    }

    private static void commandBegin() {
        pointsCount = 0;
        map = MapDriver.getEmptyMap();
        GomokuPlayer gomokuPlayer = new GomokuPlayer(map, Level.VERY_HIGH);
        Result result = gomokuPlayer.play(Color.BLACK);
        setPoint(result.getPoint(), Color.BLACK);
        printResult(result);
    }

    private static void commandBoard() {
        //协议只支持我方和对方，这里假定我方是黑棋
        map = MapDriver.getEmptyMap();
        pointsCount = 0;
        Color aiColor = Color.BLACK;
        boolean done = false;
        while (!done) {
            while (messageBuffer.size() > 0) {
                String pointsInfo = messageBuffer.getFirst().trim();
                messageBuffer.removeFirst();
                if (pointsInfo.equals("DONE")) {
                    done = true;
                    break;
                }
                String[] numbers = pointsInfo.split(",");
                int x = convertStringToInt(numbers[0]);
                int y = convertStringToInt(numbers[1]);
                int colorCode = convertStringToInt(numbers[2]);
                sendDebugToPiskvork(String.format("get point %s,%s %s", x, y, colorCode));
                Color color = colorCode == 1 ? aiColor : aiColor.getOtherColor();
                setPoint(new Point(x, y), color);
            }
        }

        //输出加载的棋盘
        for (int i = 0; i < map.length; i++) {
            StringBuilder text = new StringBuilder();
            for (int j = 0; j < map.length; j++) {
                switch (map[i][j]) {
                    case NULL:
                        text.append(".");
                        break;
                    case BLACK:
                        text.append("x");
                        break;
                    case WHITE:
                        text.append("o");
                        break;
                }
            }
            sendDebugToPiskvork(text.toString());
        }

        GomokuPlayer player = new GomokuPlayer(map, Level.VERY_HIGH);
        long time = player.getThinkTime(matchLimit, moveLimit, pointsCount);
        sendDebugToPiskvork("think time " + time);
        Result result = player.playGomokuCup(aiColor, time);
        setPoint(result.getPoint(), aiColor);
        printResult(result);
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

    private static void printResult(Result result) {
        sendDebugToPiskvork(String.format("(%s,%s) depth:%s combo:%s value:%s",
                result.getPoint().getX(),
                result.getPoint().getY(),
                result.getSearchLevel(),
                result.getComboLevel(),
                result.getMaxValue()));
        sendCommandToPiskvork(String.format("%s,%s",
                result.getPoint().getX(),
                result.getPoint().getY()));
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
        System.out.println("Message " + message);
    }

    private static void sendDebugToPiskvork(String message) {
        System.out.println("Debug " + message);
    }

    private static void sendErrorToPiskvork(String message) {
        System.out.println("Error " + message);
    }
}
