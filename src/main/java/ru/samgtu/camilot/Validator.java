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
            case 'У': //русская большая в английскую большую
            case 'у': //русская малая в английскую большую
            case 'y': return 'Y'; //английская малая в английскую большую
            case 'H': //английская большая в русскую малую
            case 'h': //английская малая в русскую малую
            case 'Н': return 'н'; //русская большая в русскую малую
            case 'w': return 'W'; //английская малая в английскую большую
            case 'u': return 'U'; //английская малая в английскую большую
            case 'd': return 'D'; //английская малая в английскую большую
            case 'K': //английская большая в русскую малую
            case 'k': //английская малая в русскую малую
            case 'К': return 'к'; //русская большая в русскую малую
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
        if (s.length() != 1) throw new Exception("Ошибка. Введено больше одного символа.");

        if (s.charAt(0) == '1') return true;
        else if (s.charAt(0) == '0') return false;
        else throw new Exception("Введено некорректное логическое значение.");
    }

    public static boolean[] parseBooleans(String s) throws Exception {
        boolean[] booleans = new boolean[s.length()];
        for (int i = 0; i < s.length(); i++) booleans[i] = parseBoolean(s.charAt(i));
        return booleans;
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
