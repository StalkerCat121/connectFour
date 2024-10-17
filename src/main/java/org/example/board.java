package org.example;

import java.io.Serializable;

class Board implements Serializable {
    private static final int ROWS = 6;
    private static final int COLS = 7;

    private int[][] board;

    public Board() {
        board = new int[ROWS][COLS];
    }

    public boolean isColumnFull(int col) {
        return board[0][col] != 0;
    }

    public boolean placePiece(int col, int player) {
        if (isColumnFull(col)) {
            return false;
        }

        int row = ROWS - 1;
        while (board[row][col] != 0) {
            row--;
        }
        board[row][col] = player;
        return true;
    }

    public boolean checkLine(int row, int col, int deltaRow, int deltaCol, int player) {
        int count = 1;
        while (true) {
            row += deltaRow;
            col += deltaCol;
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                break;
            }
            if (board[row][col] != player) {
                break;
            }
            count++;
        }
        return count >= 4;
    }

    public boolean checkWin(int player) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == player && (
                        checkLine(row, col, 1, 0, player) || // left-right
                                checkLine(row, col, 0, 1, player) || // up-down
                                checkLine(row, col, 1, 1, player) || // diagonal up-right
                                checkLine(row, col, 1, -1, player))) { // diagonal up-left
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFull() {
        for (int col = 0; col < COLS; col++) {
            if (!isColumnFull(col)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = ROWS - 1; row >= 0; row--) {
            for (int col = 0; col < COLS; col++) {
                char symbol = board[row][col] == 0 ? ' ' : board[row][col] == 1 ? 'X' : 'O';
                sb.append(symbol + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static Board fromString(String boardString) {
        Board newBoard = new Board();
        String[] rows = boardString.trim().split("\n");

        for (int i = 0; i < rows.length; i++) {
            String[] cols = rows[i].trim().split(" ");
            for (int j = 0; j < cols.length; j++) {
                if (cols[j].equals("X")) {
                    newBoard.board[ROWS - 1 - i][j] = 1;  // 'X' = p1
                } else if (cols[j].equals("O")) {
                    newBoard.board[ROWS - 1 - i][j] = 2;  // 'O' = p2
                } else {
                    newBoard.board[ROWS - 1 - i][j] = 0;  // nothing
                }
            }
        }
        return newBoard;
    }
}
