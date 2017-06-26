package de.gruppe3.sudoku;

import java.util.List;
import java.util.Random;

/**
 * @author Max
 */
public class Generator {
    static byte[][] generate(int givenFields) {
        byte[][] sudoku = new byte[9][9];

        Random random = new Random();

        for (int i = 0; i < givenFields; i++) {
            int x = random.nextInt(9);
            int y = random.nextInt(9);

            List<Byte>
        }
    }
}
