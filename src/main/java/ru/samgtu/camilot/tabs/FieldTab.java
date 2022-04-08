package ru.samgtu.camilot.tabs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.Parser;
import ru.samgtu.camilot.gui.Field;
import ru.samgtu.camilot.managers.FileManager;
import ru.samgtu.camilot.objects.Bot;
import ru.samgtu.camilot.objects.Token;
import ru.samgtu.camilot.ulits.vectors.DoubleVector2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FieldTab extends Tab {

     private final Field field;

    private final MenuButton mbField;
    private final MenuButton mbPathLSA;
    private final Label labelStatus;
    private final TextField tfDelay;

    public FieldTab() {
        super("Лабиринт");
        setClosable(false);
        AnchorPane root;
        setContent(root = new AnchorPane());

        field = new Field(this, new DoubleVector2(10, 130), new DoubleVector2(700, 700));

        root.getChildren().addAll(
                field.getScrollPane(),
                GuiConstructor.createLabel("Лабиринт:", 8, 20, 80),
                mbField = GuiConstructor.createMenuButton(85, 15, 220),
                GuiConstructor.createButton(e -> updateFiles(mbField, "data\\field\\"), "↻", 315, 15, 25),
                GuiConstructor.createLabel("Алгоритм:", 8, 60, 80),
                mbPathLSA = GuiConstructor.createMenuButton(getFiles("data\\pathLSA\\"), 85, 55, 220),
                GuiConstructor.createButton(e -> updateFiles(mbPathLSA, "data\\pathLSA\\"), "↻", 315, 55, 25),
                GuiConstructor.createButton(e -> start(), "Старт", 355, 15, 140),
                //GuiConstructor.createButton(e -> clear(), "Сброс", 355, 55, 140),
                labelStatus = GuiConstructor.createLabel("Статус:", 15, 95, 800),
                GuiConstructor.createLabel("Время одного кадра:", 505, 19, 150, Pos.CENTER),
                tfDelay = GuiConstructor.createTextField("50", 655, 14, 60),
                GuiConstructor.createLabel("ms", 720, 19, 30, Pos.CENTER),
                GuiConstructor.createButton(e -> field.firstScreenShot(),"<<", 510, 60, 45),
                GuiConstructor.createButton(e -> field.prevScreenShot(),"<", 560, 60, 45),
                GuiConstructor.createButton(e -> field.play(), "p", 610, 60, 45),
                GuiConstructor.createButton(e -> field.nextScreenShot(false),">", 660, 60, 45),
                GuiConstructor.createButton(e -> field.lastScreenShot(),">>", 710, 60, 45)
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
    public void updateFiles(MenuButton menuButton, String filePath) {
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
        if (field.isPlaying) return;

        updateStatus("");
        if (!FileManager.isFileExist("data\\field\\" + mbField.getText()) || !FileManager.isFileExist("data\\pathLSA\\" + mbPathLSA.getText())) {
            updateStatus("Пожалуйста, выберите корректные файлы лабиринта и алгоритма поиска пути!");
            return;
        }

        if (mbField.getText().equals("")) { updateStatus("Пожалуйста, выберите файл с лабиринтом."); return; }
        if (mbPathLSA.getText().equals("")) { updateStatus("Пожалуйста, выберите файл с ЛСА."); return; }

        List<String> data =  FileManager.readList("data\\pathLSA\\" + mbPathLSA.getText());
        if (data.size() == 0) { updateStatus("В файле выбранной ЛСА отсутствуют данные!"); return; }


        clear();
        try {
            List<Token> tokens = Parser.parseTokenString(data.get(0), true);
            Bot bot = field.getBot();
            if (bot == null) throw new Exception("Бот не загружен.");
            bot.setScreenShots(new ArrayList<>());
            bot.makeScreenShot();

            bot.start(tokens);

        } catch (Exception e) {
            updateStatus(e.getMessage());
            e.printStackTrace();
        }
    }

    public void clear() {
        if (field.isPlaying) return;
        String filePath = "data\\field\\" + mbField.getText();
        loadFieldFromFile(filePath);
        field.getBot().getScreenShots().clear();
        updateStatus("Очищено.");
    }

    /**
     * Метод загрузки данных о тайлах из файла данных
     * @param filePath путь до файла с данными
     */
    private void loadFieldFromFile(String filePath) {
        List<String> data = FileManager.readList(filePath);
        field.loadField(data);
    }

    public void updateStatus(String message) {
        System.out.println(message);
    }

    public Label getLabelStatus() {
        return labelStatus;
    }

}
