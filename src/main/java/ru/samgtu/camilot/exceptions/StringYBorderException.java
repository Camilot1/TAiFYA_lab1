package ru.samgtu.camilot.exceptions;

public class StringYBorderException extends Exception {

    public StringYBorderException() {
        super("Введённая строка некорректна. Причина: ошибка в записи Yн и(или) Yк");
    }
}
