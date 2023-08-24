package org.aaliu;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

public class SudokuBoard {
    // Various Data Structures that store the positions of numbers and candidate information
    private HashMap<Integer, HashSet<String>> rowMap, colMap;
    private HashMap<String, HashSet<String>> regMap;
    private HashMap<String, HashSet<Integer>> posToCandidates;
    private HashMap<String, HashMap<Integer, ArrayList<String>>> regToCandPosition = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> rowToCandPosition = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> colToCandPosition = new HashMap<>();
    private HashMap<String, ArrayList<String>> eliminationSummary = new HashMap<String, ArrayList<String>>();


    public SudokuBoard(ArrayList<ArrayList<String>> board){

        rowMap = new HashMap<>();
        colMap = new HashMap<>();
        regMap = new HashMap<>();
        posToCandidates = new HashMap<>();

        // Initalizing rowMap, colMap, regMap, and posToCandidates with values from the board
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {

                if (board.get(i).get(j).equals(".")) {
                    posToCandidates.put(i + "" + j, new HashSet<Integer>());
                    continue;
                }

                String val = board.get(i).get(j);

                int row = i;
                int col = j;

                updateBoard(val, row, col, true);

            }
        }

        // Populating the posToCandidates values with "candidates" or the set of values an empty cell could be
        for (Map.Entry<String, HashSet<Integer>> mapEntry : posToCandidates.entrySet()) {
            String pair = mapEntry.getKey();
            HashSet<Integer> candidateSet = mapEntry.getValue();

            int rowNum = pair.charAt(0) - '0';
            int colNum = pair.charAt(1) - '0';
            String region = (rowNum / 3) + "" + (colNum / 3);

            HashSet<String> rowSet = rowMap.getOrDefault(rowNum, new HashSet<String>());
            HashSet<String> colSet = colMap.getOrDefault(colNum, new HashSet<String>());
            HashSet<String> regSet = regMap.getOrDefault(region, new HashSet<String>());
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

        createCandidateObjects();

    }

    // Creating more specific candidate objects that are used in the candidate reduction algorithms
    private void createCandidateObjects() {
        // these hashmaps map some row/col/region to another hashmap that maps a candidate value to a list of relevent positions within these rows/cols/regions

        for (Map.Entry<String, HashSet<Integer>> mapEntry : posToCandidates.entrySet()) {
            String pair = mapEntry.getKey();
            HashSet<Integer> candidateSet = mapEntry.getValue();

            int rowNum = pair.charAt(0) - '0';
            int colNum = pair.charAt(1) - '0';
            String region = (rowNum / 3) + "" + (colNum / 3);

            HashMap<Integer, ArrayList<Integer>> curRowCands = rowToCandPosition.getOrDefault(rowNum, new HashMap<Integer, ArrayList<Integer>>());
            HashMap<Integer, ArrayList<Integer>> curColCands = colToCandPosition.getOrDefault(colNum, new HashMap<Integer, ArrayList<Integer>>());
            HashMap<Integer, ArrayList<String>> curRegCands = regToCandPosition.getOrDefault(region, new HashMap<Integer, ArrayList<String>>());

            for (Integer candidate : candidateSet) {
                ArrayList<Integer> whereInRow = curRowCands.getOrDefault(candidate, new ArrayList<Integer>());
                ArrayList<Integer> whereInCol = curColCands.getOrDefault(candidate, new ArrayList<Integer>());
                ArrayList<String> whereInReg = curRegCands.getOrDefault(candidate, new ArrayList<String>());

                whereInRow.add(colNum);
                whereInCol.add(rowNum);
                whereInReg.add(pair);

                curRowCands.put(candidate, whereInRow);
                curColCands.put(candidate, whereInCol);
                curRegCands.put(candidate, whereInReg);
            }

            rowToCandPosition.put(rowNum, curRowCands);
            colToCandPosition.put(colNum, curColCands);
            regToCandPosition.put(region, curRegCands);
            
        }
    }

    // public interface for updating internal structures
    public void updateBoard(String value, int rowNum, int colNum, boolean initalizing){
        String region = (rowNum / 3) + "" + (colNum / 3);

        HashSet<String> rowSet = rowMap.getOrDefault(rowNum, new HashSet<String>());
        HashSet<String> colSet = colMap.getOrDefault(colNum, new HashSet<String>());
        HashSet<String> regSet = regMap.getOrDefault(region, new HashSet<String>());

        if (rowSet.contains(value)){
            throw new IllegalArgumentException("Oops! There seems to be an extra " + value + " in row " + rowNum + 1);
        } else if (colSet.contains(value)){
            throw new IllegalArgumentException("Oops! There seems to be an extra " + value + " in column " + colNum + 1);
        } else if (regSet.contains(value)){
            throw new IllegalArgumentException("Oops! There seems to be an extra " + value + " in region " + region);
        }

        rowSet.add(value);
        colSet.add(value);
        regSet.add(value);

        rowMap.put(rowNum, rowSet);
        colMap.put(colNum, colSet);
        regMap.put(region, regSet);

        if (posToCandidates.keySet().contains(rowNum + "" + colNum)){
            posToCandidates.remove(rowNum + "" + colNum);
        }
        
        if (!initalizing){
            updateCandidates(value, rowNum, colNum, Arrays.asList("row", "col", "reg"), new HashSet<String>());
        }
        
    }

    // Updates the candidate structures by removing a given value from the dimensions specified in the toUpdate list 
    // The ignore set specifies the position of values to ignore and not update
    private void updateCandidates(String value, int rowNum, int colNum, List<String> toUpdate, HashSet<String> ignoreSet){
        String region = (rowNum / 3) + "" + (colNum / 3);

        for (Map.Entry<String, HashSet<Integer>> mapEntry : posToCandidates.entrySet()) {
            String position = mapEntry.getKey();
            
            if (ignoreSet.contains(position)){
                continue;
            }

            int curRowNum = position.charAt(0) - '0';
            int curColNum = position.charAt(1) - '0';
            String curRegion = (curRowNum / 3) + "" + (curColNum / 3);

            HashSet<Integer> setOfCandidates = mapEntry.getValue();
            boolean check = false;

            if (toUpdate.contains("row")) {
                check |= curRowNum == rowNum;
            } 
            if (toUpdate.contains("col")) {
                check |= curColNum == colNum;
            } 
            if (toUpdate.contains("reg")) {
                check |= curRegion.equals(region);
            }

            if (check) {
                setOfCandidates.remove(Integer.parseInt(value));
            }
        }

        if (toUpdate.contains("row")){
            HashMap<Integer, ArrayList<Integer>> rowCands = rowToCandPosition.get(rowNum);
            ArrayList<Integer> colVals = new ArrayList<Integer>();
            for (String colVal : ignoreSet){
                colVals.add(colVal.charAt(1) - '0');
            }
            rowCands.put(Integer.parseInt(value), colVals);
        }
        
        if (toUpdate.contains("col")){  
            HashMap<Integer, ArrayList<Integer>> colCands = colToCandPosition.get(colNum);
            ArrayList<Integer> rowVals = new ArrayList<Integer>();
            for (String rowVal : ignoreSet){
                rowVals.add(rowVal.charAt(0) - '0');
            }
            colCands.put(Integer.parseInt(value), rowVals);
        }

        if (toUpdate.contains("reg")){
            HashMap<Integer, ArrayList<String>> regCands = regToCandPosition.get(region);
            regCands.put(Integer.parseInt(value), new ArrayList<>(ignoreSet));
        }     
    }

    //This technique suggests values in positions where only one value can go based on the posToCandidates structure 
    private String NakedSingle(){
        for (Map.Entry<String, HashSet<Integer>> mapEntry : posToCandidates.entrySet()) {
            String pair = mapEntry.getKey();
            HashSet<Integer> candidateSet = mapEntry.getValue();

            int rowNum = pair.charAt(0) - '0';
            int colNum = pair.charAt(1) - '0';
            if (candidateSet.size() == 1) {
                return (rowNum + 1) + "" + (colNum + 1) + "" + candidateSet.iterator().next();
            }
        }
        return "";
    }

    // Helper function 
    private String findHiddenSingle(HashMap<Integer, HashMap<Integer, String>> candidates){
        for (HashMap<Integer, String> candidateMap : candidates.values()) {
            for(Map.Entry<Integer, String> candidateEntries: candidateMap.entrySet()){
                Integer candidate = candidateEntries.getKey();
                String position = candidateEntries.getValue();

                // When position is "" we know that there was more than one of this candidate in the group
                if(!position.equals("")){
                    int rowNum = position.charAt(0) - '0';
                    int colNum = position.charAt(1) - '0';
                    return (rowNum + 1) + "" + (colNum + 1) + "" + candidate;
                }
            }
        }
        return "";
    }

    //This techniques suggests values for a given position if that position is the only place that value can be in a row/col/reg
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

            //candidates are first added, and if they are found again when iterated they are discarded and set to some placeholder value

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

    // Some more information on this pattern: https://sudoku.com/sudoku-rules/pointing-pairs/
    private boolean PointingPairs(){
        boolean didEliminate = false;
        for (HashMap<Integer, ArrayList<String>> candInd : regToCandPosition.values()) {
            for (Map.Entry<Integer, ArrayList<String>> curIndexMap : candInd.entrySet()) {
                Integer curCandidate = curIndexMap.getKey();
                ArrayList<String> curIndices = curIndexMap.getValue();
                
                //we are only concerned about cases in which a region has 2 or 3 dependencies on a candidate
                if (curIndices.size() >= 2 && curIndices.size() <= 3) {
                    char rowChar = curIndices.get(0).charAt(0);
                    char colChar = curIndices.get(0).charAt(1);
                    boolean isRowPair = true;
                    boolean isColPair = true;

                    // This ensures that the group found is either in the same row or column
                    for (String idx : curIndices) {
                        if (idx.charAt(0) != rowChar) {
                            isRowPair = false;
                        }
                        if (idx.charAt(1) != colChar) {
                            isColPair = false;
                        }
                    }
                    
                    // Finally updates the candidates based on whether the found pair is along a row or column
                    if (isRowPair || isColPair) {
                        StringBuilder description = new StringBuilder();
                        description.append("Positions: ");
                        didEliminate = true;
                        int rowNum = rowChar - '0'; int colNum = colChar - '0';

                        //ignoring cases in which the candidate for elimination is already in the regions on the same row
                        String nextReg, prevReg;
                        int regX = rowNum / 3;
                        int regY= colNum / 3;
                        if (isRowPair){
                            nextReg = regX + "" + ((regY + 1) % 3);
                            prevReg = regX + "" + (((regY - 1 % 3) + 3) % 3); //getting positive mod
                        } else {
                            nextReg = ((regX + 1) % 3) + "" + regY;
                            prevReg = (((regX - 1 % 3) + 3) % 3) + "" + regY; //getting positive mod
                        }

                        if (regMap.get(nextReg).contains(curCandidate.toString()) && regMap.get(prevReg).contains(curCandidate.toString())) {
                            continue;
                        }

                        for (String pair : curIndices) {
                            rowNum = pair.charAt(0) - '0';
                            colNum = pair.charAt(1) - '0';
                            description.append("(" + (rowNum + 1) + ", " + (colNum + 1) +") ");

                            List<String> toUpdate = isRowPair ? Arrays.asList("row") : Arrays.asList("col");
                            updateCandidates(curCandidate.toString(), rowNum, colNum, toUpdate, new HashSet<>(curIndices));

                        }
                        String relevantAxis = (isRowPair ? ("row " + (rowNum + 1)) : ("column " + (colNum + 1)));
                        description.append("are the only positions that can be " + curCandidate.toString() + " in their region. This means that no other element in ");
                        description.append(relevantAxis);
                        description.append(" can be a " + curCandidate);
                        
                        ArrayList<String> summaryList = eliminationSummary.getOrDefault(relevantAxis, new ArrayList<String>());
                        summaryList.add(description.toString());
                        eliminationSummary.put(relevantAxis, summaryList);
                        
                    }
                }
            }
        }
        return didEliminate;
    }

    // Some information on this particular pattern: https://sudoku.com/sudoku-rules/h-wing/
    private boolean XWing(String direction){
        assert direction.equals("row") || direction.equals("col") : "'Direction' can only be 'col' or 'row'";

        boolean didEliminate = false;

        HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> candPositionHashMap;

        // direction informs what the base axis for our "box" shouold be
        if (direction.equals("row")){
            candPositionHashMap = rowToCandPosition;
        } else {
             candPositionHashMap = colToCandPosition;
        }

        // This loop finds which candidates appear only twice in a given row/column
        for (Integer i = 0; i < 9; i++){
            if (!candPositionHashMap.keySet().contains(i)){
                continue;
            }
            for (Map.Entry<Integer, ArrayList<Integer>> curCandEntry : candPositionHashMap.get(i).entrySet()){
                Integer cand = curCandEntry.getKey();
                ArrayList<Integer> indsAlongAxis = curCandEntry.getValue();

                if(indsAlongAxis.size() != 2) {
                    continue;
                }

                //This loop looks after the position of the found pair to see if there are any matching to complete the box
                for (Integer j = i + 1; j < 9; j++){

                    if (
                        candPositionHashMap.containsKey(j) &&
                        candPositionHashMap.get(j).containsKey(cand) &&
                        candPositionHashMap.get(j).get(cand).size() == 2 &&
                        candPositionHashMap.get(j).get(cand).containsAll(indsAlongAxis)
                    ){
                        HashSet<String> toIgnore = new HashSet<>();
                        didEliminate = true;

                        StringBuilder description = new StringBuilder();
                        String relevantAxis1 = Integer.toString(indsAlongAxis.get(0) + 1);
                        String relevantAxis2 = Integer.toString(indsAlongAxis.get(1) + 1);

                        if (direction.equals("row")){
                            toIgnore.add(i + "" + indsAlongAxis.get(0)); toIgnore.add(i + "" + indsAlongAxis.get(1));
                            toIgnore.add(j + "" + indsAlongAxis.get(0)); toIgnore.add(j + "" + indsAlongAxis.get(1));

                            description.append(String.format(
                                "Positions (%s, %s), (%s, %s), (%s, %s), and (%s, %s) form an X-Wing box", 
                                (i + 1), relevantAxis1, (i + 1), relevantAxis2,
                                (j + 1), relevantAxis1, (j + 1), relevantAxis2
                            ));

                            updateCandidates(cand.toString(), i, indsAlongAxis.get(0), Arrays.asList(direction), toIgnore);
                            updateCandidates(cand.toString(), i, indsAlongAxis.get(1), Arrays.asList(direction), toIgnore);

                        } else {
                            toIgnore.add(indsAlongAxis.get(0) + "" + i); toIgnore.add(indsAlongAxis.get(1) + "" + i);
                            toIgnore.add(indsAlongAxis.get(0) + "" + j); toIgnore.add(indsAlongAxis.get(1) + "" + j);

                            description.append(String.format(
                                "Positions (%s, %s), (%s, %s), (%s, %s), and (%s, %s) form an X-Wing box", 
                                (indsAlongAxis.get(0) + 1), (i + 1), (indsAlongAxis.get(1) + 1), (i + 1), 
                                (indsAlongAxis.get(0) + 1), (j + 1), (indsAlongAxis.get(1) + 1), (j + 1)
                            ));

                            updateCandidates(cand.toString(), indsAlongAxis.get(0), i, Arrays.asList(direction), toIgnore);
                            updateCandidates(cand.toString(), indsAlongAxis.get(1), i, Arrays.asList(direction), toIgnore);

                        }

                        description.append("; therefore, they are the only positions along ");
                        description.append((direction.equals("row") ? "col" : "row"));
                        description.append("s " + indsAlongAxis.get(0) + " and " + indsAlongAxis.get(1));

                        ArrayList<String> summaryList1 = eliminationSummary.getOrDefault(direction + " " + relevantAxis1, new ArrayList<String>());
                        ArrayList<String> summaryList2 = eliminationSummary.getOrDefault(direction + " " + relevantAxis2, new ArrayList<String>());

                        summaryList1.add(description.toString());
                        summaryList2.add(description.toString());

                        eliminationSummary.put(direction + " " + relevantAxis1, summaryList1);
                        eliminationSummary.put(direction + " " + relevantAxis2, summaryList2);
                    }

                }
                


            }
        }
        return didEliminate;

    }

    private String getDescription(String result, String reason){
        char rowNum = result.charAt(0);
        char colNum = result.charAt(1);
        char value = result.charAt(2);

        HashSet<String> allDescriptions = new HashSet<>();

        allDescriptions.add(String.join(". ", eliminationSummary.getOrDefault("row " + rowNum, new ArrayList<String>())));
        allDescriptions.add(String.join(". ", eliminationSummary.getOrDefault("col " + colNum, new ArrayList<String>())));

        String candidateReductionSummary = String.join(". ", allDescriptions);

        while (candidateReductionSummary.startsWith(". ")){
            candidateReductionSummary = candidateReductionSummary.substring(2);
        }

        if (candidateReductionSummary.equals("")) {
            return value + String.format(" should go in row %s and column %s since %s.", rowNum, colNum, reason);
        } else {
            return candidateReductionSummary + String.format("\n \n Because of this we suggest you put a %s in row %s and column %s since %s.", value, rowNum, colNum, reason);
        }
    }

    public String suggest(){
        if (posToCandidates.size() == 0) {
            return "Looks like you won, congrats!";
        }
        
        while (PointingPairs() || XWing("row") || XWing("col")) {
            String nakedSingleRes = NakedSingle();
            if (!nakedSingleRes.equals("")){
                return getDescription(nakedSingleRes, "it is the only number that can go there");
            }

            String hiddenSingleRes = HiddenSingle();
            if (!hiddenSingleRes.equals("")){
                return getDescription(hiddenSingleRes, "it is the only position in its row/col/region that can be that number");
            }
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
