package ru.samgtu.camilot.objects;

public class Validator {

    /**
     * Метод валидации строки.
     * @param s входящая строка
     * @return строка с исправленым регистром и раскладкой
     */
    public static String validateString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) sb.append(validateChar(s.charAt(i)));
        return sb.toString();
    }

    /**
     * Метод проверки, является ли символ корректным индексом
     * @param ch символ
     * @return результат проверки
     */
    public static boolean isIndexChar(char ch) {
        if ((int)ch > 47 && (int)ch < 58) return true; //цифра от 0 до 9
        return ch == 'к' || ch == 'н';
    }

    /**
     * Метод валидации символа. Исправляет регистр и раскладку символа
     * @param ch входной символ
     * @return валидированный символ
     */
    public static char validateChar(char ch) {
        switch (ch) {
            case 'У': //русская большая в английскую большую
            case 'у': //русская малая в английскую большую
            case 'y': return 'Y'; //английская малая в английскую большую
            case 'Х': //русская большая в английскую большую
            case 'х': //русская малая в английскую большую
            case 'x': return 'X'; //английская малая в английскую большую
            case 'H': //английская большая в русскую малую
            case 'h': //английская малая в русскую малую
            case 'Н': return 'н'; //русская большая в русскую малую
            case 'K': //английская большая в русскую малую
            case 'k': //английская малая в русскую малую
            case 'К': return 'к'; //русская большая в русскую малую
            case 'w': return 'W'; //английская малая в английскую большую
            case 'u': return 'U'; //английская малая в английскую большую
            case 'd': return 'D'; //английская малая в английскую большую
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
     * Валидация одного входного логического значения
     * @param s строка со входным логическим значением
     * @return логическое значение
     * @throws Exception ошибка при некорректном логическом значении
     */
    public static boolean parseBoolean(String s) throws Exception {
        if (s.length() != 1) throw new Exception("Ошибка. Введено больше одного символа.");

        if (s.charAt(0) == '1') return true;
        else if (s.charAt(0) == '0') return false;
        else throw new Exception("Введено некорректное логическое значение.");
    }

    /**
     * Валидация нескольких входных логических значений
     * @param s строка со входными логическими значениями
     * @return массив логических значений
     * @throws Exception ошибка при некорректных логических значениях
     */
    public static boolean[] parseBooleans(String s) throws Exception {
        boolean[] booleans = new boolean[s.length()];
        for (int i = 0; i < s.length(); i++) booleans[i] = parseBoolean(s.charAt(i));
        return booleans;
    }

    /**
     * Метод получения int значения из boolean
     * @param value входное boolean значение
     * @return int значение
     */
    public static int getIntFromBoolean(boolean value) {
        return value ? 1: 0;
    }
}
