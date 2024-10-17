package org.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class app {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Player 1's name: ");
        String player1Name = scanner.nextLine();
        System.out.print("Enter Player 2's name: ");
        String player2Name = scanner.nextLine();

        Board board = new Board();

        int currentPlayer = 1;
        String[] playerNames = {player1Name, player2Name};

        System.out.print("Do you want to load a saved game? (y/n): ");
        String loadGame = scanner.nextLine();

        if (loadGame.equalsIgnoreCase("y")) {
            GameState loadedState = loadGameState();
            if (loadedState != null) {
                board = loadedState.getBoard();
                currentPlayer = loadedState.getCurrentPlayer();
                System.out.println("Loaded!");
            } else {
                System.out.println("No saved found.");
            }
        }

        while (true) {
            System.out.println(board);

            System.out.print(playerNames[currentPlayer - 1] + ", enter a column (0-6), '/save' to save, '/load' to load: ");
            String input = scanner.nextLine();

            if (input.equals("/save")) {
                // savee
                saveGameState(board, currentPlayer);
                System.out.println("Game state saved!");
            } else if (input.equals("/load")) {
                // load
                GameState loadedState = loadGameState();
                if (loadedState != null) {
                    board = loadedState.getBoard();
                    currentPlayer = loadedState.getCurrentPlayer();
                    System.out.println("Game state loaded!");
                } else {
                    System.out.println("No saved game found.");
                }
            } else {
                int col;
                try {
                    col = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a column (0-6) ");
                    continue;
                }

                if (board.placePiece(col, currentPlayer)) {
                    if (board.checkWin(currentPlayer)) {
                        System.out.println(playerNames[currentPlayer - 1] + " wins!");
                        break;
                    } else if (board.isFull()) {
                        System.out.println("tie!");
                        break;
                    }

                    currentPlayer = 3 - currentPlayer;
                } else {
                    System.out.println("Invalid column. ");
                }
            }
        }
    }

    private static void saveGameState(Board board, int currentPlayer) {
        try {
            // todo: make the output utf8
            FileOutputStream fileOutputStream = new FileOutputStream("game_state.txt");
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            GameState gameState = new GameState(board, currentPlayer);
            writer.write(gameState.toString());

            writer.close();
            fileOutputStream.close();
        } catch (IOException e) {
            System.out.println("Error saving game state: ");
            e.printStackTrace();
        }
    }

    private static GameState loadGameState() {
        try {
            FileInputStream fileInputStream = new FileInputStream("game_state.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

            String currentPlayerLine = reader.readLine();
            String boardState = reader.readLine();

            int currentPlayer = Integer.parseInt(currentPlayerLine.split(": ")[1]);

            Board board = Board.fromString(boardState);

            reader.close();
            fileInputStream.close();

            return new GameState(board, currentPlayer);
        } catch (IOException e) {
            System.out.println("Error loading game state: ");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error parsing saved game data.");
            e.printStackTrace();
        }
        return null;
    }
}