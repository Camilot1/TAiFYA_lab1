package ru.samgtu.camilot.tabs;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.objects.Analyzer;
import ru.samgtu.camilot.objects.Validator;
import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumPath;
import ru.samgtu.camilot.managers.FileManager;
import ru.samgtu.camilot.objects.BooleanPackage;
import ru.samgtu.camilot.objects.Modeller;
import ru.samgtu.camilot.objects.Token;
import ru.samgtu.camilot.objects.TokenPackage;

import java.io.File;
import java.util.List;

public class LSATab extends Tab {

    private volatile Modeller modeller;

    private final TextField tfText;
    public final TextField tfInput;
    public final TextArea taOutput;
    private final Button btnCalculate;
    private final MenuButton mbType;
    private final MenuButton mbFile;
    private final Button btnNextStep;
    private final Label labelStatus;

    public LSATab() {
        super("Моделирование ЛСА");
        setClosable(false);
        AnchorPane root;
        setContent(root = new AnchorPane());

        modeller = new Modeller();
        modeller.setDaemon(true);
        modeller.start();

        root.getChildren().addAll(
                GuiConstructor.createLabel("Уравнение:", 10, 10, 360),
                tfText = GuiConstructor.createTextField(10, 35, 420),
                mbType = GuiConstructor.createMenuButton(EnumCalculateType.getFirstType(), EnumCalculateType.getShowToUserValues(), 440, 35, 120),
                btnCalculate = GuiConstructor.createButton(e -> calculate(tfText.getText()),"Запустить моделирование",  570, 35, 200),
                GuiConstructor.createButton(e -> clear(),"Очистить",  550, 95, 140),
                GuiConstructor.createLabel("Файл с данными:", 10, 70, 360),
                mbFile = GuiConstructor.createMenuButton(getFiles(), 10, 95, 280),
                GuiConstructor.createButton(e -> {
                    mbFile.setText("");
                    getFiles();
                }, "↻", 300, 95, 25),
                GuiConstructor.createButton(e -> loadFile(), "Загрузить из файла", 340, 95, 200),
                labelStatus = GuiConstructor.createLabel("Статус: ", 10, 135, 700),
                GuiConstructor.createLabel("Входные данные:", 10, 175, 200),
                GuiConstructor.createLabel("Выходные данные:", 230, 175, 200),
                tfInput = GuiConstructor.createTextField("", 10, 200, 200),
                taOutput = GuiConstructor.createTextArea(false,230, 200, 400, 300),
                btnNextStep = GuiConstructor.createButton(e -> nextStep(), "Следующий шаг", 10, 240, 200),
                GuiConstructor.createButton(e -> stopModelling(), "Остановить моделирование", 10, 280, 200)

        );
    }

    public void stopModelling() {
        modeller.stopModelling();
        modeller.play();
        modeller.interrupt();
        modeller = new Modeller();
        modeller.start();
        taOutput.clear();
        updateStatus("Моделирование отменено.");
        btnCalculate.setOnAction(e -> calculate(tfText.getText()));
    }

    public Modeller getModeller() {
        return modeller;
    }

    private void calculate(String lsa) {
        if (!modeller.needToModel) {
            btnCalculate.setOnAction(e -> stopModelling());
            try {
                taOutput.setText("");
                String validatedLSA = Validator.validateString(lsa);
                List<Token> tokens = Analyzer.parseTokenString(validatedLSA);

                EnumCalculateType type = EnumCalculateType.getEnumByType(mbType.getText());

                BooleanPackage booleanPackage = new BooleanPackage(type);
                booleanPackage.setGuiObject(tfInput, btnNextStep, labelStatus);
                TokenPackage tokenPackage = new TokenPackage(type, booleanPackage);
                tokenPackage.setTAOutput(taOutput);

                boolean checkForLoop = false;

                if (type == null) return;
                switch (type) {
                    case COMMON:
                        checkForLoop = false;
                        booleanPackage.setIsWaitedForValuesString(true);
                        break;
                    case ALL:
                        checkForLoop = true;
                        booleanPackage.setMaxBitsCount(Modeller.getFormalToRealIndexMap(tokens).keySet().size());
                        break;
                }

                modeller.setupObjects(tokens, booleanPackage, tokenPackage, checkForLoop);


            } catch (Exception e) {
                e.printStackTrace();
                updateStatus(e.getMessage());
            }
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
