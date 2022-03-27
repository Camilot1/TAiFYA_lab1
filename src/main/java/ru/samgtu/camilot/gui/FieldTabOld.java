package ru.samgtu.camilot.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.enums.EnumDirection;
import ru.samgtu.camilot.managers.FileManager;
import ru.samgtu.camilot.objects.Bot;
import ru.samgtu.camilot.objects.ScreenShot;
import ru.samgtu.camilot.objects.Token;
import ru.samgtu.camilot.ulits.vectors.DoubleVector2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FieldTabOld extends GuiObject {

    private final AnchorPane scrollRoot;

    private final MenuButton mbField;
    private final MenuButton mbPathLSA;
    private final Label labelStatus;
    private final TextField tfDelay;
    private boolean isPlaying = false;

    private Bot bot;
    private Tile startTile;
    private Tile endTile;
    private Tile[][] tiles;

    private List<Token> tokens;

    private static int currentScreenShot;

    private final List<ScreenShot> screenShots;

    public FieldTabOld(DoubleVector2 position) {
        super(position, new DoubleVector2(840, 800));
        ScrollPane scrollPane = GuiConstructor.createScrollPane(scrollRoot = new AnchorPane(), 0, 125, 750, 600);
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
                GuiConstructor.createButton(e -> clear(), "Сброс", 355, 55, 140),
                labelStatus = GuiConstructor.createLabel("Статус:", 15, 95, 700),
                GuiConstructor.createLabel("Время одного кадра:", 510, 19, 140, Pos.CENTER),
                tfDelay = GuiConstructor.createTextField("50", 655, 14, 60),
                GuiConstructor.createLabel("ms", 720, 19, 30, Pos.CENTER),
                GuiConstructor.createButton(e -> firstScreenShot(),"<<", 510, 60, 45),
                GuiConstructor.createButton(e -> prevScreenShot(),"<", 560, 60, 45),
                GuiConstructor.createButton(e -> play(), "p", 610, 60, 45),
                GuiConstructor.createButton(e -> nextScreenShot(false),">", 660, 60, 45),
                GuiConstructor.createButton(e -> lastScreenShot(),">>", 710, 60, 45)
        );

        for (String item : getFiles("data\\field\\")) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(e -> {
                mbField.setText(menuItem.getText());
                loadFieldFromFile("data\\field\\" + menuItem.getText());
            });
            mbField.getItems().add(menuItem);
        }

    }

    /**
     * Метод обновления MenuItems с названиями файлов в MenuButton
     * @param menuButton кнопка
     * @param filePath путь до папки с файлами
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
        //bot.loadScreenShot(screenShots.get(currentScreenShot));
    }

    private void prevScreenShot() {
        if (isPlaying) return;
        if (currentScreenShot > 0) currentScreenShot--;
        if (screenShots == null || screenShots.size() <= currentScreenShot) return;
        //bot.loadScreenShot(screenShots.get(currentScreenShot));
    }

    private void play() {
        firstScreenShot();
        int[] counter = new int[1];
        Timeline timeline = new Timeline (new KeyFrame(Duration.millis(getPlayFrameDelay()), ae -> {
            nextScreenShot(true);
            counter[0]--;
            if (counter[0] == 0) isPlaying = false;
            System.out.println(counter[0]);
        }));
        timeline.setCycleCount(screenShots.size()); //Ограничим число повторений
        counter[0] = screenShots.size();
        isPlaying = true;
        timeline.play();
    }

    private void nextScreenShot(boolean ignoreIsPlaying) {
        if (!ignoreIsPlaying) if (isPlaying) return;
        if (screenShots == null) return;
        if (currentScreenShot + 1 < screenShots.size()) currentScreenShot++;
        //if (screenShots.size() > 0) bot.loadScreenShot(screenShots.get(currentScreenShot));
    }

    private void lastScreenShot() {
        if (isPlaying) return;
        if (screenShots == null || screenShots.size() == 0) return;
        currentScreenShot = screenShots.size()-1;
        //bot.loadScreenShot(screenShots.get(currentScreenShot));
    }

    public int getPlayFrameDelay() {
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
        if (isPlaying) return;


        updateStatus("");
        if (!FileManager.isFileExist("data\\field\\" + mbField.getText()) || !FileManager.isFileExist("data\\pathLSA\\" + mbPathLSA.getText())) {
            updateStatus("Пожалуйста, выберите корректные файлы лабиринта и алгоритма поиска пути!");
            return;
        }

        if (mbField.getText().equals("")) {
            updateStatus("Пожалуйста, выберите файл с лабиринтом.");
            return;
        }
        if (mbPathLSA.getText().equals("")) {
            updateStatus("Пожалуйста, выберите файл с ЛСА.");
            return;
        }

        List<String> data =  FileManager.readList("data\\pathLSA\\" + mbPathLSA.getText());
        if (data.size() == 0) {
            updateStatus("В файле выбранной ЛСА отсутствуют данные!");
            return;
        }


        /*
        clear();
        try {
            tokens = Parser.parseTokenString(data.get(0), true);
            bot.setTokens(tokens);
            bot.setScreenShots(screenShots);
            bot.makeScreenShot();

            bot.makeFirstStep();

        } catch (Exception e) {
            updateStatus(e.getMessage());
        }
        
         */
    }

    public void clear() {
        if (isPlaying) return;
        String filePath = "data\\field\\" + mbField.getText();
        loadFieldFromFile(filePath);
        screenShots.clear();
        updateStatus("Очищено.");
    }

    /**
     * Метод загрузки данных о тайлах из файла данных
     * @param filePath путь до файла с данными
     */
    private void loadFieldFromFile(String filePath) {
        List<String> data = FileManager.readList(filePath);
        //updateFieldSize(data);
        //loadTiles(data);
        loadBot();
    }

    public void hideBot() {
        if (bot != null) scrollRoot.getChildren().remove(bot.getRoot());
    }
    public void showBot() {
        if (bot != null) scrollRoot.getChildren().add(bot.getRoot());
    }

    /**
     * Метод загрузки данных о боте из файла данных
     */
    private void loadBot() {
        if (bot == null) bot = new Bot(null, EnumDirection.RIGHT, startTile.getXyIndexes());

        scrollRoot.getChildren().remove(bot.getRoot());
        //bot.setPosition(startTile.getPosition());
        scrollRoot.getChildren().add(bot.getRoot());

    }


    /**
     * Метод обновления статуса
     * @param message сообщение
     */
    public void updateStatus(String message) {
        labelStatus.setText("Статус: " + message);
    }

    public Bot getBot() {
        return bot;
    }

}
