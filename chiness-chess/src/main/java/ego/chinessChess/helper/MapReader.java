package ego.chinessChess.helper;

import ego.chinessChess.core.Config;
import ego.chinessChess.entity.Relation;
import ego.chinessChess.entity.Troop;
import ego.chinessChess.entity.Unit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapReader {

    static List<Unit> readUnits(String path) throws IOException {
        File file = new File(path);
        List<Unit> units = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        for (int i = 0; i < Config.HEIGHT; i++) {
            String line = bufferedReader.readLine();
            for (int j = 0; j < Config.WIDTH; j++) {
                char letter = line.charAt(j);
                if (letter == '.') {
                    continue;
                }
                Unit unit = new Unit();
                unit.x = i;
                unit.y = j;
                unit.relation = Character.isUpperCase(letter) ? Relation.SELF : Relation.OPPONENT;
                switch (Character.toUpperCase(letter)) {
                    case 'C':
                        unit.troop = Troop.CHE;
                        break;
                    case 'M':
                        unit.troop = Troop.MA;
                        break;
                    case 'X':
                        unit.troop = Troop.XIANG;
                        break;
                    case 'S':
                        unit.troop = Troop.SHI;
                        break;
                    case 'J':
                        unit.troop = Troop.JIANG;
                        break;
                    case 'P':
                        unit.troop = Troop.PAO;
                        break;
                    case 'B':
                        unit.troop = Troop.BING;
                        break;
                    default:
                        throw new IllegalArgumentException("troops not support");
                }
                units.add(unit);
            }
        }
        return units;
    }

}
