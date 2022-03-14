package ru.samgtu.camilot.enums;

import com.sun.javafx.geom.Vec2d;

public enum EnumDirection {
    UP(0, '↑', new Vec2d(0, -1)),
    RIGHT(1, '→', new Vec2d(1, 0)),
    DOWN(2, '↓', new Vec2d(0, 1)),
    LEFT(3, '←', new Vec2d(-1, 0));

    private final int value;
    private final char directionChar;
    private final Vec2d positionDifference;
    EnumDirection(int value, char directionChar, Vec2d positionDifference) {
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

    public Vec2d getPositionDifference() {
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
