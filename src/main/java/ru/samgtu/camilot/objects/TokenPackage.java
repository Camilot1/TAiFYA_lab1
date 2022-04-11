package ru.samgtu.camilot.objects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumTokenType;
import ru.samgtu.camilot.gui.Field;

public class TokenPackage {

    private volatile TextArea taOutput;
    private Field field;
    private Bot bot;
    private final EnumCalculateType type;
    private final StringBuilder sb = new StringBuilder();
    private final BooleanPackage booleanPackage;

    public TokenPackage(EnumCalculateType type, BooleanPackage booleanPackage) {
        this.type = type;
        this.booleanPackage = booleanPackage;
    }

    public boolean hasBot() {
        return bot != null;
    }

    /**
     * Метод старта проигрывания истории скриншотов. Запускается либо по завершении моделирования бота,
     * либо вручную пользователем через соответствующую кнопку.
     */
    public void playScreenShots() {
        if (hasField()) field.play();
    }

    public boolean hasField() {
        return field != null;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void removeField() {
        field = null;
    }

    public void setTAOutput(TextArea taOutput) {
        this.taOutput = taOutput;
    }

    public void removeTAOutput() {
        taOutput = null;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public void removeBot() {
        this.bot = null;
    }

    /**
     * Метод вывода сообщения о бесконечном цикле для пользователя в режиме "перебора"
     * @param booleans набор логических значений, при котором был получен бесконечный цикл
     */
    public void infiniteCycle(boolean[] booleans) {
        sb.setLength(0);
        sb.append('[');
        if (booleans != null) for (boolean b: booleans) sb.append(Validator.getIntFromBoolean(b));
        sb.append("] Бесконечный цикл\n");
        setTAText(sb.toString());
        sb.setLength(0);
    }

    /**
     * Метод для установки текста в поле вывода.
     * @param text тектс
     */
    public synchronized void setTAText(String text) {
        doJavaFXEvent(e -> taOutput.appendText(text));
    }

    /**
     * Метод записи в результат токена, полученного от моделлера.
     * Если выбран "обычный" или "пошаговый" режим, то токен записывается в поле выходных данных.
     * Если выбран режим "перебора", то сначала записывается набор логических переменных, а потом все полученные ранее токены Y.
     * Если выбран режим "бота", то полученный Y токен, если он не является начальным или конечным, отправляется боту в виде команды для выполнения.
     * @param token Y токен
     * @return true если бот выполнил команду, false если стоят другие режимы моделирования
     * @throws Exception ошибка при записи токена
     */
    public boolean addToken(Token token) throws Exception {
        switch (type) {
            case STEP_BY_STEP:
            case COMMON:
                if (taOutput == null) throw new Exception("Не найдено поле для вывода токена.");
                taOutput.appendText(token.toString());
                return false;
            case ALL:
                if (taOutput == null) throw new Exception("Не найдено поле для вывода токена.");
                sb.append(token.toString());
                if (token.getType().equals(EnumTokenType.Y) && token.getIndex().equals("к")) {
                    sb.append('\n');
                    String s = sb.toString();
                    sb.setLength(0);
                    sb.append('[');
                    for (boolean b: booleanPackage.getBooleans()) sb.append(Validator.getIntFromBoolean(b));
                    sb.append("] ").append(s);
                    setTAText(sb.toString());
                    sb.setLength(0);
                }
                return false;
            case BOT:
                if (bot == null) throw new Exception("Не найден бот при попытке обработать токен.");
                if (!token.getIndex().equals("н") && !token.getIndex().equals("к")) {
                    bot.executeCommand(token);
                    return true;
                }
                return false;
            default: return false;
        }
    }
    /**
     * Метод для корректной совместной работы Thread и JavaFX Thread
     * @param event лямбда-выражение
     */
    private void doJavaFXEvent(EventHandler<ActionEvent> event) {
        new Timeline(new KeyFrame(Duration.ONE, event)).play();
    }
}
