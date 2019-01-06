package ego.gomoku.core;

import java.util.LinkedHashMap;
import java.util.Map;

class Cache {

    Config config;

    GameMap gameMap;

    private QueueMap<Long, Boolean> cacheCombo = new QueueMap<>();


    Cache(Config config, GameMap gameMap) {
        this.config = config;
        this.gameMap = gameMap;
    }

    void recordComboResult(boolean value) {
        if (Config.cacheSize > 0) {
            cacheCombo.put(gameMap.getHashCode(), value);
        }
    }

    Boolean getComboResult() {
        if (Config.cacheSize > 0) {
            if (cacheCombo.containsKey(gameMap.getHashCode())) {
                return cacheCombo.get(gameMap.getHashCode());
            }
        }
        return null;
    }

    public void clear(){
        cacheCombo.clear();
    }

    class QueueMap<K, V> extends LinkedHashMap<K, V> {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > Config.cacheSize;
        }
    }
}
