package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    static final String EDIT_DIFFICULTY = "Change Difficulty";
    static final String EDIT_BG_COLOR = "Edit Background";
    static final String EDIT_FG_COLOR = "Edit Foreground";

    boolean menuOpen = false;
    int difficulty = 3;

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

    ListView<String> editMenu;
    ListView<ImageView> loadMenu;

    ColorPicker bgColorPicker;
    ColorPicker numColorPicker;

    Slider difficultySlider;

    @FXML
    BorderPane contentPane;

    TextField[][] sudokuSquares;

    @FXML
    public void initialize() {
        sudokuSquares = new TextField[9][9];

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                TextField sudokuSquare = new TextField("");
                sudokuSquares[x][y] = sudokuSquare;

                sudokuSquare.textProperty().addListener((o, oldVal, newVal) -> {
                    if (newVal.length() > 1) {
                        newVal = newVal.substring(0, 1);
                        sudokuSquare.setText(newVal);
                    }

                    if (!newVal.matches("[0-9]")) sudokuSquare.setText("");
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
            Color bgColor = bgColorPicker.getValue();

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
            Color bgColor = numColorPicker.getValue();

            for (TextField[] line : sudokuSquares) {
                for (TextField square : line) {
                    square.setStyle(square.getStyle().concat(String.format("-fx-text-fill: rgb(%d, %d, %d); -fx-border-color: rgb(%d, %d, %d);",
                            (int)(bgColor.getRed()*255), (int)(bgColor.getGreen()*255), (int)(bgColor.getBlue()*255),
                            (int)(bgColor.getRed()*255), (int)(bgColor.getGreen()*255), (int)(bgColor.getBlue()*255))));
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
        loadMenu.setOnMouseClicked(e -> loadGame(loadMenu.getSelectionModel().getSelectedItem()));
    }

    public void postInitialize() {
        Scene s = Main.scene;

        s.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> saveButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN), () -> loadButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), () -> restartButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN), () -> editButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN), () -> solveButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> newButton.fire());
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN), () -> ((Stage) s.getWindow()).close());
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

    public void loadGame(ImageView selectedItem) {
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

        for (int line = 0; line < 9; line++) {
            String[] fields = csvLines.get(line).split(", ");

            for (int column = 0; column < 9; column++) {
                sudokuSquares[line][column].setText(fields[column]);
                if(!fields[column].equals("")) sudokuSquares[line][column].getStyleClass().add("givenNumber");
            }
        }

        contentPane.setCenter(sudokuGrid);
    }

    @FXML
    public void saveGame() {
        StringBuilder csvBuilder = new StringBuilder();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                csvBuilder.append(sudokuSquares[x][y].getText());

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
            csvFile.getParentFile().mkdir();
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

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(param.getImage(), null), "PNG", jpgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }, new SnapshotParameters(), null);
    }
}
