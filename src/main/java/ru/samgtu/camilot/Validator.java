package ru.samgtu.camilot;

import ru.samgtu.camilot.exceptions.TokenCheckerException;
import ru.samgtu.camilot.objects.Token;

public class Validator {


    public static Token validateToken(String s) {
        if (s.length() != 2) return null;

        try {
            return new Token(s);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Метод валидации строки.
     * @param s входящая строка
     * @return строка с исправленым регистром и раскладкой
     */
    public static String validateString(String s) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            sb.append(validateChar(s.charAt(i)));
        }

        return sb.toString();
    }

    /**
     * Метод проверки, является ли символ корректным индексом
     * @param ch символ
     * @return результат проверки
     */
    public static boolean isIndexChar(char ch) {
        if ((int)ch > 47 && (int)ch < 58) return true; //цифра от 0 до 9
        if (ch == 'к' || ch == 'н') return true;
        return false;
    }

    /**
     * Метод валидации символа. Исправляет регистр и раскладку символа
     * @param ch
     * @return
     */
    public static char validateChar(char ch) {
        switch (ch) {
            case 'У': //русская в английскую
            case 'у': return 'Y'; //русская в английскую
            case 'Н': //русская в русскую
            case 'H': return 'н'; //английская в русскую
            case 'w': return 'W';
            case 'u': return 'U';
            case 'd': return 'D';
            case 'k': return 'к';
            default: return ch;
        }
    }

    /**
     * Валидация входного логического значения
     * @param ch входное значение
     * @return логическое значение
     * @throws TokenCheckerException ошибка при некорректном значении
     */
    public static boolean parseBoolean(char ch) throws TokenCheckerException {
        if (ch == '1') return true;
        else if (ch == '0') return false;
        else throw new TokenCheckerException("Введено некорректное входное значение.");
    }

    /**
     * Валидация входного логического значения
     * @param s входное значение
     * @return логическое значение
     * @throws TokenCheckerException ошибка при некорректном значении
     */
    public static boolean parseBoolean(String s) throws TokenCheckerException {
        if (s.length() != 1) throw new TokenCheckerException("Введено некорректное входное значение.");

        int value = Integer.parseInt(s);

        if (value == 1) return true;
        else if (value == 0) return false;
        else throw new TokenCheckerException("Введено некорректное входное значение.");
    }
}
