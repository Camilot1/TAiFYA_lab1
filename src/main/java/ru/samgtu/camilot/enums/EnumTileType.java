package ru.samgtu.camilot.enums;

public enum EnumTileType {
    START('S', "#FF3A3A"),
    END('E', "#3EFF3A"),
    EMPTY('_', "#FFFFFF"),
    WALL('1', "#545454"),
    BOT('B', "#FF06F5");

    private final char ch;
    private final String color;
    EnumTileType(char ch, String color) {
        this.ch = ch;
        this.color = color;
    }

    public char getChar() {
        return ch;
    }

    public String getColor() {
        return color;
    }

    public static EnumTileType getEnumByChar(char ch) {
        for (EnumTileType tileType: EnumTileType.values()) if (tileType.ch == ch) return tileType;
        return null;
    }
}
