package de.gruppe3.sudoku.backend;

import java.util.List;
import java.util.Random;

/**
 * @author Max
 */
public class Generator {
    private static final long[] SEEDS = {
            5407323736472169620L,
            -8042353658081015279L,
            8976665098050808121L,
            -7081261358895956858L,
            -9222669974325238838L,
            1879218710118810433L,
            7332373476308576978L,
            -8130847646216063396L,
            9006004907410356852L,
            -4579493598877376157L,
            -6589699323586889233L,
            1331363024219721155L,
            -8244109316479312679L,
            8703204573996259565L,
            3433763141351488132L,
            2059152111615838806L,
            -199608048638161256L,
            -903592083093142553L,
            -1231277488393041897L,
            -6173214294124089480L,
            6153699427674334574L,
            -7732303522490961841L,
            -7252866211331364582L,
            4811022883035668729L,
            -730137438807657875L,
            -5761714629455161586L,
            -4665883815272849628L,
            6131497411840585014L,
            4792824498220324483L,
            1737390078372298360L
    };

    public static byte[][] generate(int givenFields) {
        Random random = new Random();
        random.setSeed(SEEDS[random.nextInt(SEEDS.length)]);

        byte[][] sudoku = null;

        boolean solvable = false;

        while (!solvable) {
            sudoku = new byte[9][9];

            for (int f = 0; f < 30; f++) {
                int x = random.nextInt(9);
                int y = random.nextInt(9);

                if (sudoku[x][y] != 0) {
                    f--;
                    continue;
                }

                List<Byte> possibilities = Solver.allPossibilities();
                Solver.checkFieldInSquare((byte) x, (byte) y, possibilities, sudoku);
                Solver.checkFieldInRow((byte) y, possibilities, sudoku);
                Solver.checkFieldInColumn((byte) x, possibilities, sudoku);

                if (possibilities.size() == 0) {
                    f--;
                    continue;
                }

                sudoku[x][y] = possibilities.remove(random.nextInt(possibilities.size()));
            }

            byte[][] toSolve = new byte[9][9];

            for (int x = 0; x < 9; x++) {
                byte[] column = sudoku[x];
                System.arraycopy(column, 0, toSolve[x], 0, column.length);
            }

            Solver.solve(toSolve);

            if (toSolve[0][0] != -1) solvable = true;
        }

        Solver.solvePartially(sudoku, givenFields);

        return sudoku;
    }
}
