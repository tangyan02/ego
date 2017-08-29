package core;

import entity.Point;

import java.util.ArrayList;
import java.util.List;

class LevelProcessor {

    static List<Point> getExpandPoints(Analyzer data) {
        List<Point> result = selectSet(data);

        if (result.isEmpty()) {
            result.add(new Point(7, 7));
            return result;
        }

        return result;
    }

    private static List<Point> selectSet(Analyzer data) {
        //如果能连5，则连5
        if (!data.getFiveAttack().isEmpty()) {
            return new ArrayList<>(data.getFiveAttack());
        }
        //如果有对方冲4，则防冲4
        if (!data.getFourDefence().isEmpty()) {
            return new ArrayList<>(data.getFourDefence());
        }
        //如果有对方活3，则防活3或者冲四
        if (!data.getThreeDefence().isEmpty()) {
            return new ArrayList<Point>(data.getFourAttack()) {{
                addAll(data.getThreeDefence());
            }};
        }
        List<Point> result = new ArrayList<>();
        result.addAll(data.getFourAttack());
        result.addAll(data.getThreeOpenAttack());
        result.addAll(data.getTwoAttack());
        result.addAll(data.getNotKey());
        return result;
    }
}
