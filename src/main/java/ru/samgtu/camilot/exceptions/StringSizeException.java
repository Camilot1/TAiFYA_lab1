package ru.samgtu.camilot.exceptions;

public class StringSizeException extends Exception {

    public StringSizeException() {
        super("Введённая строка некорректна. Причина: слишком малая длина строки.");
    }
}
