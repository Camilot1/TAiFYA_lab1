package ru.samgtu.camilot.objects;

import ru.samgtu.camilot.enums.EnumTokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    /**
     * Метод проверки корректности токенов, полученных из строки с ЛСА.
     * В нём выполняется следующие проверки:
     * 1. Проверка на позицию и дублирование токенов Yн и Yк,
     * 2. Проверка на отсутсвие верхней стрелки после токенов X и W,
     * 3. Проверка на отсутсвие токенов X и W перед верхней стрелкой?
     * 4. Проверки на отсутствие пары верхней стрелки в виде нижней стрелки с таким же индексом, как у верхней стрелки.
     * @param tokens список токенов
     * @throws Exception ошибка при некорректном наборе токенов
     */
    public static void checkTokenList(List<Token> tokens) throws Exception {
        if (tokens.size() == 0) throw new Exception("Некорректная ЛСА. Причина: введена пустая строка");
        if (!tokens.get(0).toString().equals("Yн")) throw new Exception("Некорректная ЛСА. Причина: ЛСА начинается не с токена Yн");
        if (!tokens.get(tokens.size()-1).toString().equals("Yк")) throw new Exception("Некорректная ЛСА. Причина: ЛСА заканчивается не на токен Yк");

        int i = 1;
        while (i < tokens.size() - 1) {
            Token token = tokens.get(i);

            if (token.toString().equals("Yн")) throw new Exception("Некорректная ЛСА. Причина: присутствует несколько токенов Yн");
            else if (token.toString().equals("Yк")) throw new Exception("Некорректная ЛСА. Причина: присутствует несколько токенов Yк");

            if (token.getType() == EnumTokenType.W || token.getType() == EnumTokenType.X) {
                if (tokens.get(i+1).getType() != EnumTokenType.UP) {
                    throw new Exception("Некорректная ЛСА. Причина: после токена " + token.toString() + " (№" + i + " нет верхней стрелки");
                }
            }

            if (token.getType() == EnumTokenType.UP) {
                if (!(tokens.get(i-1).getType() == EnumTokenType.W || tokens.get(i-1).getType() == EnumTokenType.X)) {
                    throw new Exception("Некорректная ЛСА. Причина: верхняя стрелка стоит после токена " + tokens.get(i-1).toString() + " (№" + (i-1) + "), а не после токенов X или W");
                }
                int index = getDownArrowTokenByIndex(tokens, token.getIndex());
                if (index == -1) {
                    throw new Exception("Некорректная ЛСА. Причина: верхняя стрелка указывает на несуществующую нижнюю стрелку. Номер токена в строке: " + i);
                }
            }
            i++;
        }
    }

    /**
     * Метод получения номера нижней стрелки в массиве токена по её индексу
     * @param tokens список токенов
     * @param index индекс нижней стрелки
     * @return номер нижней стрелки в массиве токенов. Возвращает -1, если нижняя стрелка не найдена
     */
    private static int getDownArrowTokenByIndex(List<Token> tokens, String index) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == EnumTokenType.DOWN && tokens.get(i).getIndex().equals(index)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Метод парсинга (лексического анализа) строки с ЛСА на токены
     * @param s строка с ЛСА
     * @return список токенов
     * @throws Exception ошибка при парсинге токенов
     */
    public static List<Token> parseTokenString(String s) throws Exception {
        int i = 0;
        List<Token> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        EnumTokenType type;

        while (i < s.length()) {
            type = EnumTokenType.getEnumByChar(s.charAt(i));

            if (type != null) { //Если корректный символ токена
                while (i+1 < s.length()) { //Поиск полного индекса токена
                    if (Validator.isIndexChar(s.charAt(i+1))) {
                        sb.append(s.charAt(i+1));
                        i++;
                    } else break;
                }

                if (type != EnumTokenType.W && sb.length() == 0) {
                    throw new Exception("Введена некорректная строка. Причина: некорректный индекс после токена. Индекс в строке: " + i);
                } else tokens.add(new Token(type, sb.toString()));
                sb.setLength(0);
                i++;
            } else throw new Exception();
        }
        checkTokenList(tokens);
        return tokens;
    }
}
