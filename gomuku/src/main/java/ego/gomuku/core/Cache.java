package ego.gomuku.core;

import ego.gomuku.entity.Counter;

import java.util.LinkedHashMap;
import java.util.Map;

class Cache {

    Config config;

    GameMap gameMap;

    Counter counter;

    private QueueMap<Long, Boolean> cacheCombo = new QueueMap<>();


    Cache(Config config, GameMap gameMap, Counter counter) {
        this.config = config;
        this.gameMap = gameMap;
        this.counter = counter;
    }

    void recordComboResult(boolean value) {
        if (config.cacheSize > 0) {
            cacheCombo.put(gameMap.getHashCode(), value);
        }
    }

    Boolean getComboResult() {
        if (config.cacheSize > 0) {
            if (cacheCombo.containsKey(gameMap.getHashCode())) {
                counter.comboCacheHit++;
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
            return size() > config.cacheSize;
        }
    }
}
