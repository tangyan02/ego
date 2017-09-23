package ego.sudoku;

import ego.sudoku.exception.NoAnswerException;
import ego.sudoku.exception.ValidFailException;
import ego.sudoku.helper.ConsolePrinter;
import ego.sudoku.helper.InputDriver;
import ego.sudoku.player.SudokuPlayer;

public class Main {

    public static void main(String[] args) {
        Integer[][] map = InputDriver.readMap();
        SudokuPlayer player = new SudokuPlayer(map);
        Integer[][] result;
        try {
            result = player.play();
            ConsolePrinter.printMap(result);
        } catch (NoAnswerException e) {
            System.out.println("没有答案");
        } catch (ValidFailException e) {
            System.out.println("输入不合法");
        }
    }
}
