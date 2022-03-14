package ru.samgtu.camilot;

public class Validator {

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
     * @throws Exception ошибка при некорректном значении
     */
    public static boolean parseBoolean(char ch) throws Exception {
        if (ch == '1') return true;
        else if (ch == '0') return false;
        else throw new Exception("Введено некорректное входное значение.");
    }

    /**
     * Валидация входного логического значения
     * @param s входное значение
     * @return логическое значение
     * @throws Exception ошибка при некорректном значении
     */
    public static boolean parseBoolean(String s) throws Exception {
        if (s.length() != 1) throw new Exception("Введено некорректное входное значение.");

        int value = Integer.parseInt(s);

        if (value == 1) return true;
        else if (value == 0) return false;
        else throw new Exception("Введено некорректное входное значение.");
    }

    public static boolean[] parseBooleans(String s, int size) throws Exception {
        boolean[] booleans = new boolean[size];
        for (int i = 0; i < size; i++) booleans[i] = parseBoolean(s.charAt(i));
        return booleans;
    }

    public static int getIntFromBoolean(boolean value) {
        return value ? 1: 0;
    }
}
