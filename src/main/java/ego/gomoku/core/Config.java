package ego.gomoku.core;

public class Config {

    public static int size = 15;

    public int searchDeep = 6;

    public int comboDeep = 7;

    public static int cacheSize = 30000;

    public long searchTimeOut = 10 * 1000;

    public long comboTimeOut = 10 * 1000;

    public static boolean debug = false;

    static int nodeLimit = 99;

}
