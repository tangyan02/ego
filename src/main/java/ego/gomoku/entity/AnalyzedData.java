package ego.gomoku.entity;

import java.util.HashSet;
import java.util.Set;

public class AnalyzedData {

    private Set<Point> fiveAttack = new HashSet<>();

    private Set<Point> fourAttack = new HashSet<>();

    private Set<Point> threeOpenAttack = new HashSet<>();

    private Set<Point> fourDefence = new HashSet<>();

    private Set<Point> threeDefence = new HashSet<>();

    private Set<Point> origin = new HashSet<>();

    private Set<Point> notKey = new HashSet<>();

    public Set<Point> getFiveAttack() {
        return fiveAttack;
    }

    public void setFiveAttack(Set<Point> fiveAttack) {
        this.fiveAttack = fiveAttack;
    }

    public Set<Point> getFourAttack() {
        return fourAttack;
    }

    public void setFourAttack(Set<Point> fourAttack) {
        this.fourAttack = fourAttack;
    }

    public Set<Point> getThreeOpenAttack() {
        return threeOpenAttack;
    }

    public void setThreeOpenAttack(Set<Point> threeOpenAttack) {
        this.threeOpenAttack = threeOpenAttack;
    }

    public Set<Point> getFourDefence() {
        return fourDefence;
    }

    public void setFourDefence(Set<Point> fourDefence) {
        this.fourDefence = fourDefence;
    }

    public Set<Point> getThreeDefence() {
        return threeDefence;
    }

    public void setThreeDefence(Set<Point> threeDefence) {
        this.threeDefence = threeDefence;
    }

    public Set<Point> getOrigin() {
        return origin;
    }

    public void setOrigin(Set<Point> origin) {
        this.origin = origin;
    }

    public Set<Point> getNotKey() {
        return notKey;
    }

    public void setNotKey(Set<Point> notKey) {
        this.notKey = notKey;
    }
}
