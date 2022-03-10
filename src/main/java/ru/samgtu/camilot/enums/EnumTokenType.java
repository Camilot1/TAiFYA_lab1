package ru.samgtu.camilot.enums;

public enum EnumTokenType {
    Y('Y'),
    X('X'),
    UP('U'),
    DOWN('D'),
    W('W');

    private char ch;
    EnumTokenType(char ch) {
        this.ch = ch;
    }

    public char getChar() {
        return ch;
    }

    public static EnumTokenType getEnumByChar(char ch) {
        for (EnumTokenType tokenType: EnumTokenType.values()) if (tokenType.ch == ch) return tokenType;
        return null;
    }
}
