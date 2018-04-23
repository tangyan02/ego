package util;

import ego.chineseChess.core.GameMap;
import ego.chineseChess.entity.Unit;
import ego.chineseChess.helper.MapDriver;

import java.io.IOException;
import java.util.List;

public class MapProvider {

    public static GameMap getGameMap() {
        String path = "src/test/resources/input.txt";
        List<Unit> units = null;
        try {
            units = MapDriver.readUnits(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new GameMap(units);
    }

    public static GameMap getGameMap(String fileName) {
        String path = "src/test/resources/" + fileName;
        List<Unit> units = null;
        try {
            units = MapDriver.readUnits(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new GameMap(units);
    }

}
