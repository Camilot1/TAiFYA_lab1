package ru.samgtu.camilot.tabs;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.Main;
import ru.samgtu.camilot.Parser;
import ru.samgtu.camilot.Validator;
import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumPath;
import ru.samgtu.camilot.managers.FileManager;
import ru.samgtu.camilot.objects.Token;

import java.io.File;
import java.util.List;

public class LSATab extends Tab {

    private final AnchorPane root;

    private final TextField tfText;
    public final TextField tfInput;
    public final TextArea taOutput;
    private final MenuButton mbType;
    private final MenuButton mbFile;
    private final Button btnNextStep;
    private final Label labelStatus;

    public LSATab() {
        super("Моделирование ЛСА");
        setClosable(false);
        setContent(root = new AnchorPane());

        root.getChildren().addAll(
                GuiConstructor.createLabel("Уравнение:", 10, 10, 360),
                tfText = GuiConstructor.createTextField(10, 35, 420),
                mbType = GuiConstructor.createMenuButton(EnumCalculateType.getFirstType(), EnumCalculateType.getShowToUserValues(), 440, 35, 100),
                GuiConstructor.createButton(e -> calculate(tfText.getText()),"Обработать",  550, 35, 140),
                GuiConstructor.createButton(e -> clear(),"Очистить",  550, 95, 140),
                GuiConstructor.createLabel("Файл с данными:", 10, 70, 360),
                mbFile = GuiConstructor.createMenuButton(getFiles(), 10, 95, 280),
                GuiConstructor.createButton(e -> {
                    mbFile.setText("");
                    getFiles();
                }, "↻", 300, 95, 25),
                GuiConstructor.createButton(e -> loadFile(), "Загрузить из файла", 340, 95, 200),
                labelStatus = GuiConstructor.createLabel("Статус: ", 10, 135, 700),
                GuiConstructor.createLabel("Входные данные:", 10, 175, 180),
                GuiConstructor.createLabel("Выходные данные:", 210, 175, 180),
                tfInput = GuiConstructor.createTextField("", 10, 200, 180),
                taOutput = GuiConstructor.createTextArea(false,210, 200, 400, 300),
                btnNextStep = GuiConstructor.createButton(e -> nextStep(), "Следующий шаг", 10, 240, 180)
                //GuiConstructor.createButton(e -> Main.getThreadModeller().play(), "PLAY", 0, 0, 50)
        );
    }

    private void calculate(String lsa) {
        try {
            String validatedLSA = Validator.validateString(lsa);
            List<Token> tokens = Parser.parseTokenString(validatedLSA, true);

            
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus(e.getMessage());
        }
    }

    private void loadFile() {
        if (mbFile.getText().equals("")) return;
        if (!FileManager.isFileExist(EnumPath.LSA.getPath() + mbFile.getText())) return;

        List<String> list = FileManager.readList(EnumPath.LSA.getPath() + mbFile.getText());
        if (list.size() > 0) {
            tfText.setText(list.get(0));
        } else tfText.setText("");
    }

    private void nextStep() {

    }

    private void clear() {
        tfText.setText("");
    }

    private File[] getFiles() {
        return FileManager.getFilesInDir(EnumPath.LSA.getPath());
    }

    private void updateStatus(String message) {
        labelStatus.setText("Статус: " + message);
    }
}