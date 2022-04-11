package ru.samgtu.camilot.objects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import ru.samgtu.camilot.enums.EnumCalculateType;


public class BooleanPackage {

    private volatile Bot bot;
    private final EnumCalculateType type;
    private volatile boolean[] booleans;
    private volatile TextField tfInput;
    private volatile Button btnNextStep;
    private volatile Label labelStatus;
    private volatile boolean isWaitedForValuesString;

    private volatile int step = 0, maxBitsCount = 0, maxStep = 0;
    private volatile int currentBoolean = 0;
    private volatile boolean hasGivenValuesString;

    public BooleanPackage(EnumCalculateType type) {
        this.type = type;
    }

    public BooleanPackage(EnumCalculateType type, boolean[] booleans) {
        this.type = type;
        this.booleans = booleans;
    }

    /**
     * Метод, устанавливающий число логических значений в режиме "перебора"
     * @param maxBitsCount
     */
    public void setMaxBitsCount(int maxBitsCount) {
        this.maxBitsCount = maxBitsCount;
        maxStep = (int)Math.round(Math.pow(2, maxBitsCount));
        booleans = getBinaryArray(step, maxBitsCount);
    }

    /**
     * Метод, проверяющий, остались ли ещё неиспользованные логические переменные из строки в режиме "обычного" моделирования
     * @return
     */
    public boolean hasNextBoolean() {
        return currentBoolean < booleans.length - 1;
    }

    /**
     * Метод получения первого логического значения из строки в режиме "обычного" моделирования
     * @return логическое значение
     */
    public synchronized boolean getFirstBoolean() {
        return booleans[0];
    }

    /**
     * Метод получения следующего логического значения из строки в режиме "обычного" моделирования
     * @return логическое значение
     */
    public synchronized boolean getNextBoolean() {
        currentBoolean++;
        return booleans[currentBoolean];
    }

    /**
     * Метод обновления текущего набора логических переменных в режиме "перебора", представленного в виде "двоичного кода"
     */
    public synchronized void nextBooleans() {
        step++;
        booleans = getBinaryArray(step, maxBitsCount);
    }

    /**
     * Метод, реализующий установку ожидания строки из логических значения для режима "обычного" моделирования
     * @param flag флаг
     */
    public synchronized void setIsWaitedForValuesString(boolean flag) {
        isWaitedForValuesString = flag;
    }

    /**
     * Метод, реализующий установку факта получения строки из логических значения для режима "обычного" моделирования
     * @param flag флаг
     */
    public synchronized void setHasGivenValuesString(boolean flag) {
        this.hasGivenValuesString = flag;
    }

    public int getStep() {
        return step;
    }

    public int getMaxStep() {
        return maxStep;
    }

    /**
     * Метод получения требуемого количества логических значений, получаемый путём бинарной записи текущего этапа "подбора" значений
     */
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

    public boolean hasBot() {
        return bot != null;
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

    /**
     * Метод, реализующий ожидание ввода строки логических значения для токенов X в режиме "обычного" моделирования
     * @param modeller объект моделлера
     * @throws Exception ошибка при ожидании
     */
    public void waitForValuesString(Modeller modeller) throws Exception {
        if (btnNextStep == null) throw new Exception("Не подключена кнопка следующего хода.");
        if (tfInput == null) throw new Exception("Не подключена кнопка текстового поля с входными данными.");
        if (labelStatus == null) throw new Exception("Не подключена надпись статуса.");

        updateStatus("Введите строку с логическими значениями");
        btnNextStep.setOnAction(e -> {
            try {
                boolean[] bs = Validator.parseBooleans(tfInput.getText());
                tfInput.setText("");
                setBooleans(bs);
                setHasGivenValuesString(true);
                if (bs.length != 0) {
                    btnNextStep.setOnAction(e1 -> {
                    });
                    modeller.play();
                } else updateStatus("Не найдены логические значения. Повторите ввод строки");
            } catch (Exception ex) {
                updateStatus(ex.getMessage());
            }
        });

    }

    /**
     * Метод, реализующий ожидание ввода логического значения токена X в режиме пошагового моделирования
     * @param modeller объект моделлера
     * @param token токен X, ожидающий логическое значение
     * @throws Exception ошибка при ожидании значения
     */
    public void waitForNextStep(Modeller modeller, Token token) throws Exception {
        if (btnNextStep == null) throw new Exception("Не подключена кнопка следующего хода.");
        if (tfInput == null) throw new Exception("Не подключена кнопка текстового поля с входными данными.");
        if (labelStatus == null) throw new Exception("Не подключена надпись статуса.");

        updateStatus("Введите логическое значение для " + token.toString());
        btnNextStep.setOnAction(e -> {
            try {
                boolean[] b = new boolean[1];
                b[0] = Validator.parseBoolean(tfInput.getText());
                tfInput.setText("");
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

    public boolean isWaitedForValuesString() {
        return isWaitedForValuesString;
    }

    public boolean isHasGivenValuesString() {
        return hasGivenValuesString;
    }

    public boolean isStepByStepMode() {
        return type == EnumCalculateType.STEP_BY_STEP;
    }

    /**
     * Метод получения логического значения по индексу
     * @param index индекс значения
     * @return логическое значение
     */
    public boolean getBoolean(int index) {
        return booleans[index];
    }

    /**
     * Метод установки значений логических переменных
     * @param booleans значения
     */
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
