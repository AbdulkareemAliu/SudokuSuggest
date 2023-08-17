package org.aaliu;

public class SudokuCell {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value.equals("") ? "." : value;
    }
}
