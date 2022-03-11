package ru.samgtu.camilot.gui;

import com.sun.javafx.geom.Vec2d;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.Parser;
import ru.samgtu.camilot.enums.EnumDirection;
import ru.samgtu.camilot.enums.EnumTileType;
import ru.samgtu.camilot.managers.FileManager;
import ru.samgtu.camilot.objects.ScreenShot;
import ru.samgtu.camilot.objects.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Field {

    private final AnchorPane root;
    private final ScrollPane scrollPane;
    private final AnchorPane scrollRoot;

    private final MenuButton mbField;
    private final MenuButton mbPathLSA;
    private final Label labelStatus;
    private final TextField tfDelay;

    private Bot bot;
    private Tile startTile;
    private Tile endTile;
    private Tile[][] tiles;

    private static int counter = 0;

    private static int currentScreenShot;
    private static boolean isPlaying;

    private final List<ScreenShot> screenShots;

    public Field(double x, double y) {
        root = GuiConstructor.createAnchorPane(x, y, 840, 800);
        scrollPane = GuiConstructor.createScrollPane(scrollRoot = new AnchorPane(), 0, 125, 750, 600);
        screenShots = new ArrayList<>();

        root.getChildren().addAll(
                scrollPane,
                GuiConstructor.createLabel("Лабиринт:", 15, 20, 70),
                mbField = GuiConstructor.createMenuButton(85, 15, 220),
                GuiConstructor.createButton(e -> update(mbField, "data\\field\\"), "↻", 315, 15, 25),
                GuiConstructor.createLabel("Алгоритм:", 15, 60, 70),
                mbPathLSA = GuiConstructor.createMenuButton(getFiles("data\\pathLSA\\"), 85, 55, 220),
                GuiConstructor.createButton(e -> update(mbPathLSA, "data\\pathLSA\\"), "↻", 315, 55, 25),
                GuiConstructor.createButton(e -> start(), "Старт", 355, 15, 140),
                labelStatus = GuiConstructor.createLabel("Статус:", 15, 95, 700),
                GuiConstructor.createLabel("Время одного кадра:", 510, 19, 140, Pos.CENTER),
                tfDelay = GuiConstructor.createTextField("50", 655, 14, 60),
                GuiConstructor.createLabel("ms", 720, 19, 30, Pos.CENTER),
                GuiConstructor.createButton(e -> firstScreenShot(),"<<", 510, 60, 40),
                GuiConstructor.createButton(e -> prevScreenShot(),"<", 560, 60, 40),
                GuiConstructor.createButton(e -> play(), "p", 610, 60, 40),
                GuiConstructor.createButton(e -> nextScreenShot(),">", 660, 60, 40),
                GuiConstructor.createButton(e -> lastScreenShot(),">>", 710, 60, 40)
        );

        for (String item : getFiles("data\\field\\")) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(e -> {
                mbField.setText(menuItem.getText());
                loadFieldFromFile("data\\field\\" + menuItem.getText());
            });
            mbField.getItems().add(menuItem);
        }
        //System.out.println(start(FileManager.readList("data\\lsa\\Алгоритм левой руки.data").get(0)));

    }

    /**
     * Метод обновления списка файлов
     */
    public void update(MenuButton menuButton, String filePath) {
        menuButton.setText("");
        GuiConstructor.modifyMenuItemList(menuButton, getFiles(filePath));
    }

    /**
     * Метод получения списка файлов
     * @return список файлов
     */
    public List<String> getFiles(String filePath) {
        List<String> list = new ArrayList<>();
        for (File file: FileManager.getFilesInDir(filePath))  list.add(file.getName());
        return list;
    }

    private void firstScreenShot() {
        if (isPlaying) return;
        if (screenShots == null || screenShots.size() == 0) return;
        currentScreenShot = 0;
        bot.loadScreenShot(screenShots.get(currentScreenShot));
    }

    private void prevScreenShot() {
        if (isPlaying) return;
        if (currentScreenShot > 0) currentScreenShot--;
        if (screenShots == null || screenShots.size() <= currentScreenShot) return;
        bot.loadScreenShot(screenShots.get(currentScreenShot));
    }

    private void play() {
        firstScreenShot();
        isPlaying = true;
        Timeline timeline = new Timeline (
                new KeyFrame(
                        Duration.millis(getPlayFrameDelay()), //1000 мс * 60 сек = 1 мин
                        ae -> {
                            isPlaying = false;
                            nextScreenShot();
                            isPlaying = true;
                        }
                )
        );
        timeline.setCycleCount(screenShots.size()); //Ограничим число повторений
        timeline.play();
        isPlaying = false;
    }

    private void nextScreenShot() {
        if (isPlaying) return;
        if (screenShots == null) return;
        if (currentScreenShot + 1 < screenShots.size()) currentScreenShot++;
        bot.loadScreenShot(screenShots.get(currentScreenShot));
    }

    private void lastScreenShot() {
        if (isPlaying) return;
        if (screenShots == null || screenShots.size() == 0) return;
        currentScreenShot = screenShots.size()-1;
        bot.loadScreenShot(screenShots.get(currentScreenShot));
    }

    public void makeScreenShot() {
        screenShots.add(new ScreenShot(bot.getPosition(), bot.getDirection()));
    }

    private int getPlayFrameDelay() {
        int ms = 0;
        try {
            ms = Integer.parseInt("0" + tfDelay.getText());
        } catch (Exception e) {
            tfDelay.setText("10");
        }

        if (ms < 10) ms = 10;
        return ms;
    }

    private void start() {
        updateStatus("");
        if (!FileManager.isFileExist("data\\field\\" + mbField.getText()) || !FileManager.isFileExist("data\\pathLSA\\" + mbPathLSA.getText())) {
            updateStatus("Пожалуйста, выберите корректные файлы лабиринта и алгоритма поиска пути!");
            return;
        }

        List<String> data =  FileManager.readList("data\\pathLSA\\" + mbPathLSA.getText());
        if (data.size() == 0) {
            updateStatus("В файле выбанного лабиринта отсутствуют данные!");
            return;
        }


        screenShots.clear();
        makeScreenShot();
        try {
            List<Token> list = Parser.parseTokenString(data.get(0), true);

            bot.nextStep(list);
            if (bot.getPosition().equals(startTile.getPosition())) updateStatus("Путь не найден. Бот вернулся в начало");
            else if (bot.getPosition().equals(endTile.getPosition())) updateStatus("Путь найден. Вы можете посмотреть повтор прохождения лабиринта с помощью кнопок справа.");
        } catch (Exception e) {
            updateStatus(e.getMessage());
        }
    }

    /**
     * Метод загрузки данных о тайлах из файла данных
     * @param filePath путь до файла с данными
     */
    private void loadFieldFromFile(String filePath) {
        List<String> data = FileManager.readList(filePath);
        updateFieldSize(data);
        loadTiles(data);
        loadBot();
    }

    /**
     * Метод загрузки данных о боте из файла данных
     */
    private void loadBot() {
        if (bot == null) bot = new Bot(this, EnumDirection.RIGHT, startTile.getPosition());

        scrollRoot.getChildren().remove(bot.getRoot());
        bot.setPosition(startTile.getPosition());
        scrollRoot.getChildren().add(bot.getRoot());

    }

    /**
     * Метод загрузки типов тайлов из списка строк данных
     * @param data построчный список данных
     */
    private void loadTiles(List<String> data) {
        for (int y = 0; y < data.size(); y++) {
            for (int x = 0; x < data.get(y).length(); x++) {
                EnumTileType type = EnumTileType.getEnumByChar(data.get(y).charAt(x));
                if (type == null) type = EnumTileType.EMPTY;
                else if (type == EnumTileType.START) startTile = tiles[x][y];
                else if (type == EnumTileType.END) endTile = tiles[x][y];
                tiles[x][y].setType(type);
            }
        }

    }

    /**
     * Метод очистки всех тайлов от типов (установка типа EMPTY)
     */
    private void clearTiles() {
        for(Tile[] tilesArr: tiles) {
            for(Tile tile: tilesArr) {
                tile.setType(EnumTileType.EMPTY);
            }
        }
    }

    /**
     * Метод-прослойка для обновления размера поля по списку строк данных
     * @param data список строк данных
     */
    private void updateFieldSize(List<String> data) {
        updateFieldSize(getMaxX(data), data.size());
    }

    /**
     * Метод поиска максимального значения X (ширины поля) по списку строк данных
     * @param data список строк данных
     * @return максимальный X (ширина поля)
     */
    private int getMaxX(List<String> data) {
        if (data == null || data.size() == 0) return 0;
        int maxX = -1;

        for (String s: data) {
            if (maxX < s.length()) maxX = s.length();
        }
        return maxX;
    }

    /**
     * Метод обновления размера поля в тайлах.
     * Если какие-либо тайлы уже существует, очищает и их отображение тоже
     * @param x ширина поля
     * @param y высота поля
     */
    private void updateFieldSize(int x, int y) {
        if (tiles != null) {
            for (int iX = 0; iX < x; iX++) {
                for (int iY = 0; iY < y; iY++) {
                    scrollRoot.getChildren().remove(tiles[iX][iY].getRectangle());
                }
            }
        }
        tiles = new Tile[x][y];
        for (int iX = 0; iX < x; iX++) {
            for (int iY = 0; iY < y; iY++) {
                tiles[iX][iY] = new Tile(EnumTileType.EMPTY, new Vec2d(iX, iY));
                scrollRoot.getChildren().add(tiles[iX][iY].getRectangle());
            }
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public AnchorPane getRoot() {
        return root;
    }

    /**
     * Метод обновления статуса
     * @param message сообщение
     */
    public void updateStatus(String message) {
        labelStatus.setText("Статус: " + message);
    }

}
