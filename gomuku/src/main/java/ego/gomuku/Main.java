package ego.gomuku;

import ego.gomuku.core.Config;
import ego.gomuku.entity.CountData;
import ego.gomuku.entity.Point;
import ego.gomuku.enumeration.Color;
import ego.gomuku.enumeration.Level;
import ego.gomuku.player.GomukuPlayer;
import ego.gomuku.helper.MapDriver;
import ego.gomuku.helper.WinChecker;

public class Main {

    private static GomukuPlayer gomukuPlayer = null;

    private static Color[][] map = MapDriver.readMap();

    private static int progress = 0;

    private static int currentProgress = 0;

    private static Point result = null;

    private static boolean debug = true;

    private static boolean autoRun = false;

    private static boolean updateFile = true;

    private static Color aiColor = Color.BLACK;

    public static void main(String[] args) {
        System.out.println("正在初始化数据...");
        System.out.println("开始计算...");
        if (WinChecker.win(map) != null) {
            System.out.println(WinChecker.win(map) + " win");
            return;
        }
        Config.debug = debug;
        if (!debug) {
            listen();
        }
        GomukuPlayer gomukuPlayer = new GomukuPlayer(map, Level.HIGH);
        result = gomukuPlayer.play(aiColor).getPoint();
        System.out.println(result);
        map[result.getX()][result.getY()] = aiColor;
        if (updateFile) {
            MapDriver.printMap(map);
        }
        if (autoRun) {
            loop();
        }
    }

    private static void loop() {
        aiColor = aiColor.getOtherColor();
        main(null);
    }

    private static void listen() {
        new Thread(() -> {
            while (true) {
                CountData data = gomukuPlayer.getCountData();
                if (data.getAllStep() > 0 && progress == 0) {
                    progress = data.getAllStep();
                    for (int i = 0; i < progress; i++) {
                        System.out.print("=");
                    }
                    System.out.println();
                }
                if (data.getFinishStep() > currentProgress) {
                    for (int i = 0; i < data.getFinishStep() - currentProgress; i++) {
                        System.out.print(">");
                    }
                    currentProgress = data.getFinishStep();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (progress == currentProgress && progress > 0) {
                    System.out.println();
                    System.out.println(result.getX() + " " + result.getY());
                    return;
                }
            }
        }).start();
    }

}
