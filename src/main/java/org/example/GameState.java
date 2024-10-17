package org.example;

import java.util.List;

public class GameState {
    private Board board;
    private int currentPlayer;
    private List<Integer> moves;

    public GameState(Board board, int currentPlayer, List<Integer> moves) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.moves = moves;
    }

    public Board getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Integer> getMoves() {
        return moves;
    }

    public void setMoves(List<Integer> moves) {
        this.moves = moves;
    }
}