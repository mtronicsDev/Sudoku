package de.gruppe3.sudoku;

import de.gruppe3.sudoku.backend.Generator;
import de.gruppe3.sudoku.backend.Solver;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Controller {

    private static final String EDIT_DIFFICULTY = "Change Difficulty";
    private static final String EDIT_BG_COLOR = "Edit Background";
    private static final String EDIT_FG_COLOR = "Edit Foreground";

    private static final Color WRONG_FIELD_COLOR = new Color(0.8902, 0.451, 0.4431, 1);

    private boolean menuOpen = false;
    private int difficulty = 3;

    @FXML
    GridPane sudokuGrid;

    @FXML
    Button newButton;

    @FXML
    Button saveButton;

    @FXML
    Button loadButton;

    @FXML
    Button editButton;

    @FXML
    Button solveButton;

    @FXML
    Button restartButton;

    private ListView<String> editMenu;
    private ListView<ImageView> loadMenu;

    private ColorPicker bgColorPicker;
    private ColorPicker numColorPicker;

    private Color bgColor = Color.WHEAT;
    private Color fgColor = Color.BLACK;

    private Slider difficultySlider;

    @FXML
    BorderPane contentPane;

    private byte[][] empty = new byte[9][9];
    private byte[][] replayField = empty;

    private TextField[][] sudokuSquares;
    private boolean setup = true;

    @FXML
    public void initialize() {
        sudokuSquares = new TextField[9][9];

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                TextField sudokuSquare = new TextField("");
                sudokuSquares[x][y] = sudokuSquare;

                sudokuSquare.textProperty().addListener((o, oldVal, newVal) -> {
                    sudokuSquare.setStyle(sudokuSquare.getStyle().concat(String.format("-fx-background-color: rgb(%d, %d, %d);",
                            (int)(bgColor.getRed()*255), (int)(bgColor.getGreen()*255), (int)(bgColor.getBlue()*255))));

                    if (newVal.length() > 1) {
                        newVal = newVal.substring(0, 1);
                        sudokuSquare.setText(newVal);
                    }

                    if (!newVal.matches("[1-9]")) sudokuSquare.setText("");
                    else {
                        boolean solvable = isSettingUp() || checkSudoku() != null;

                        if (!solvable) {
                            sudokuSquare.setStyle(sudokuSquare.getStyle().concat(String.format("-fx-background-color: rgb(%d, %d, %d);",
                                    (int)(WRONG_FIELD_COLOR.getRed()*255),
                                    (int)(WRONG_FIELD_COLOR.getGreen()*255),
                                    (int)(WRONG_FIELD_COLOR.getBlue()*255))));
                        } else {
                            boolean done = true;

                            for (int xc = 0; xc < 9; xc++) {
                                if (!done) break;

                                for (int yc = 0; yc < 9; yc++) {
                                    if (!done) break;

                                    if (sudokuSquares[xc][yc].getText().equals("")) {
                                        done = false;
                                    }
                                }
                            }

                            if(done) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setHeaderText("You did it!");
                                alert.setContentText("You successfully solved the Sudoku. Play Again!");
                                alert.showAndWait();
                            }
                        }
                    }
                });

                sudokuSquare.getStyleClass().add("sudokuSquare");

                if (x % 3 == 2 && y % 3 != 2 && x != 8) {
                    sudokuSquare.getStyleClass().add("verticalLine");
                } else if (x == 8)
                    sudokuSquare.getStyleClass().add("sudokuRightBorder");

                if (y % 3 == 2 && x % 3 != 2 && y != 8) {
                    sudokuSquare.getStyleClass().add("horizontalLine");
                } else if (y == 8)
                    sudokuSquare.getStyleClass().add("sudokuBottomBorder");

                if (y % 3 == 2 && x % 3 == 2) {
                    if (x != 8 && y != 8)
                        sudokuSquare.getStyleClass().add("crossLine");
                    else if (x == 8 && y == 8)
                        sudokuSquare.getStyleClass().add("sudokuBottomRightCorner");
                    else if (y == 8)
                        sudokuSquare.getStyleClass().add("verticalLineBottom");
                    else
                        sudokuSquare.getStyleClass().add("horizontalLineRight");
                }

                if (y % 3 != 2 && x % 3 != 2)
                    sudokuSquare.getStyleClass().add("sudokuSquareNormal");

                sudokuGrid.add(sudokuSquare, x, y);
            }
        }

        bgColorPicker = new ColorPicker(Color.WHITE);
        bgColorPicker.setOnAction(e -> {
            bgColor = bgColorPicker.getValue();

            for (TextField[] line : sudokuSquares) {
                for (TextField square : line) {
                    square.setStyle(square.getStyle().concat(String.format("-fx-background-color: rgb(%d, %d, %d);",
                            (int)(bgColor.getRed()*255), (int)(bgColor.getGreen()*255), (int)(bgColor.getBlue()*255))));
                }
            }

            contentPane.setCenter(sudokuGrid);
            menuOpen = false;
        });

        numColorPicker = new ColorPicker(Color.BLACK);
        numColorPicker.setOnAction(e -> {
            fgColor = numColorPicker.getValue();

            for (TextField[] line : sudokuSquares) {
                for (TextField square : line) {
                    square.setStyle(square.getStyle().concat(String.format("-fx-text-fill: rgb(%d, %d, %d); -fx-border-color: rgb(%d, %d, %d);",
                            (int)(fgColor.getRed()*255), (int)(fgColor.getGreen()*255), (int)(fgColor.getBlue()*255),
                            (int)(fgColor.getRed()*255), (int)(fgColor.getGreen()*255), (int)(fgColor.getBlue()*255))));
                }
            }

            contentPane.setCenter(sudokuGrid);
            menuOpen = false;
        });

        difficultySlider = new Slider(0, 10, difficulty);
        difficultySlider.setOrientation(Orientation.HORIZONTAL);
        difficultySlider.setShowTickLabels(true);
        difficultySlider.setMajorTickUnit(2.5);
        difficultySlider.setOnMouseReleased(e -> {
            difficulty = (int) difficultySlider.getValue();
            contentPane.setCenter(sudokuGrid);
            menuOpen = false;
        });

        editMenu = new ListView<>();
        editMenu.setOnMouseClicked(e -> {
            String action = editMenu.getSelectionModel().getSelectedItem();

            switch (action) {
                case EDIT_BG_COLOR:
                    showBgMenu();
                    break;
                case EDIT_FG_COLOR:
                    showNumMenu();
                    break;
                case EDIT_DIFFICULTY:
                    showDifficultyMenu();
                    break;
            }
        });

        editMenu.getItems().add(EDIT_BG_COLOR);
        editMenu.getItems().add(EDIT_FG_COLOR);
        editMenu.getItems().add(EDIT_DIFFICULTY);

        loadMenu = new ListView<>();
        loadMenu.setPadding(Insets.EMPTY);
        loadMenu.setOnMouseClicked(e -> loadGame(loadMenu.getSelectionModel().getSelectedItem()));

        newButton.setOnAction(e -> {
            setup = true;

            loadArray(empty);

            byte[][] sudoku = Generator.generate(30 + (10 - difficulty) * 3);
            loadArray(sudoku);
            replayField = sudoku;

            setup = false;
        });

        restartButton.setOnAction(e -> {
            setup = true;

            loadArray(replayField);

            setup = false;
        });

        setup = false;
    }

    void postInitialize() {
        Scene s = Main.scene;

        s.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> saveButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN), () -> loadButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), () -> restartButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN), () -> editButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN), () -> solveButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> newButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN), () -> ((Stage) s.getWindow()).close());
    }

    private boolean isSettingUp() {
        return setup;
    }

    private void showDifficultyMenu() {
        contentPane.setCenter(difficultySlider);
    }

    private void showNumMenu() {
        contentPane.setCenter(numColorPicker);
    }

    private void showBgMenu() {
        contentPane.setCenter(bgColorPicker);
    }

    @FXML
    public void showEditMenu() {
        if (menuOpen) contentPane.setCenter(sudokuGrid);
        else contentPane.setCenter(editMenu);

        menuOpen = !menuOpen;
    }

    @FXML
    public void showLoadMenu() {
        File saveDir = new File("saves");

        loadMenu.getItems().clear();

        //noinspection ConstantConditions
        for (File img : saveDir.listFiles(File::isFile)) {
            if (img.getName().endsWith(".png")) {
                try {
                    BufferedInputStream is = new BufferedInputStream(new FileInputStream(img));

                    Image image = new Image(is);
                    ImageView view = new ImageView(image);
                    view.setId(img.getName().substring(0, img.getName().length() - 4));
                    System.out.println(view.getId());

                    loadMenu.getItems().add(view);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        contentPane.setCenter(loadMenu);
    }

    @FXML
    public void solveSudoku() {
        byte[][] solution = checkSudoku();

        if (solution == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Sudoku is unsolvable!");
            alert.setContentText("The sudoku has either no or too many possible solutions!");
            alert.showAndWait();
        } else {
            loadArray(solution);
        }
    }

    private void loadArray(byte[][] sudoku) {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                sudokuSquares[x][y].setText(String.valueOf(sudoku[x][y]));
            }
        }
    }

    private byte[][] checkSudoku() {
        byte[][] values = new byte[9][9];

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                String text = sudokuSquares[x][y].getText();
                if (text.equals("")) values[x][y] = 0;
                else values[x][y] = Byte.valueOf(text);
            }
        }

        Solver.solve(values);

        if (values[0][0] == -1) return null;
        return values;
    }

    private void loadGame(ImageView selectedItem) {
        setup = true;

        String csvName = "saves/" + selectedItem.getId() + ".csv";

        List<String> csvLines = new ArrayList<>(9);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvName));

            String line;
            while ((line = reader.readLine()) != null) {
                csvLines.add(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[][] loaded = new byte[9][9];

        for (int line = 0; line < 9; line++) {
            String[] fields = csvLines.get(line).split("[ ]*,[ ]*");

            for (int column = 0; column < 9; column++) {
                loaded[column][line] = Byte.valueOf(fields[column]);
                sudokuSquares[column][line].setText(fields[column]);
                if(!fields[column].equals("0")) sudokuSquares[column][line].getStyleClass().add("givenNumber");
            }
        }

        replayField = loaded;

        setup = false;
        contentPane.setCenter(sudokuGrid);
    }

    @FXML
    public void saveGame() {
        StringBuilder csvBuilder = new StringBuilder();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (!sudokuSquares[x][y].getText().equals(""))csvBuilder.append(sudokuSquares[x][y].getText());
                else csvBuilder.append("0");

                if (x != 8)
                    csvBuilder.append(", ");
            }
            if (y != 8)
                csvBuilder.append('\n');
        }

        String csvText = csvBuilder.toString();

        SimpleDateFormat formatter = new SimpleDateFormat("d.M.y H-m-s");
        String filename = formatter.format(Date.from(Instant.now()));

        File csvFile = new File("saves/" + filename + ".csv");


        try {
            //noinspection ResultOfMethodCallIgnored
            csvFile.getParentFile().mkdir();
            //noinspection ResultOfMethodCallIgnored
            csvFile.createNewFile();

            PrintWriter writer = new PrintWriter(csvFile);
            writer.write(csvText);
            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Saved!");
            alert.setContentText("Your game is now found in the load menu.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sudokuGrid.snapshot(param -> {
            File jpgFile = new File("saves/" + filename + ".png");

            WritableImage img = param.getImage();

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(img, null), "PNG", jpgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }, new SnapshotParameters(), null);
    }
}
