package ego.gomoku.enumeration;

public enum Color {
    BLACK, WHITE, NULL;

    public Color getOtherColor() {
        if (this == NULL) {
            return null;
        }
        return this == BLACK ? WHITE : BLACK;
    }
}
