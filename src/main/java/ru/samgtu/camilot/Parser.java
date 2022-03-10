package ru.samgtu.camilot;

import ru.samgtu.camilot.enums.EnumTokenType;
import ru.samgtu.camilot.exceptions.StringSizeException;
import ru.samgtu.camilot.exceptions.TokenCheckerException;
import ru.samgtu.camilot.exceptions.TokenIndexException;
import ru.samgtu.camilot.objects.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Parser {

    public static void checkTokenList(List<Token> list) throws TokenCheckerException {
        int i = 0;
        while (i < list.size()) {
            Token token = list.get(i);
            Token tempToken;

            //Проверка на дополнительные Yн и Yк
            if (token.getType() == EnumTokenType.Y &&
                    (token.getIndex().equals("н") || token.getIndex().equals("к"))
            ) throw new TokenCheckerException("Некорректные данные. Причина: в данных присутствуют лишние Yн или Yк");

            if (token.getType() == EnumTokenType.W) {
                if (i == list.size() - 1)
                    throw new TokenCheckerException("Некорректные данные. Причина: в безусловный переход расположен в конце");

                tempToken = list.get(i+1);
                if (tempToken.getType() != EnumTokenType.UP && tempToken.getType() != EnumTokenType.DOWN)
                    throw new TokenCheckerException("Некорректные данные. Причина: после безусловного перехода нет стелок");
            }

            if (token.getType() == EnumTokenType.UP) {
                int i2 = 0;

                while (true) {
                    if (list.get(i2).getType() == EnumTokenType.DOWN) {
                        if (token.getIndex().equals(list.get(i2).getIndex())) break;
                    }
                    i2++;

                    if (i2 == list.size()) throw new TokenCheckerException("Некорректные данные. Причина: стрелка вверх указывает на несуществующую стрелку вниз. Номер токена: " + i+1);
                }
            }


            i++;
        }

        HashSet<String> xList = new HashSet<>();
        for (Token token: list) {
            if (token.getType() == EnumTokenType.X) {
                if (xList.contains(token.getIndex())) throw new TokenCheckerException("Некорректные данные. Причина: повторяется условный оператор " + token.toString());
                else xList.add(token.getIndex());
            }
        }
    }

    public static int getVarsCount(List<Token> tokens) {
        List<Integer> list = new ArrayList<>();

        for (Token token: tokens) {
            if (token.getType() == EnumTokenType.X) {
                list.add(Integer.parseInt(token.getIndex()));
            }
        }
        Collections.sort(list);

        if (list.size() > 0) return list.get(list.size()-1);
        return -1;
    }

    public static List<Token> parseString(String s) throws Exception {

        List<Token> tokens;
        if (s.length() <= 4) throw new StringSizeException();

        tokens = parseTokenString(Validator.validateString(s.substring(2, s.length()-2)));

        return tokens;
    }

    private static List<Token> parseTokenString(String s) throws Exception {
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

                if (type != EnumTokenType.W && sb.length() == 0) throw new TokenIndexException(); //Если
                else {
                    tokens.add(new Token(type, sb.toString()));
                }
                sb.setLength(0);
            } else throw new Exception();
            i++;
        }

        return tokens;
    }
}
