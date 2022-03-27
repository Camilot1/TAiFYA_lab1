package ru.samgtu.camilot;

import ru.samgtu.camilot.enums.EnumTokenType;
import ru.samgtu.camilot.objects.Token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Parser {

    public static void checkTokenList(List<Token> list) throws Exception {
        int i = 0;
        int yStartCount = 0, yEndCount = 0;

        if (!list.get(0).toString().equals("Yн")) throw new Exception("Некорректная ЛСА. Причина: ЛСА начинается не с Yн");
        if (!list.get(list.size()-1).toString().equals("Yк")) throw new Exception("Некорректная ЛСА. Причина: ЛСА заканчивается не на Yк");

        while (i < list.size()) {
            Token token = list.get(i);
            Token tempToken;

            //Проверка на дополнительные Yн и Yк
            if (token.toString().equals("Yн")) yStartCount++;
            else if (token.toString().equals("Yк")) yEndCount++;

            if (yStartCount > 1) throw new Exception("Некорректная ЛСА. Причина: присутствует несколько Yн");
            else if (yEndCount > 1) throw new Exception("Некорректная ЛСА. Причина: присутствует несколько Yк");

            if (token.getType() == EnumTokenType.W) {
                tempToken = list.get(i+1);
                if (tempToken.getType() != EnumTokenType.UP && tempToken.getType() != EnumTokenType.DOWN)
                    throw new Exception("Некорректная ЛСА. Причина: после безусловного перехода нет стелок");
            }

            if (token.getType() == EnumTokenType.UP) {
                int i2 = 0;

                while (true) {
                    if (list.get(i2).getType() == EnumTokenType.DOWN) {
                        if (token.getIndex().equals(list.get(i2).getIndex())) break;
                    }
                    i2++;

                    if (i2 == list.size()) throw new Exception("Некорректная ЛСА. Причина: стрелка вверх указывает на несуществующую стрелку вниз. Номер токена в строке: " + i);
                }
            }
            i++;
        }

        HashSet<String> xList = new HashSet<>();
        for (Token token: list) {
            if (token.getType() == EnumTokenType.X) {
                if (xList.contains(token.getIndex())) throw new Exception("Некорректная ЛСА. Причина: повторяется условный оператор " + token.toString());
                else xList.add(token.getIndex());
            }
        }
    }

    /**
     * Метод поиска максимального индекса логических операторов Xn
     * @param tokens список токенов
     * @return максимальный индекс логических операторов Xn
     */
    public static int getVarsCount(List<Token> tokens) {
        List<Integer> list = new ArrayList<>();

        for (Token token: tokens) {
            if (token.getType() == EnumTokenType.X) {
                list.add(Integer.parseInt(token.getIndex()));
            }
        }

        return list.size();
    }

    /**
     * Метод парсинга строки с ЛСА на токены
     * @param s строка с ЛСА
     * @return список токенов
     * @throws Exception ошибка при парсинге токенов
     */
    public static List<Token> parseTokenString(String s, boolean checkStartAndEnd) throws Exception {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        List<Token> tokens = new ArrayList<>();

        EnumTokenType type;

        while (i < s.length()) {
            type = EnumTokenType.getEnumByChar(s.charAt(i));

            if (type != null) { //Если корректный символ операции
                while (i+1 < s.length()) { //Поиск полного индекса
                    char ch = Validator.validateChar(s.charAt(i+1));
                    if (Validator.isIndexChar(ch)) {
                        i++;
                        sb.append(ch);
                    } else break;
                }

                if (type != EnumTokenType.W && sb.length() == 0) throw new Exception("Введена некорректная строка. Причина: некорректный индекс после токена. Индекс в строке: " + i); //Если
                else {
                    tokens.add(new Token(type, sb.toString()));
                }
                sb.setLength(0);
            } else throw new Exception();
            i++;
        }

        if (checkStartAndEnd) {
            if (!tokens.contains(new Token(EnumTokenType.Y, "н")))
                throw new Exception("Ошибка. В ЛСА не найден Yн");
            if (!tokens.contains(new Token(EnumTokenType.Y, "к")))
                throw new Exception("Ошибка. В ЛСА не найден Yк");
        }

        return tokens;
    }

    public static List<Token> getSpecificTokens(List<Token> tokens, EnumTokenType type, String[] bannedIndexes) {
        List<Token> newTokens = new ArrayList<>();
        for (Token token: tokens) {
            if (token.getType().equals(type)) {
                boolean hasBannedIndex = false;
                for (String bannedIndex: bannedIndexes) {
                    if (bannedIndex.equals(token.getIndex())) {
                        hasBannedIndex = true;
                        break;
                    }
                }
                if (!hasBannedIndex) newTokens.add(token);
            }
        }
        return newTokens;
    }
}
