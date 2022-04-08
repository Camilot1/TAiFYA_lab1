package ru.samgtu.camilot.objects;

import ru.samgtu.camilot.Main;
import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumTokenType;
import ru.samgtu.camilot.tabs.LSATab;

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
                    tokenPackage.infiniteCycle(booleanPackage.getBooleans());
                    booleanPackage.nextBooleans();
                    if (booleanPackage.getStep() >=  booleanPackage.getMaxStep()) stopModelling();
                } catch (Exception e) {
                    e.printStackTrace();
                    stopModelling();
                }
            }
        }
    }

    private synchronized void stopModelling() {
        needToModel = false;
        tokens = null;
        booleanPackage = null;
        tokenPackage = null;
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

    private synchronized void checkPause() {
        while (!play) {
            try {
                wait();
            } catch (InterruptedException ex) {

            }
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public synchronized void setupObjects(List<Token> tokens, BooleanPackage booleanPackage, TokenPackage tokenPackage, boolean checkForLoop) {
        if (needToModel) return;
        this.tokens = tokens;
        this.booleanPackage = booleanPackage;
        this.tokenPackage = tokenPackage;
        this.checkForLoop = checkForLoop;
        needToModel = true;
    }

    public synchronized void model(List<Token> tokens, BooleanPackage booleanPackage, TokenPackage tokenPackage, boolean checkForLoop) throws Exception {
        Map<String, Integer> formalToRealIndexMap = getFormalToRealIndexMap(tokens);
        Set<Token> xTokenHistory = new HashSet<>(); //История логических токенов. Нужна для пресечения бесконечной прогонки

        boolean useUpArrow = false;
        int tokenIndex = 0;

        while (true) {
            Token token = tokens.get(tokenIndex);
            switch (token.getType()) {
                case Y:
                    if (tokenPackage.addToken(token)) booleanPackage.checkBot();
                    if (token.getIndex().equals("к")) {
                        booleanPackage.updateStatus("Выполнение окончено. Включите проигрывание (нажмите кнопку [p], расположенную справа).");
                        return;
                    }
                    break;
                case X:
                    if (checkForLoop) {
                        if (xTokenHistory.contains(token)) throw new RuntimeException("Бесконечный цикл.");
                        else xTokenHistory.add(token);
                    }
                    if (booleanPackage.isStepByStepMode()) {
                        booleanPackage.waitForNextStep(this);
                        instantPause();

                        if (booleanPackage.getBoolean(0)) useUpArrow = true;
                    } else if (booleanPackage.isWaitedForCommonValues()) {
                        booleanPackage.setIsWaitedForCommonValues(false);
                        booleanPackage.waitForCommonValues(this);
                        instantPause();
                        if (booleanPackage.getBoolean(formalToRealIndexMap.get(token.getIndex()))) useUpArrow = true;
                    } else if (booleanPackage.getBoolean(formalToRealIndexMap.get(token.getIndex()))) {
                        useUpArrow = true;
                    }
                    break;
                case UP:
                    if (useUpArrow) { //Если должен перейти по UP-стрелке
                        tokenIndex = getDownArrowTokenByIndex(tokens, token.getIndex()); //то переходит на DOWN-стрелку
                        if (tokenIndex == -1) throw new Exception("Не нашёл стрелку вниз с указанным индексом.");
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

    private int getDownArrowTokenByIndex(List<Token> tokens, String index) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == EnumTokenType.DOWN && tokens.get(i).getIndex().equals(index)) {
                return i;
            }
        }
        return -1;
    }

}
