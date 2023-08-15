package org.aaliu;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

/*
Naked Singles: If a cell has only one possible value remaining based on row, column, and block constraints, that value is assigned to the cell.

Hidden Singles: If a particular value appears only once as a candidate in a row, column, or block, that value is assigned to the corresponding cell.

Naked Pairs, Triples, and Quads: If a set of two, three, or four cells in a row, column, or block have the same set of candidates, those candidates can be eliminated from other cells in the same row, column, or block.

Pointing Pairs and Triples: If a certain candidate appears only in a particular row or column within a block, that candidate can be eliminated from other cells in that row or column outside the block.

Box-Line Reduction: If a candidate is confined to a particular row or column within a block and there's a corresponding row or column in the larger grid, that candidate can be eliminated from other cells in that row or column.

X-Wing: If a candidate is restricted to two rows in two different columns (or vice versa) and the same candidate is restricted to the same two columns in two different rows, the candidate can be eliminated from other cells in the affected rows and columns.

Swordfish and Jellyfish: These are advanced strategies that involve finding candidate patterns in rows and columns.

Coloring: This involves identifying pairs of cells with the same candidates and using them to eliminate candidates from other cells.

Y-Wing and XY-Wing: More complex strategies that involve chains of cells and candidates.
 */

public class SudokuBoard {
    public HashMap<Integer, HashSet<String>> rowMap, colMap;
    public HashMap<String, HashSet<String>> regMap;
    public HashMap<String, HashSet<Integer>> posToCandidates;

    public SudokuBoard(String[][] board){

        rowMap = new HashMap<>();
        colMap = new HashMap<>();
        regMap = new HashMap<>();
        posToCandidates = new HashMap<>();

        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {

                if (board[i][j].equals(".")) {
                    posToCandidates.put(i + "" + j, new HashSet<Integer>());
                    continue;
                }

                String val = board[i][j];

                int row = i;
                int col = j;

                updateBoard(val, row, col);

            }
        }

        for (Map.Entry<String, HashSet<Integer>> mapEntry : posToCandidates.entrySet()) {
            String pair = mapEntry.getKey();
            HashSet<Integer> candidateSet = mapEntry.getValue();

            int rowNum = pair.charAt(0) - '0';
            int colNum = pair.charAt(1) - '0';
            String region = (rowNum / 3) + "" + (colNum / 3);

            HashSet<String> rowSet = rowMap.get(rowNum);
            HashSet<String> colSet = colMap.get(colNum);
            HashSet<String> regSet = regMap.get(region);
            HashSet<String> totalSet = new HashSet<>();

            totalSet.addAll(rowSet);
            totalSet.addAll(colSet);
            totalSet.addAll(regSet);

            for (Integer i = 1; i <= 9; i++) {
                if (!totalSet.contains(i.toString())){
                    candidateSet.add(i);
                }
            }



            posToCandidates.put(pair, candidateSet);

        }



    }

    public void updateBoard(String value, int rowNum, int colNum){
        String region = (rowNum / 3) + "" + (colNum / 3);

        HashSet<String> rowSet = rowMap.getOrDefault(rowNum, new HashSet<String>());
        HashSet<String> colSet = colMap.getOrDefault(colNum, new HashSet<String>());
        HashSet<String> regSet = regMap.getOrDefault(region, new HashSet<String>());

        rowSet.add(value);
        colSet.add(value);
        regSet.add(value);

        rowMap.put(rowNum, rowSet);
        colMap.put(colNum, colSet);
        regMap.put(region, regSet);

        if (posToCandidates.keySet().contains(rowNum + "" + colNum)){
            posToCandidates.remove(rowNum + "" + colNum);
        }

        updateCandidates(value, rowNum, colNum);


    }

    private void updateCandidates(String value, int rowNum, int colNum){
        String region = (rowNum / 3) + "" + (colNum / 3);

        for (Map.Entry<String, HashSet<Integer>> mapEntry : posToCandidates.entrySet()) {
            String position = mapEntry.getKey();

            int curRowNum = position.charAt(0) - '0';
            int curColNum = position.charAt(1) - '0';
            String curRegion = (curRowNum / 3) + "" + (curColNum / 3);

            HashSet<Integer> setOfCandidates = mapEntry.getValue();
            if (
                curRowNum == rowNum ||
                curColNum == colNum ||
                curRegion.equals(region)
            ){
                setOfCandidates.remove(Integer.parseInt(value));
            }
        }

    }

    private String NakedSingle(){
        for (Map.Entry<String, HashSet<Integer>> mapEntry : posToCandidates.entrySet()) {
            String pair = mapEntry.getKey();
            HashSet<Integer> candidateSet = mapEntry.getValue();

            int rowNum = pair.charAt(0) - '0';
            int colNum = pair.charAt(1) - '0';
            if (candidateSet.size() == 1) {
                return "Item at row " + (rowNum + 0) + " and column " + (colNum + 0) + " is a Naked Single and has value " + candidateSet.iterator().next();
            }
        }
        return "";
    }

    private String findHiddenSingle(HashMap<Integer, HashMap<Integer, String>> candidates){
        for (HashMap<Integer, String> candidateMap : candidates.values()) {
            for(Map.Entry<Integer, String> candidateEntries: candidateMap.entrySet()){
                Integer candidate = candidateEntries.getKey();
                String position = candidateEntries.getValue();
                if(!position.equals("")){
                    int rowNum = position.charAt(0) - '0';
                    int colNum = position.charAt(1) - '0';
                    return "Item at row " + (rowNum + 0) + " and column " + (colNum + 0) + " is a Hidden Single and has value " + candidate;
                }
            }
        }
        return "";
    }

    private String HiddenSingle(){
        HashMap<Integer, HashMap<Integer, String>> rowCandidates = new HashMap<>();
        HashMap<Integer, HashMap<Integer, String>> colCandidates = new HashMap<>();
        HashMap<Integer, HashMap<Integer, String>> regCandidates = new HashMap<>();

        for (Map.Entry<String, HashSet<Integer>> mapEntry : posToCandidates.entrySet()) {
            String pair = mapEntry.getKey();
            HashSet<Integer> candidateSet = mapEntry.getValue();

            int rowNum = pair.charAt(0) - '0';
            int colNum = pair.charAt(1) - '0';
            int region = Integer.parseInt((rowNum / 3) + "" + (colNum / 3));

            HashMap<Integer, String> rowCandidateInd = rowCandidates.getOrDefault(rowNum, new HashMap<Integer, String>());
            HashMap<Integer, String> colCandidateInd = colCandidates.getOrDefault(colNum, new HashMap<Integer, String>());
            HashMap<Integer, String> regCandidateInd = regCandidates.getOrDefault(region, new HashMap<Integer, String>());

            for (Integer candidate: candidateSet) {
                if (rowCandidateInd.containsKey(candidate)) {
                    rowCandidateInd.put(candidate, "");
                } else {
                    rowCandidateInd.put(candidate, pair);
                }
                
                if (colCandidateInd.containsKey(candidate)) {
                    colCandidateInd.put(candidate, "");
                } else {
                    colCandidateInd.put(candidate, pair);
                }

                if (regCandidateInd.containsKey(candidate)) {
                    regCandidateInd.put(candidate, "");
                } else {
                    regCandidateInd.put(candidate, pair);
                }
            }

            rowCandidates.put(rowNum, rowCandidateInd);
            colCandidates.put(colNum, colCandidateInd);
            regCandidates.put(region, regCandidateInd);
        }

        String rowResult = findHiddenSingle(rowCandidates);
        if(!rowResult.equals("")) {
            return rowResult;
        }

        String colResult = findHiddenSingle(colCandidates);
        if (!colResult.equals("")) {
            return colResult;
        }

        String regResult = findHiddenSingle(regCandidates);
        if (!regResult.equals("")) {
            return regResult;
        }

        return "";


    }

    private void updateCandidatesFromPairs(ArrayList<String> pairs, String value){
        int rowNum, colNum;
        for (String pair : pairs) {
            rowNum = pair.charAt(0) - '0';
            colNum = pair.charAt(1) - '0';
            updateCandidates(value, rowNum, colNum);
        }
    }

    private void PointingPairs(){
        HashMap<String, HashMap<Integer, ArrayList<String>>> candToIndices = new HashMap<String, HashMap<Integer, ArrayList<String>>>();
        for (Map.Entry<String, HashSet<Integer>> mapEntry : posToCandidates.entrySet()) {
            String pair = mapEntry.getKey();
            HashSet<Integer> candidateSet = mapEntry.getValue();

            int rowNum = pair.charAt(0) - '0';
            int colNum = pair.charAt(1) - '0';
            String region = (rowNum / 3) + "" + (colNum / 3);

            HashMap<Integer, ArrayList<String>> currIndMap = candToIndices.getOrDefault(region, new HashMap<Integer, ArrayList<String>>());

            ArrayList<String> indices;
            for (Integer candidate: candidateSet) {
                indices = currIndMap.getOrDefault(candidate, new ArrayList<String>());
                indices.add(pair);
                currIndMap.put(candidate, indices);
            }
            
            candToIndices.put(region, currIndMap);
        }

        for (HashMap<Integer, ArrayList<String>> candInd : candToIndices.values()) {
            for (Map.Entry<Integer, ArrayList<String>> curIndexMap : candInd.entrySet()) {
                Integer curCandidate = curIndexMap.getKey();
                ArrayList<String> curIndices = curIndexMap.getValue();
    
                if (curIndices.size() >= 2 && curIndices.size() <= 3) {
                    char rowChar = curIndices.get(0).charAt(0);
                    char colChar = curIndices.get(0).charAt(1);
                    boolean isRowPair = true;
                    boolean isColPair = true;

                    for (String idx : curIndices) {
                        if (idx.charAt(0) != rowChar) {
                            isRowPair = false;
                        }
                        if (idx.charAt(1) != colChar) {
                            isColPair = false;
                        }
                    }
    
                    if (isRowPair || isColPair) {
                        updateCandidatesFromPairs(curIndices, curCandidate.toString());
                    }
                }
            }
        }
    } 

    public String evaluate(){
        String nakedSingleRes = NakedSingle();
        if (!nakedSingleRes.equals("")){
            return nakedSingleRes;
        }

        String hiddenSingleRes = HiddenSingle();
        if (!hiddenSingleRes.equals("")){
            return hiddenSingleRes;
        }
        return "No strategies have worked";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Row Constraints:\n");
        for (int row : rowMap.keySet()) {
            sb.append("Row ").append(row).append(": ").append(rowMap.get(row)).append("\n");
        }

        sb.append("\nColumn Constraints:\n");
        for (int col : colMap.keySet()) {
            sb.append("Column ").append(col).append(": ").append(colMap.get(col)).append("\n");
        }

        sb.append("\nRegion Constraints:\n");
        for (String region : regMap.keySet()) {
            sb.append("Region ").append(region).append(": ").append(regMap.get(region)).append("\n");
        }

        return sb.toString();
    }
}
