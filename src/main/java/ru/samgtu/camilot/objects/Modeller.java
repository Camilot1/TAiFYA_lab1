package ru.samgtu.camilot.objects;

import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumTokenType;

import java.util.*;

public class Modeller extends Thread {
    private volatile boolean play = true;

    public volatile boolean needToModel = false;

    private volatile List<Token> tokens;
    private volatile BooleanPackage booleanPackage;
    private volatile TokenPackage tokenPackage;
    private volatile boolean checkForLoop;

    public Modeller() {
    }

    /**
     * Метод запуска потока объекта моделирования ГСА
     */
    @Override
    public void run() {
        while (!isInterrupted()) {
            checkPause();

            if (needToModel) {
                try {
                    model(tokens, booleanPackage, tokenPackage, checkForLoop);
                    if (booleanPackage.getType() == EnumCalculateType.ALL) {
                        if (booleanPackage.getStep() >= booleanPackage.getMaxStep()) stopModelling();
                        else booleanPackage.nextBooleans();
                    } else stopModelling();
                } catch (RuntimeException re) {
                    if (tokenPackage != null && booleanPackage != null) {
                        tokenPackage.infiniteCycle(booleanPackage.getBooleans());
                        booleanPackage.nextBooleans();
                        if (booleanPackage.getStep() >= booleanPackage.getMaxStep()) stopModelling();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    stopModelling();
                }
            }
        }
    }

    /**
     * Метод остановки моделирования ГСА
     */
    public synchronized void stopModelling() {
        needToModel = false;
        tokens = null;
        booleanPackage = null;
        tokenPackage = null;
    }

    /**
     * Метод разморозки процесса моделирования.
     */
    public synchronized void play() {
        play = true;
        notify();
    }

    /**
     * Метод заморозки процесса моделирования.
     */
    public void pause() {
        play = false;
    }

    /**
     * Метод мгновенной и безусловной заморозки процесса моделирования.
     * Используется в момент ожидания получения новых логических значений.
     */
    public void instantPause() {
        play = false;
        checkPause();
    }

    /**
     * Метод провеки состояния процесса.
     */
    private synchronized void checkPause() {
        while (!play) {
            try {
                wait();
            } catch (InterruptedException ignored) {

            }
        }
    }

    /**
     * Метод получения используемых для моделирования токенов.
     * @return список токенов
     */
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * Метод для установки объектов и параметров, необходимых для моделирования, и запуска моделирования ГСА
     * @param tokens список токенов из строки с ЛСА
     * @param booleanPackage объект, хранящий в себе логические значения
     * @param tokenPackage объект, в который записываются полученные при моделировании Y токены
     * @param checkForLoop флаг проверки на бесконечный цикл для режима моделирования с перебором логических значений
     */
    public synchronized void setupObjects(List<Token> tokens, BooleanPackage booleanPackage, TokenPackage tokenPackage, boolean checkForLoop) {
        if (needToModel) return;
        this.tokens = tokens;
        this.booleanPackage = booleanPackage;
        this.tokenPackage = tokenPackage;
        this.checkForLoop = checkForLoop;
        needToModel = true;
    }

    /**
     * Метод моделирования ГСА
     * @param tokens список токенов из строки с ЛСА
     * @param booleanPackage объект, хранящий в себе логические значения
     * @param tokenPackage объект, в который записываются полученные при моделировании Y токены
     * @param checkForLoop флаг проверки на бесконечный цикл для режима моделирования с перебором логических значений
     * @throws Exception ошибка при моделировании ГСА
     */
    public synchronized void model(List<Token> tokens, BooleanPackage booleanPackage, TokenPackage tokenPackage, boolean checkForLoop) throws Exception {
        Map<String, Integer> formalToRealIndexMap = getFormalToRealIndexMap(tokens);
        Set<Token> xTokenHistory = new HashSet<>(); //История логических токенов. Нужна для пресечения бесконечного моделирования, если запущен режим перебора значений

        boolean useUpArrow = false;
        int tokenIndex = 0;

        while (true) {
            Token token = tokens.get(tokenIndex);
            switch (token.getType()) {
                case Y:
                    if (tokenPackage.addToken(token)) booleanPackage.checkBot();
                    if (token.getIndex().equals("к")) {
                        booleanPackage.updateStatus("Выполнение окончено.");
                        if (tokenPackage.hasBot()) tokenPackage.playScreenShots();
                        return;
                    }
                    break;
                case X:
                    if (checkForLoop) {
                        if (xTokenHistory.contains(token)) throw new RuntimeException("Бесконечный цикл.");
                        else xTokenHistory.add(token);
                    }
                    if (booleanPackage.isStepByStepMode()) {
                        booleanPackage.waitForNextStep(this, token);
                        instantPause();
                        if (!booleanPackage.getBoolean(0)) useUpArrow = true;
                    } else if (booleanPackage.isWaitedForValuesString()) {
                        booleanPackage.waitForValuesString(this);
                        instantPause();
                        booleanPackage.setIsWaitedForValuesString(false);
                        if (!booleanPackage.getFirstBoolean()) useUpArrow = true;
                    } else if (booleanPackage.isHasGivenValuesString()) {
                        if (booleanPackage.hasNextBoolean()) {
                            if (!booleanPackage.getNextBoolean()) useUpArrow = true;
                        } else {
                            booleanPackage.updateStatus("Выполнение окончено.");
                            return;
                        }
                    }
                    else {
                        int realIndex = formalToRealIndexMap.get(token.getIndex());
                        if (!booleanPackage.getBoolean(realIndex)) {
                            useUpArrow = true;
                        }
                    }
                    break;
                case UP:
                    if (useUpArrow) { //Если должен перейти по UP-стрелке
                        tokenIndex = getDownArrowTokenByIndex(tokens, token.getIndex()); //то переходит на DOWN-стрелку
                        if (tokenIndex != -1) useUpArrow = false;
                        else throw new Exception("Не нашёл стрелку вниз с указанным индексом.");
                    }
                    break;
                case W:
                    useUpArrow = true;
                    break;
            }
            tokenIndex++;
        }
    }

    /**
     * Метод, возвращащий мапу с ключами - формальными индексами X-токенов и значениями реальных индексов в массиве
     * @param tokens список всех токенов
     * @return мапа с реальными индексами X-токенов по ключу-формальному-индекску-токена
     */
    public static Map<String, Integer> getFormalToRealIndexMap(List<Token> tokens) {
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

    /**
     * Метод получения номера нижней стрелки в массиве токена по её индексу
     * @param tokens список токенов
     * @param index индекс нижней стрелки
     * @return номер нижней стрелки в массиве токенов. Возвращает -1, если нижняя стрелка не найдена
     */
    private int getDownArrowTokenByIndex(List<Token> tokens, String index) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == EnumTokenType.DOWN && tokens.get(i).getIndex().equals(index)) {
                return i;
            }
        }
        return -1;
    }

}
