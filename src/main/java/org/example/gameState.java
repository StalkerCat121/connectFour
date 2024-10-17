package org.example;

import java.io.Serializable;

class GameState implements Serializable {
    private final Board board;
    private final int currentPlayer;

    public GameState(Board board, int currentPlayer) {
        this.board = board;
        this.currentPlayer = currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
}