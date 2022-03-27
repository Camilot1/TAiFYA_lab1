package ru.samgtu.camilot.ulits.vectors;

import java.util.Objects;

public class DoubleVector2 {

    public double x;
    public double y;

    public DoubleVector2() {
        this(0, 0);
    }

    public DoubleVector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public DoubleVector2(DoubleVector2 doubleVector2) {
        this(doubleVector2.x, doubleVector2.y);
    }

    public void plus(DoubleVector2 doubleVector2) {
        this.x += doubleVector2.x;
        this.y += doubleVector2.y;
    }

    public void minus(DoubleVector2 doubleVector2) {
        this.x -= doubleVector2.x;
        this.y -= doubleVector2.y;
    }

    public static void plus(DoubleVector2 firstVector, DoubleVector2 secondVector) {
        firstVector.x += secondVector.x;
        firstVector.y += secondVector.y;
    }

    public static void minus(DoubleVector2 firstVector, DoubleVector2 secondVector) {
        firstVector.x -= secondVector.x;
        firstVector.y -= secondVector.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleVector2 that = (DoubleVector2) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "DoubleVector2[" + x + "," + y + "]";
    }
}
