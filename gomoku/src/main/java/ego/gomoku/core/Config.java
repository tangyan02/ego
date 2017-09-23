package ego.gomoku.core;

public class Config {

    public final static int size = 15;

    public int searchDeep = 6;

    public int comboDeep = 7;

    public int cacheSize = 50000;

    public long searchTimeOut = Long.MAX_VALUE;

//    public long comboTimeOut = Long.MAX_VALUE;

    public static boolean debug = false;

    static int nodeLimit = 40;

    long startTime;

}
