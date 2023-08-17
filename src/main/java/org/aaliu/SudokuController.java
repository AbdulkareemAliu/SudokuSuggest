package org.aaliu;


import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
public class SudokuController {

    @CrossOrigin(origins = "chrome-extension://khpjpfkagcmgmpepmpmgplckcobobcjb")
    @PostMapping(value = "/suggest")
    public HashMap<String, String> handleRequest(@RequestBody SudokuBoardDTO request){
        SudokuBoard convertedBoard;
        
        String result;
        System.out.println("request made");

        
        try {
            ArrayList<ArrayList<String>> myBoard = request.getInputBoard();
            printSudokuArray(myBoard);

            convertedBoard = new SudokuBoard(myBoard);

            System.out.println("conversion made");

            result = convertedBoard.suggest();
        } catch (IllegalArgumentException e) {
            result = "The board looks a little off, please make sure it is valid or I won't be able to make a great suggestion!";
        }

        HashMap<String, String> response = new HashMap<String, String>();
        response.put("response", result);

        System.out.println(result);

        return response;
    }

    private static void printSudokuArray(ArrayList<ArrayList<String>> sudokuArray) {
        for (ArrayList<String> row: sudokuArray) {
            System.out.println(String.join("_", row));
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SudokuController.class, args);
    }
}
