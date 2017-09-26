package ego.gomoku.player;

import ego.gomoku.core.Config;
import ego.gomoku.core.Game;
import ego.gomoku.core.Result;
import ego.gomoku.enumeration.Color;
import ego.gomoku.enumeration.Level;

public class GomokuPlayer {

    Game game;

    Config config = new Config();

    public GomokuPlayer(Color[][] map, Level level) {
        game = new Game();
        if (level == Level.EASY) {
            config.comboDeep = 0;
            config.searchDeep = 4;
        }
        if (level == Level.NORMAL) {
            config.comboDeep = 0;
            config.searchDeep = 6;
        }
        if (level == Level.HIGH) {
            config.comboDeep = 15;
            config.searchDeep = 6;
            config.searchTimeOut = 10 * 1000;
            config.comboTimeOut = 5 * 1000;
        }
        if (level == Level.VERY_HIGH) {
            config.comboDeep = 50;
            config.searchDeep = 10;
            config.searchTimeOut = 20 * 1000;
            config.comboTimeOut = 10 * 1000;
        }
        game.init(map, config);
    }

    public void setCacheSize(int value) {
        Config.cacheSize = value;
    }

    public Result play(Color color) {
        Result result = game.search(color, false);
        return result;
    }

    public Result randomBegin(Color color) {
        Result result = game.search(color, true);
        return result;
    }

    public Result playGomokuCup(Color color, long time) {
        config.comboTimeOut = time / 3;
        config.searchTimeOut = time / 3 * 2;
        Result result = game.search(color, false);
        return result;
    }

    public long getThinkTime(long matchTimeLeft, long moveTimeLimit, int pointsCount) {
        long time = matchTimeLeft / 10;
        if (pointsCount < 40) {
            long maxTime = matchTimeLeft / (40 - pointsCount);
            time = Math.min(maxTime, time);
        }
        time = Math.min(moveTimeLimit, time);
        time -= 100;
        return time;
    }

}
