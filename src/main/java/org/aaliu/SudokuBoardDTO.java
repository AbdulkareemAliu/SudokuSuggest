package org.aaliu;

import java.util.ArrayList;

public class SudokuBoardDTO {
    private SudokuCell[][] inputBoard;

    public ArrayList<ArrayList<String>> getInputBoard() {
        ArrayList<ArrayList<String>> board = new ArrayList<ArrayList<String>>();

        for (SudokuCell[] row : inputBoard) {
            ArrayList<String> curRow = new ArrayList<String>();
            for (SudokuCell item : row){
                curRow.add(item.getValue());
            }
            board.add(curRow);
        }

        return board;
    }

    public void setInputBoard(SudokuCell[][] inputBoard) {
        this.inputBoard = inputBoard;
    }
}

