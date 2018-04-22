package ego.chineseChess.core;

import ego.chineseChess.entity.Move;

import java.util.HashMap;
import java.util.Map;

public class ScoreCache {

    private Map<String, Move> cache = new HashMap<>();

    public void record(GameMap gameMap, int level, int alpha, int beta, Move move) {
        String key = gameMap.getCode() + level + alpha + beta;
        cache.put(key, move);
    }

    public Move get(GameMap gameMap, int level, int alpha, int beta) {
        String key = gameMap.getCode() + level + alpha + beta;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        return null;
    }

}
