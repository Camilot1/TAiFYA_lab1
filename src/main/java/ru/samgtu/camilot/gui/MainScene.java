package ru.samgtu.camilot.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ru.samgtu.camilot.Controller;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.Modeller;
import ru.samgtu.camilot.Parser;
import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumTokenType;
import ru.samgtu.camilot.managers.FileManager;
import ru.samgtu.camilot.objects.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainScene {

    private final Scene scene;
    private final Field field;
    private final AnchorPane root;

    private final TextField textTF;
    public final TextField inputTF;
    public final TextField outputTF;
    private final MenuButton typeMB;
    private final MenuButton fileMB;
    private final Button nextStepBTN;
    private final Label statusLabel;

    public MainScene() {
        scene = new Scene(root = new AnchorPane());

        field = new Field(760, 0);

        root.getChildren().addAll(
                GuiConstructor.createLabel("Уравнение:", 10, 10, 360),
                textTF = GuiConstructor.createTextField(10, 35, 420),
                typeMB = GuiConstructor.createMenuButton(EnumCalculateType.getFirstType(), EnumCalculateType.getValuesAsList(), 440, 35, 100),
                GuiConstructor.createButton(e -> calculate(textTF.getText()),"Обработать",  550, 35, 140),
                GuiConstructor.createLabel("Файл с данными:", 10, 70, 360),
                fileMB = GuiConstructor.createMenuButton(getFiles(), 10, 95, 280),
                GuiConstructor.createButton(e -> updateFiles(), "↻", 300, 95, 25),
                GuiConstructor.createButton(e -> loadFile(), "Загрузить из файла", 340, 95, 200),
                statusLabel = GuiConstructor.createLabel("Статус: ", 10, 135, 700),
                GuiConstructor.createLabel("Входные данные:", 10, 175, 180),
                GuiConstructor.createLabel("Выходные данные:", 210, 175, 180),
                inputTF = GuiConstructor.createTextField("", 10, 200, 180),
                outputTF = GuiConstructor.createTextField("", false,210, 200, 400),
                nextStepBTN = GuiConstructor.createButton(e -> {}, "Следующий шаг", 10, 240, 180),

                field.getRoot()
        );

    }

    /**
     * Метод установки функции, выполняющейся при нажатии кнопки "Следующий шаг
     * @param e функция
     */
    public void setNextStepFunc(EventHandler<ActionEvent> e) {
        nextStepBTN.setOnAction(e);
    }

    public void calculate(String text) {
        try {
            List<Token> tokens = parseData(text);

            EnumCalculateType calculateType = EnumCalculateType.getEnumByType(typeMB.getText());
            Modeller.model(tokens, calculateType);
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus(e.getMessage());
        }
    }

    public List<Token> parseData(String text) throws Exception {
        List<Token> tokens = Parser.parseTokenString(text, true);

        for (Token token: tokens) {
            if (token.getType() != EnumTokenType.W) Controller.addToken(token.toString(), token);
        }

        Parser.checkTokenList(tokens);
        for (Token token: tokens) {
            System.out.println(token);
        }
        return tokens;
    }

    /**
     * Метод загрузки ЛСА из файла
     */
    public void loadFile() {
        if (fileMB.getText().equals("")) return;
        if (!FileManager.isFileExist("data\\lsa\\" + fileMB.getText())) return;

        List<String> list = FileManager.readList("data\\lsa\\" + fileMB.getText());
        if (list.size() > 0) {
            textTF.setText(list.get(0));
            try {
                List<Token> tokens = Parser.parseTokenString(list.get(0), true);
                updateStatus("Введите " + Parser.getVarsCount(tokens) + " логических значения."); ;
            } catch (Exception e) {
                e.printStackTrace();
                updateStatus(e.getMessage());
            }
        }
    }

    /**
     * Метод обновления списка файлов
     */
    public void updateFiles() {
        fileMB.setText("");
        GuiConstructor.modifyMenuItemList(fileMB, getFiles());
    }

    /**
     * Метод получения списка файлов
     * @return список файлов
     */
    public List<String> getFiles() {
        List<String> list = new ArrayList<>();
        for (File file: FileManager.getFilesInDir("data\\lsa\\"))  list.add(file.getName());
        return list;
    }

    public Scene getScene() {
        return scene;
    }

    /**
     * Метод обновления статуса
     * @param message сообщение
     */
    public void updateStatus(String message) {
        statusLabel.setText("Статус: " + message);
    }
}
