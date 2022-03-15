package ru.samgtu.camilot.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import ru.samgtu.camilot.*;
import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumTokenType;
import ru.samgtu.camilot.managers.FileManager;
import ru.samgtu.camilot.objects.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainScene {

    private final Scene scene;
    private final Field field;
    private final AnchorPane root;

    private ThreadModeller threadModeller;

    private boolean waitForNewValue = false;
    private boolean waitForNewValues = false;
    private int waitedNewValuesSize = -1;

    private final TextField textTF;
    public final TextField inputTF;
    public final TextArea outputTA;
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
                GuiConstructor.createButton(e -> clear(),"Очистить",  550, 95, 140),
                GuiConstructor.createLabel("Файл с данными:", 10, 70, 360),
                fileMB = GuiConstructor.createMenuButton(getFiles(), 10, 95, 280),
                GuiConstructor.createButton(e -> updateFiles(), "↻", 300, 95, 25),
                GuiConstructor.createButton(e -> loadFile(), "Загрузить из файла", 340, 95, 200),
                statusLabel = GuiConstructor.createLabel("Статус: ", 10, 135, 700),
                GuiConstructor.createLabel("Входные данные:", 10, 175, 180),
                GuiConstructor.createLabel("Выходные данные:", 210, 175, 180),
                inputTF = GuiConstructor.createTextField("", 10, 200, 180),
                outputTA = GuiConstructor.createTextArea(false,210, 200, 400, 300),
                nextStepBTN = GuiConstructor.createButton(e -> nextStep(), "Следующий шаг", 10, 240, 180),
                GuiConstructor.createButton(e -> Main.getThreadModeller().play(), "PLAY", 0, 0, 50),

                field.getRoot()
        );

    }

    public void setThreadModeller(ThreadModeller threadModeller) {
        this.threadModeller = threadModeller;
    }

    /**
     * Метод установки функции, выполняющейся при нажатии кнопки "Следующий шаг
     * @param e функция
     */
    public void setNextStepFunc(EventHandler<ActionEvent> e) {
        nextStepBTN.setOnAction(e);
    }

    public void nextStep() {
        if (!waitForNewValue && !waitForNewValues) return;

        if (waitForNewValue) {
            try {
                boolean bool = Validator.parseBoolean(inputTF.getText());
                setNewValue(bool);
                inputTF.setText("");
                waitForNewValue = false;
            } catch (Exception e) {
                updateStatus("Введите корректное логическое значение для продолжения работы.");
            }
        } else if (waitForNewValues) {
            try {
                boolean[] booleans = Validator.parseBooleans(inputTF.getText(), waitedNewValuesSize);
                setNewValues(booleans);
                waitForNewValues = false;
            } catch (Exception e) {
                updateStatus("Введите N логических значений для продолжения работы.");
            }
        }
    }

    public void clear() {
        textTF.clear();
        inputTF.clear();
        outputTA.clear();
    }

    public void calculate(String text) {
        if (waitForNewValue) return;
        outputTA.setText("");

        try {
            List<Token> tokens = parseData(text);

            EnumCalculateType calculateType = EnumCalculateType.getEnumByType(typeMB.getText());
            assert calculateType != null;
            threadModeller.addModelData(tokens, calculateType);
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus(e.getMessage());
        }
    }

    public List<Token> parseData(String text) throws Exception {
        List<Token> tokens = Parser.parseTokenString(text, true);
        Parser.checkTokenList(tokens);
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

    public void waitForNewCommonValues(int size) {
        updateStatus("Пожалуйста, введите " + size + " логических значений.");
        waitForNewValues = true;
        this.waitedNewValuesSize = size;
    }

    public void waitForNewValues(int size) {
        updateStatus("Бесконечный цикл. Введите новые значения.");
        waitForNewValues = true;
        this.waitedNewValuesSize = size;
    }

    public void waitForNewValue(Token token) {
        updateStatus("Введите одно логическое значение для токена " + token.toString());
        waitForNewValue = true;
    }

    public void setNewValue(boolean value) {
        Main.getThreadModeller().setNewValue(value, true);
    }

    public void setNewValues(boolean[] values) {
        Main.getThreadModeller().setNewValues(values, true);
    }

    public boolean getWaitForNewValue() {
        return waitForNewValue;
    }
    public boolean getWaitForNewValues() {
        return waitForNewValues;
    }

    public void playThreadModeller() {
        Main.getThreadModeller().play();
    }

    public Field getField() {
        return field;
    }
}
