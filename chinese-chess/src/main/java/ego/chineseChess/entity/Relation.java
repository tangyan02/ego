package ego.chineseChess.entity;

public enum Relation {

    SELF, OPPONENT;

    public Relation getOther() {
        if (this == SELF)
            return OPPONENT;
        if (this == OPPONENT)
            return SELF;
        return null;
    }

}
