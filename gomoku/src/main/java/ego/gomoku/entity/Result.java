package ego.gomoku.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Result {

    Point point;

    List<Point> pointList = new ArrayList<>();

    int maxValue = Integer.MIN_VALUE;

    int comboLevel;

    int searchLevel;

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

    public int getComboLevel() {
        return comboLevel;
    }

    public void setComboLevel(int comboLevel) {
        this.comboLevel = comboLevel;
    }

    public int getSearchLevel() {
        return searchLevel;
    }

    public void setSearchLevel(int searchLevel) {
        this.searchLevel = searchLevel;
    }
}
