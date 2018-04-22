package ego.chineseChess.entity;

public enum Troop {

    CHE(1000, 'c'),
    MA(500, 'm'),
    XIANG(300, 'x'),
    SHI(300, 's'),
    JIANG(100000, 'j'),
    PAO(500, 'p'),
    BING(100, 'b');

    private int value;

    private char letter;

    Troop(int value, char letter) {
        this.value = value;
        this.letter = letter;
    }

    public int getValue() {
        return value;
    }

    public char getLetter() {
        return letter;
    }
}
