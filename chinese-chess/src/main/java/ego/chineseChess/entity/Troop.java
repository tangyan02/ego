package ego.chineseChess.entity;

public enum Troop {

    CHE(1000),
    MA(500),
    XIANG(300),
    SHI(300),
    JIANG(10000),
    PAO(500),
    BING(1000);

    private int value;

    Troop(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
