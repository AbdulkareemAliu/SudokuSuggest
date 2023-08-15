package org.aaliu;

import java.io.File;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.aaliu.SudokuBoard;

public class ReadWebpage {
    public static void main(String[] args) {
        String[][] board;
        try {
            // Accessing grid element
            String pathToHtml = "src/main/java/org/aaliu/example.html";
            File htmlFile = new File(pathToHtml);
            Document doc = Jsoup.parse(htmlFile, "UTF-8");
            Element gridElement = doc.select("#puzzle_grid > tbody").first();
            // // Extract cell values from the grid
            board = extractCellValues(gridElement);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // // Convert cell values to a Java 2D array
        SudokuBoard mySudokuBoard = new SudokuBoard(board);

        Scanner myScanner = new Scanner(System.in);
        while (true) {
            System.out.println(mySudokuBoard.evaluate());
            
            // Read the entire line of input
            String line = myScanner.nextLine();
            String[] tokens = line.split(" ");
            
            if (tokens.length != 3) {
                System.out.println("Invalid input format. Please provide value, row number, and column number separated by spaces.");
                continue; // Skip this iteration and start over
            }
            
            try {
                int rowNum = Integer.parseInt(tokens[0]);
                int colNum = Integer.parseInt(tokens[1]);
                String value = tokens[2];
        
                mySudokuBoard.updateBoard(value, rowNum, colNum);
            } catch (NumberFormatException e) {
                System.out.println("Invalid row or column number. Please provide valid integers.");
            }
        }
        
        
    }

    private static String[][] extractCellValues(Element gridElement) {
        // Converting grid element into list of list of Strings

        String[][] board = new String[9][9];

        for (Element row: gridElement.children()){
            for (Element cell: row.children()){
                String id = cell.id();
                int colNum = id.charAt(1) - '0';
                int rowNum = id.charAt(2) - '0';
                String value = cell.select("input").attr("value");
                value = value != "" ? value : ".";
                board[rowNum][colNum] = value;
            }
        }
        printSudokuArray(board);
        return board;
    }

    private static void printSudokuArray(String[][] sudokuArray) {
        for (String[] row: sudokuArray) {
            System.out.println(String.join("_", row));
        }
    }
}
