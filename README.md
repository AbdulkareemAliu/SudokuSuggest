# SudokuSuggest

SudokuSuggest is a chrome extension that takes in some sudoku board state and suggests the next move based on 4 advanced solving techniques implemented in Java. The code for the frontend of the extension are featured in the chrome_app folder. The files that relate to the Spring Boot API called by the extension for the suggestions are featured in the src/main/java/org/aaliu folder. 

Side Note: 
Currently working on adding OCR to detect the current state of a SudokuBoard from a screenshot of the board. The current progress for this feature is in the src/main/java/org/aaliu/ReadImage.java file.