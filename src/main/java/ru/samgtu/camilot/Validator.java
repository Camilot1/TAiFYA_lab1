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

    public static String validateString(String s) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            sb.append(validateChar(s.charAt(i)));
        }

        return sb.toString();
    }

    public static boolean isIndexChar(char ch) {
        if ((int)ch > 47 && (int)ch < 58) return true; //цифра от 0 до 9
        if (ch == 'к' || ch == 'н') return true;
        return false;
    }

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

    public static boolean parseBoolean(String s) throws TokenCheckerException {
        if (s.length() != 1) throw new TokenCheckerException("Введено некорректное входное значение.");

        int value = Integer.parseInt(s);

        if (value == 1) return true;
        else if (value == 0) return false;
        else throw new TokenCheckerException("Введено некорректное входное значение.");
    }
}
