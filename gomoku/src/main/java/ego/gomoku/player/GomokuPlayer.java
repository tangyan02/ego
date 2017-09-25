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
            config.comboDeep = 100;
            config.searchDeep = 100;
            config.searchTimeOut = 20 * 1000;
            config.comboTimeOut = 10 * 1000;
        }
        game.init(map, config);
    }

    public void setCacheSize(int value) {
        config.cacheSize = value;
    }

    public Result play(Color color) {
        Result result = game.search(color, false);
        return result;
    }

    public Result randomBegin(Color color) {
        Result result = game.search(color, true);
        return result;
    }

}
