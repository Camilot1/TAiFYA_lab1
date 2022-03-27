package ru.samgtu.camilot.ulits.vectors;

import java.util.Objects;

public class IntVector2 {
    public int x;
    public int y;

    public IntVector2() {
        this(0, 0);
    }

    public IntVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public IntVector2(IntVector2 intVector2) {
        this(intVector2.x, intVector2.y);
    }

    public void plus(IntVector2 intVector2) {
        this.x += intVector2.x;
        this.y += intVector2.y;
    }

    public void minus(IntVector2 intVector2) {
        this.x -= intVector2.x;
        this.y -= intVector2.y;
    }

    public static void plus(IntVector2 firstVector, IntVector2 secondVector) {
        firstVector.x += secondVector.x;
        firstVector.y += secondVector.y;
    }

    public static void minus(IntVector2 firstVector, IntVector2 secondVector) {
        firstVector.x -= secondVector.x;
        firstVector.y -= secondVector.y;
    }

    public boolean isSmaller(IntVector2 intVector2) {
        return this.x < intVector2.x && this.y < intVector2.y;
    }
    public boolean isSmallerOrEqual(IntVector2 intVector2) {
        return this.x <= intVector2.x && this.y <= intVector2.y;
    }
    public boolean isEqual(IntVector2 intVector2) {
        return this.x == intVector2.x && this.y == intVector2.y;
    }
    public boolean isBiggerOrEqual(IntVector2 intVector2) {
        return this.x >= intVector2.x && this.y >= intVector2.y;
    }
    public boolean isBigger(IntVector2 intVector2) {
        return this.x > intVector2.x && this.y > intVector2.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntVector2 that = (IntVector2) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "IntVector2[" + x + ", " + y + "]";
    }
}
