package ego.sudoku.player;

import ego.sudoku.core.Game;
import ego.sudoku.exception.NoAnswerException;
import ego.sudoku.exception.ValidFailException;

public class SudokuPlayer {

    private Integer[][] map;

    private Game game = new Game();

    public SudokuPlayer(Integer[][] map) {
        this.map = map;
    }

    public Integer[][] play() throws NoAnswerException, ValidFailException {
        game.init(map);
        return game.execute();
    }

}
