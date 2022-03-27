package ru.samgtu.camilot.objects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import ru.samgtu.camilot.Validator;
import ru.samgtu.camilot.enums.EnumCalculateType;


public class BooleanPackage {

    private volatile Bot bot;
    private final EnumCalculateType type;
    private volatile boolean[] booleans;
    private volatile TextField tfInput;
    private volatile Button btnNextStep;
    private volatile Label labelStatus;
    private volatile boolean isWaitedForCommonValues;

    private volatile int step = 0, maxBitsCount = 0, maxStep = 0;

    public BooleanPackage(EnumCalculateType type) {
        this.type = type;
    }

    public BooleanPackage(EnumCalculateType type, boolean[] booleans) {
        this.type = type;
        this.booleans = booleans;
    }

    public void setMaxBitsCount(int maxBitsCount) {
        this.maxBitsCount = maxBitsCount;
        maxStep = (int)Math.round(Math.pow(2, maxBitsCount));
        booleans = getBinaryArray(step, maxBitsCount);
    }

    public synchronized void nextBooleans() {
        step++;
        booleans = getBinaryArray(step, maxBitsCount);
    }

    public synchronized void setIsWaitedForCommonValues(boolean flag) {
        isWaitedForCommonValues = flag;
    }

    public int getStep() {
        return step;
    }

    public int getMaxStep() {
        return maxStep;
    }
    private boolean[] getBinaryArray(int number, int base) {
        final boolean[] array = new boolean[base];
        int mask = 1;
        for (int i = base; i > 0; i--) {
            array[i - 1] = (number & mask) == mask;
            mask <<= 1;
        }
        return array;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public void checkBot() {
        if (bot != null) setBooleans(bot.checkDirections());
    }

    public EnumCalculateType getType() {
        return type;
    }

    public void removeBot() {
        this.bot = null;
    }

    public void setGuiObject(TextField tfInput, Button btnNextStep, Label labelStatus) {
        this.tfInput = tfInput;
        this.btnNextStep = btnNextStep;
        this.labelStatus = labelStatus;
    }

    public void removeGuiObject() {
        this.tfInput = null;
        this.btnNextStep = null;
        this.labelStatus = null;
    }

    public void waitForCommonValues(Modeller modeller) throws Exception {
        if (btnNextStep == null) throw new Exception("Не подключена кнопка следующего хода.");
        if (tfInput == null) throw new Exception("Не подключена кнопка текстового поля с входными данными.");
        if (labelStatus == null) throw new Exception("Не подключена надпись статуса.");

        int count = Modeller.getFormalToRealIndexMap(modeller.getTokens()).keySet().size();
        updateStatus("Введите " + count + " логических значений.");
        btnNextStep.setOnAction(e -> {
            try {
                boolean[] bs = Validator.parseBooleans(tfInput.getText());
                setBooleans(bs);
                if (count == bs.length) {
                    btnNextStep.setOnAction(e1 -> {
                    });
                    modeller.play();
                } else updateStatus("Требуется ввести " + count + " логических значений, а Вы ввели только " + bs.length + ". Повторите ввод и повторно нажмите кнопку следующего хода.");
            } catch (Exception ex) {
                updateStatus(ex.getMessage());
            }
        });

    }

    public void waitForNextStep(Modeller modeller) throws Exception {
        if (btnNextStep == null) throw new Exception("Не подключена кнопка следующего хода.");
        if (tfInput == null) throw new Exception("Не подключена кнопка текстового поля с входными данными.");
        if (labelStatus == null) throw new Exception("Не подключена надпись статуса.");

        updateStatus("Введите логическое значение.");
        btnNextStep.setOnAction(e -> {
            try {
                boolean[] b = new boolean[1];
                b[0] = Validator.parseBoolean(tfInput.getText());
                setBooleans(b);
                btnNextStep.setOnAction(e1 -> {});
                modeller.play();
            } catch (Exception ex) {
                updateStatus(ex.getMessage());
            }
        });
    }

    public boolean[] getBooleans() {
        return booleans;
    }

    public boolean isWaitedForCommonValues() {
        return isWaitedForCommonValues;
    }

    public boolean isStepByStepMode() {
        return type == EnumCalculateType.STEP_BY_STEP;
    }

    public boolean getBoolean(int index) {
        return booleans[index];
    }

    public synchronized void setBooleans(boolean[] booleans) {
        this.booleans = booleans;
    }

    /**
     * Метод для обновления статуса в GUI из потока моделлера
     * @param message сообщение
     */
    public void updateStatus(String message) {
        doJavaFXEvent(e -> labelStatus.setText("Статус: " + message));
    }

    /**
     * Метод для корректной совместной работы Thread и JavaFX Thread
     * @param event лямбда-выражение
     */
    private void doJavaFXEvent(EventHandler<ActionEvent> event) {
        new Timeline(new KeyFrame(Duration.ONE, event)).play();
    }
}
