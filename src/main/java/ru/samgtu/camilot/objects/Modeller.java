package ru.samgtu.camilot.objects;

import ru.samgtu.camilot.enums.EnumTokenType;

import java.util.*;

public class Modeller extends Thread {
    private volatile boolean play = true;

    public Modeller() {

    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            checkPause();
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

    private synchronized void checkPause() {
        while (!play) {
            try {
                wait();
            } catch (InterruptedException ex) {

            }
        }
    }

    public void model(List<Token> tokens, BooleanPackage booleanPackage, TokenPackage tokenPackage) throws Exception {
        Map<String, Integer> formalToRealIndexMap = getFormalToRealIndexMap(tokens);
        Set<Token> xTokenHistory = new HashSet<>(); //История логических токенов. Нужна для пресечения бесконечной прогонки

        boolean useUpArrow = false;
        int tokenIndex = 0, step = 0, maxBitsCount = 0, maxStep = 0;

        while (true) {
            Token token = tokens.get(tokenIndex);
            switch (token.getType()) {
                case Y:
                    if (tokenPackage.addToken(token)) booleanPackage.checkBot();
                    if (token.getIndex().equals("к")) return;
                    break;
                case X:
                    //if (checkForLoop) {}
                    if (booleanPackage.getBoolean(formalToRealIndexMap.get(token.getIndex()))) useUpArrow = true;
                    break;
                case UP:
                    if (useUpArrow) { //Если должен перейти по UP-стрелке
                        tokenIndex = getDownArrowTokenByIndex(tokens, token.getIndex()); //то переходит на DOWN-стрелку
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

}
