package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }
    //the main app alr tests for overlap
    @Test
    public void testPlacePieceValidMove() {
        assertTrue(board.placePiece(0, 1));  // P1 places piece in column 0
        assertFalse(board.isColumnFull(0));  // The column should not be full
    }

    @Test
    public void testPlacePieceInvalidMove() {
        for (int i = 0; i < 6; i++) {
            assertTrue(board.placePiece(0, 1));  // Fill the column
        }
        assertFalse(board.placePiece(0, 1));  // Column full, this move should fail
    }

    @Test
    public void testWinningConditionHorizontal() { //P1 places pieces from left -> right
        board.placePiece(0, 1);
        board.placePiece(1, 1);
        board.placePiece(2, 1);
        board.placePiece(3, 1);
        assertTrue(board.checkWin(1));  // P1 should win (hori)
    }

    @Test
    public void testWinningConditionVertical() {
        for (int i = 0; i < 4; i++) {
            board.placePiece(0, 1);  // P1 places pieces in col 0 but using 'fori'
        }
        assertTrue(board.checkWin(1));  // P1 should win (vert)
    }

    @Test
    public void testBoardIsFull() {
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                assertTrue(board.placePiece(col, row % 2 == 0 ? 1 : 2));  // Fill with alternating players
            }
        }
        assertTrue(board.isFull());
    }

    @Test
    public void testSaveAndLoadGameState() {
        // Initialize board and game state
        List<Integer> moves = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        for (int col : moves) {
            board.placePiece(col, 1);  // P1 moves
        }

        GameState gameState = new GameState(board, 1, moves);

        // Save gameStatte
        app.saveGameState(board, 1, moves);

        // Load gameState
        GameState loadedState = app.loadGameState();
        assertNotNull(loadedState);
        assertEquals(1, loadedState.getCurrentPlayer());
        assertEquals(moves, loadedState.getMoves());

        // Is the loaded board correctly
        Board loadedBoard = loadedState.getBoard();
        assertEquals(board.toString(), loadedBoard.toString());
    }
}
