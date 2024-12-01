package org.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class app {
    private static DatabaseHelper databaseHelper;

    public static void main(String[] args) {
        databaseHelper = new DatabaseHelper();
        databaseHelper.connect();
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Player 1's name: ");
        String player1Name = scanner.nextLine();
        System.out.print("Enter Player 2's name: ");
        String player2Name = scanner.nextLine();

        Board board = new Board();
        databaseHelper.insertPlayer(player1Name);
        databaseHelper.insertPlayer(player2Name);

        int currentPlayer = 1;
        String[] playerNames = {player1Name, player2Name};

        System.out.print("Do you want to load a saved game? (y/n): ");
        String loadGame = scanner.nextLine();

        List<Integer> moves = new ArrayList<>();

        if (loadGame.equalsIgnoreCase("y")) {
            GameState loadedState = loadGameState();
            if (loadedState != null) {
                board = loadedState.getBoard();
                currentPlayer = loadedState.getCurrentPlayer();
                moves = loadedState.getMoves();
                System.out.println("Loaded!");
            } else {
                System.out.println("No saved found.");
            }
        }

        System.out.println("Do you want to the current players number of wins? (y/n)");
        String scoreboard = scanner.nextLine();

        if (scoreboard.equalsIgnoreCase("y")) {
            System.out.println(player1Name + "'s number of wins: " + databaseHelper.getWins(player1Name));
            System.out.println(player2Name + "'s number of wins: " + databaseHelper.getWins(player2Name));
        }

        while (true) {
            System.out.println(board);

            System.out.print(playerNames[currentPlayer - 1] + ", enter a column (0-6), '/save' to save, '/load' to load: ");
            String input = scanner.nextLine();

            if (input.equals("/save")) {
                // save
                saveGameState(board, currentPlayer, moves);
                System.out.println("Game state saved!");
            } else if (input.equals("/load")) {
                // load
                GameState loadedState = loadGameState();
                if (loadedState != null) {
                    board = loadedState.getBoard();
                    currentPlayer = loadedState.getCurrentPlayer();
                    moves = loadedState.getMoves();
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
                        String winner = playerNames[currentPlayer - 1];
                        databaseHelper.updateWins(winner);
                        System.out.println(winner + " wins! \n" + winner + "'s number of wins: " + databaseHelper.getWins(winner));
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
        databaseHelper.close();
    }
    public static void saveGameState(Board board, int currentPlayer, List<Integer> moves) {
        try (FileOutputStream fileOutputStream = new FileOutputStream("game_state.txt");
             OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {

            String movesString = moves.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            writer.write(currentPlayer + "|" + board.toString().replace("\n", "\\n") + "|" + movesString);
            System.out.println("Game state successfully saved.");
        } catch (IOException e) {
            System.out.println("Error saving game state:");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
    public static GameState loadGameState() {
        try {
            String filePath = System.getProperty("user.dir") + "/game_state.txt";
            System.out.println("Loading game state from: " + filePath);

            FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

            String dataLine = reader.readLine();
            System.out.println("Loaded data from file: " + dataLine);

            if (dataLine == null || dataLine.isEmpty()) {
                System.out.println("No saved game found.");
                return null;
            }

            String[] dataParts = dataLine.split("\\|");
            System.out.println("Split data: " + Arrays.toString(dataParts));

            if (dataParts.length < 2 || dataParts.length > 3) {
                System.out.println("Invalid saved game format.");
                return null;
            }

            int currentPlayer = Integer.parseInt(dataParts[0].trim());

            String boardString = dataParts[1].trim().replace("\\n", "\n");
            Board board = Board.fromString(boardString);

            List<Integer> moves = new ArrayList<>();
            if (dataParts.length == 3 && !dataParts[2].trim().isEmpty()) {
                moves = Arrays.stream(dataParts[2].trim().split(","))
                        .map(String::trim)
                        .filter(move -> !move.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }
            reader.close();
            fileInputStream.close();
            System.out.println("Successfully loaded game state.");
            return new GameState(board, currentPlayer, moves);
        } catch (IOException e) {
            System.out.println("Error loading game state:");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error parsing saved game data:");
            e.printStackTrace();
        }
        return null;
    }}