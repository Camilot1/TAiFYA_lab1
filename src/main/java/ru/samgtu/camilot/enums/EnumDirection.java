package ru.samgtu.camilot.enums;

import ru.samgtu.camilot.ulits.vectors.IntVector2;

public enum EnumDirection {
    UP(0, '↑', new IntVector2(0, -1)),
    RIGHT(1, '→', new IntVector2(1, 0)),
    DOWN(2, '↓', new IntVector2(0, 1)),
    LEFT(3, '←', new IntVector2(-1, 0));

    private final int value;
    private final char directionChar;
    private final IntVector2 positionDifference;
    EnumDirection(int value, char directionChar, IntVector2 positionDifference) {
        this.value = value;
        this.directionChar = directionChar;
        this.positionDifference = positionDifference;
    }

    public int getValue() {
        return value;
    }

    public char getDirectionChar() {
        return directionChar;
    }

    public IntVector2 getPositionDifference() {
        return positionDifference;
    }

    public EnumDirection getDirectionByValue(int value) {
        for (EnumDirection enumDirection: values()) {
            if (value == enumDirection.value) return enumDirection;
        }
        return null;
    }

    public EnumDirection getRotatedDirection(int dif) {
        int i = getValue() + dif;
        if (i < 0) i += 4;
        else if (i > 3) i -= 4;
        return getDirectionByValue(i);
    }

}
