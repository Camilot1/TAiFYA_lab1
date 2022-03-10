package ru.samgtu.camilot.exceptions;

public class TokenIndexException extends Exception {

    public TokenIndexException() {
        super("Введена некорректная строка. Причина: некорректный индекс после токена");
    }
}
