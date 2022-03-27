package ru.samgtu.camilot;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumTokenType;
import ru.samgtu.camilot.objects.Bot;
import ru.samgtu.camilot.gui.MainScene;
import ru.samgtu.camilot.objects.Token;

import java.util.*;

public class ThreadModeller extends Thread {

    private volatile boolean play = true;
    private volatile Bot bot;
    private volatile boolean isWaitedForBotUpdate;
    private volatile boolean isBotUpdated;
    private volatile boolean[] boolValues;
    private volatile boolean boolValue;
    private final MainScene mainScene;

    private volatile boolean needToModel = false;
    private volatile List<Token> tokens = null;
    private volatile EnumCalculateType type = null;

    public ThreadModeller(MainScene mainScene) {
        this.mainScene = mainScene;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            checkPause();
            if (needToModel) {
                //model(tokens, type);
                needToModel = false;
            }
        }
    }

    public synchronized void play() {
        play = true;
        notify();
    }

    public void pause() {
        play = false;
    }
    public void instantPause() {
        play = false;
        checkPause();
    }

    /*
    private synchronized void checkBotUpdated() {
        if (isWaitedForBotUpdate) {
            if (isBotUpdated) {
                isWaitedForBotUpdate = false;
                isBotUpdated = false;
                play();
            }
        }
    }

     */

    private synchronized void checkPause() {
        while (!play) {
            try {
                wait();
            } catch (InterruptedException ex) {

            }
        }
    }

    /*
    public synchronized void addModelData(List<Token> tokens, EnumCalculateType type) {

        this.tokens = tokens;
        this.type = type;
        needToModel = true;
        play();
    }

    public synchronized void addBotModelData(List<Token> tokens, Bot bot, boolean[] boolValues) {

        this.tokens = tokens;
        this.type = EnumCalculateType.COMMON;
        this.bot = bot;
        this.boolValues = boolValues;
        this.isWaitedForBotUpdate = false;
        needToModel = true;
        play();
    }
     */


    /*
    public void model(List<Token> tokens, EnumCalculateType type) {
        Map<String, Integer> formalToRealIndexMap = getFormalToRealIndexMap(tokens);
        Set<Token> xTokenHistory = new HashSet<>(); //История логических токенов. Нужна для пресечения бесконечной прогонки

        StringBuilder sb = new StringBuilder();

        boolean useUpArrow = false;
        int tokenIndex = 0, step = 0, maxBitsCount = 0, maxStep = 0;

        switch (type) {
            case COMMON:
                if (bot != null) updateBooleans();
                else waitForNewCommonValues(formalToRealIndexMap.keySet().size());
                break;
            case STEP_BY_STEP:
                break;
            case ALL:
                maxBitsCount = formalToRealIndexMap.keySet().size();
                maxStep = (int)Math.round(Math.pow(2, maxBitsCount));
                boolValues = getBinaryArray(step, maxBitsCount);
                break;
        }

        while (true) {
            Token currentToken = tokens.get(tokenIndex);
            switch (currentToken.getType()) {
                case Y:
                    switch (type) {
                        case COMMON:
                            if (bot == null) sb.append(currentToken.toString());
                            if (currentToken.getIndex().equals("к")) {
                                if (bot == null) {
                                    updateStatus("Найден токен Yк. Работа завершена.");
                                    appendOutputTF(sb.toString());
                                }
                                sb.setLength(0);
                                bot = null;
                                boolValues = null;
                                isWaitedForBotUpdate = false;
                                isBotUpdated = false;
                                return;
                            } else if (!currentToken.getIndex().equals("н")) {
                                if (bot != null) {
                                    try {
                                        bot.executeCommand(currentToken);
                                        xTokenHistory.clear();
                                        waitForBotUpdate();
                                    } catch (Exception e) {
                                        updateFieldStatus("Произошла ошибка при выполнении ЛСА ботом: " + e.getMessage());
                                    }
                                }
                            }
                            break;
                        case ALL:
                            if (currentToken.getIndex().equals("к")) {
                                String lsa = sb.toString();

                                sb.setLength(0);
                                sb.append('[');
                                for (boolean b : boolValues) sb.append(Validator.getIntFromBoolean(b));
                                sb.append("] ").append(lsa).append(currentToken.toString()).append('\n');
                                updateStatus("Найден токен Yк. Работа завершена.");
                                appendOutputTF(sb.toString());
                                sb.setLength(0);

                                step++;
                                tokenIndex = -1;
                                xTokenHistory.clear();
                                if (maxStep == step) return;
                                else boolValues = getBinaryArray(step, maxBitsCount);
                            } else {
                                sb.append(currentToken.toString());
                            }
                            break;
                        case STEP_BY_STEP:
                            appendOutputTF(currentToken.toString());
                            if (currentToken.getIndex().equals("к")) {
                                updateStatus("Найден токен Yк. Работа завершена.");
                                sb.setLength(0);
                                return;
                            }
                            break;
                    }
                    break;
                case X:

                    switch (type) {
                        case COMMON:
                            System.out.println("    Проверяю условие " + currentToken.toString() + ";" + Arrays.toString(boolValues));
                            if (xTokenHistory.contains(currentToken)) {
                                if (bot == null) waitForNewValues(formalToRealIndexMap.keySet().size());
                                else {
                                    updateFieldStatus("Бесконечный цикл в алгоритме бота. Завершаю моделирование.");
                                    bot = null;
                                    return;
                                }
                            } else {
                                if (boolValues[formalToRealIndexMap.get(currentToken.getIndex())]) {
                                    useUpArrow = true;
                                    System.out.println("    Выполнено условие " + currentToken.toString());
                                }
                            }
                            xTokenHistory.add(currentToken);
                            break;

                        case ALL:
                            if (xTokenHistory.contains(currentToken)) {
                                sb.setLength(0);
                                sb.append('[');
                                for (boolean b: boolValues) sb.append(Validator.getIntFromBoolean(b));
                                sb.append("] Бесконечный цикл.").append('\n');
                                appendOutputTF(sb.toString());
                                sb.setLength(0);

                                tokenIndex = -1;
                                step++;
                                boolValues = getBinaryArray(step, maxBitsCount);
                                xTokenHistory.clear();
                                if (maxStep == step) return;
                            } else {
                                if (boolValues[formalToRealIndexMap.get(currentToken.getIndex())]) {
                                    useUpArrow = true;
                                }
                                xTokenHistory.add(currentToken);
                            }
                            break;
                        case STEP_BY_STEP:
                            waitForNewValue(currentToken);
                            if (boolValue) {
                                useUpArrow = true;
                            }
                            break;
                    }
                    break;
                case UP:
                    if (useUpArrow) { //Если должен перейти по UP-стрелке
                        tokenIndex = getDownArrowTokenByIndex(tokens, currentToken.getIndex()); //то переходит на DOWN-стрелку
                        if (tokenIndex == -1) System.err.println("//TO-DO. Не нашёл DOWN-ARROW с указанным индексом.");
                        useUpArrow = false;
                    }
                    break;
                case DOWN: //Не используется
                    break;
                case W:
                    useUpArrow = true;
                    break;
            }

            tokenIndex++;
        }
    }

     */

    /**
     * Метод, возвращащий мапу с ключами - формальными индексами X-токенов и значениями реальных индексов в массиве
     * @param tokens список всех токенов
     * @return мапа с реальными индексами X-токенов по ключу-формальному-индекску-токена
     */
    public Map<String, Integer> getFormalToRealIndexMap(List<Token> tokens) {
        Map<String, Integer> formalToRealIndexMap = new LinkedHashMap<>();
        Map<Integer, Token> tempIndexMap = new HashMap<>();
        List<Token> xTokens = new ArrayList<>();

        for(Token token: tokens) { //Записываю в отдельный список все X-токены
            if (token.getType() == EnumTokenType.X) xTokens.add(token);
        }

        int index;
        for (Token xToken: xTokens) {
            index = Integer.parseInt(xToken.getIndex());
            tempIndexMap.put(index, xToken);
        }

        int realIndex = 0;
        for (int i: tempIndexMap.keySet()) {
            formalToRealIndexMap.put(tempIndexMap.get(i).getIndex(), realIndex);
            realIndex++;
        }

        return formalToRealIndexMap;
    }

    private int getDownArrowTokenByIndex(List<Token> tokens, String index) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == EnumTokenType.DOWN && tokens.get(i).getIndex().equals(index)) {
                return i;
            }
        }
        return -1;
    }

    /*
    public void updateBooleans() {
        if (bot != null) {
            //boolValues = bot.checkDirections();
        } else {
            updateStatus("Пожалуйста, ");
            boolValues = null;
            instantPause();
        }
    }

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

    /*
    public synchronized void setNewValue(boolean boolValue, boolean playAfter) {
        this.boolValue = boolValue;
        if (playAfter) play();
    }

    public synchronized void setNewValues(boolean[] boolValues, boolean playAfter) {
        this.boolValues = boolValues;
        if (playAfter) play();
    }

    public synchronized void updateBotData(boolean[] boolValues) {
        this.boolValues = boolValues;
        isBotUpdated = true;
    }

    private void waitForNewCommonValues(int size) {
        doJavaFXEvent(e -> mainScene.waitForNewCommonValues(size));
        instantPause();
    }
    private void waitForNewValues(int size) {
        doJavaFXEvent(e -> mainScene.waitForNewValues(size));
        instantPause();
    }

    private void waitForNewValue(Token token) {
        doJavaFXEvent(e -> mainScene.waitForNewValue(token));
        instantPause();
    }

    private void waitForBotUpdate() {
        /*
        isWaitedForBotUpdate = true;
        if (!isBotUpdated) instantPause();
        else {
            isWaitedForBotUpdate = false;
            isBotUpdated = false;
            play();
        }

    }

    private void clearOutputTA() {
        doJavaFXEvent(e -> mainScene.outputTA.setText(""));
    }
    private void appendOutputTF(String text) {
        doJavaFXEvent(e -> mainScene.outputTA.appendText(text));
    }
         */

    /**
     * Метод для обновления статуса в GUI из потока моделлера
     * @param message сообщение
     */
    private void updateStatus(String message) {
        doJavaFXEvent(e -> mainScene.updateStatus(message));
    }

    /**
     * Метод для обновления статуса в Field GUI из потока моделлера
     * @param message сообщение
     */
    public void updateFieldStatus(String message) {
        doJavaFXEvent(e -> mainScene.getField().updateStatus(message));
    }

    /**
     * Метод для корректной совместной работы Thread и JavaFX Thread
     * @param event лямбда-выражение
     */
    private void doJavaFXEvent(EventHandler<ActionEvent> event) {
        new Timeline (new KeyFrame(Duration.ONE, event)).play();
    }

}
