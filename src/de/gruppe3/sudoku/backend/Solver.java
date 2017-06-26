package de.gruppe3.sudoku.backend;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max
 */
public class Solver {
    static void solvePartially(byte[][] given, int maxSolvedFields) {
        //noinspection unchecked
        List<Byte>[][] notes = new List[9][9];

        for (byte x = 0; x < 9; x++) {
            for (byte y = 0; y < 9; y++) {
                List<Byte> fieldNotes = allPossibilities();
                checkFieldInColumn(x, fieldNotes, given);
                checkFieldInRow(y, fieldNotes, given);
                checkFieldInSquare(x, y, fieldNotes, given);

                notes[x][y] = fieldNotes;
            }
        }

        int solvedFields = 0;

        boolean done = false;
        boolean solvable = true;
        while (!done && solvable && solvedFields < maxSolvedFields) {
            done = true;
            solvable = false;
            solvedFields = 0;

            for (byte x = 0; x < 9; x++) {
                for (byte y = 0; y < 9; y++) {
                    if (given[x][y] == 0 && notes[x][y].size() == 1) {
                        given[x][y] = notes[x][y].get(0);
                        notes[x][y].remove(0);

                        updateRow(x, y, notes, given);
                        updateColumn(x, y, notes, given);
                        updateSquare(x, y, notes, given);

                        solvable = true;
                        solvedFields++;
                    } else if (given[x][y] != 0) {
                        solvedFields++;
                    }
                }
            }

            for (byte x = 0; x < 9; x++) {
                for (byte y = 0; y < 9; y++) {
                    if (notes[x][y].size() > 0) {
                        done = false;
                    }
                }
            }
        }

        if (!solvable) {
            given[0][0] = -1;
        }
    }

    public static void solve(byte[][] given) {
        //noinspection unchecked
        List<Byte>[][] notes = new List[9][9];

        boolean alreadyDone = true;

        for (byte x = 0; x < 9; x++) {
            for (byte y = 0; y < 9; y++) {
                List<Byte> fieldNotes = allPossibilities();
                checkFieldInColumn(x, fieldNotes, given);
                checkFieldInRow(y, fieldNotes, given);
                checkFieldInSquare(x, y, fieldNotes, given);

                notes[x][y] = fieldNotes;
                if (fieldNotes.size() > 0) alreadyDone = false;
            }
        }

        if (alreadyDone) return;

        boolean done = false;
        boolean solvable = true;
        while (!done && solvable) {
            done = true;
            solvable = false;

            for (byte x = 0; x < 9; x++) {
                for (byte y = 0; y < 9; y++) {
                    if (given[x][y] == 0 && notes[x][y].size() == 1) {

                        given[x][y] = notes[x][y].get(0);
                        notes[x][y].remove(0);

                        updateRow(x, y, notes, given);
                        updateColumn(x, y, notes, given);
                        updateSquare(x, y, notes, given);

                        solvable = true;
                    }
                }
            }

            for (byte x = 0; x < 9; x++) {
                for (byte y = 0; y < 9; y++) {
                    if (notes[x][y].size() > 0) done = false;
                }
            }
        }

        if (!solvable) {
            given[0][0] = -1;
        }
    }

    private static void updateSquare(byte x, byte y, List<Byte>[][] notes, byte[][] given) {
        byte val = given[x][y];

        byte xStart = (byte) (x / 3 * 3); //Divide, round down, multiply
        byte yStart = (byte) (y / 3 * 3); //Divide, round down, multiply

        for (int xc = xStart; xc < xStart + 3; xc++) {
            for (int yc = yStart; yc < yStart + 3; yc++) {
                if (notes[xc][yc].contains(val)) notes[xc][yc].remove(notes[xc][yc].indexOf(val));
            }
        }
    }

    private static void updateColumn(byte x, byte y, List<Byte>[][] notes, byte[][] given) {
        byte val = given[x][y];

        for (int yc = 0; yc < 9; yc++) {
            if (notes[x][yc].contains(val)) notes[x][yc].remove(notes[x][yc].indexOf(val));
        }
    }

    private static void updateRow(byte x, byte y, List<Byte>[][] notes, byte[][] given) {
        byte val = given[x][y];

        for (int xc = 0; xc < 9; xc++) {
            if (notes[xc][y].contains(val)) notes[xc][y].remove(notes[xc][y].indexOf(val));
        }
    }


    static void checkFieldInSquare(byte x, byte y, List<Byte> fieldNotes, byte[][] given) {
        byte xStart = (byte) (x / 3 * 3); //Divide, round down, multiply
        byte yStart = (byte) (y / 3 * 3); //Divide, round down, multiply

        for (int xc = xStart; xc < xStart + 3; xc++) {
            for (int yc = yStart; yc < yStart + 3; yc++) {
                if (fieldNotes.contains(given[xc][yc])) fieldNotes.remove(fieldNotes.indexOf(given[xc][yc]));
            }
        }
    }

    static void checkFieldInRow(byte y, List<Byte> fieldNotes, byte[][] given) {
        for (int xc = 0; xc < 9; xc++) {
            if (fieldNotes.contains(given[xc][y])) fieldNotes.remove(fieldNotes.indexOf(given[xc][y]));
        }
    }

    static void checkFieldInColumn(byte x, List<Byte> fieldNotes, byte[][] given) {
        for (int yc = 0; yc < 9; yc++) {
            if (fieldNotes.contains(given[x][yc])) fieldNotes.remove(fieldNotes.indexOf(given[x][yc]));
        }
    }


    static List<Byte> allPossibilities() {
        List<Byte> possibilities = new ArrayList<>();

        for (byte i = 1; i <= 9; i++) {
            possibilities.add(i);
        }

        return possibilities;
    }


}
