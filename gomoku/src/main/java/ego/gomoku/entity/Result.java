package ego.gomoku.entity;

import ego.gomoku.entity.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Result {

    Point point;

    List<Point> pointList = new ArrayList<>();

    int maxValue = Integer.MIN_VALUE;

    public void add(Point point, int value) {
        if (value > maxValue) {
            maxValue = value;
            pointList.clear();
        }
        pointList.add(point);
        this.point = pointList.get(new Random().nextInt(pointList.size()));
    }

    void reset() {
        maxValue = Integer.MIN_VALUE;
        point = null;
        pointList.clear();
    }

    public Point getPoint() {
        return point;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public List<Point> getPointList() {
        return pointList;
    }
}
