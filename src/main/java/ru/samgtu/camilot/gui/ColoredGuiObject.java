package ru.samgtu.camilot.gui;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.ulits.vectors.DoubleVector2;

public abstract class ColoredGuiObject extends GuiObject {

    protected final Rectangle rectangle;

    public ColoredGuiObject(DoubleVector2 position, DoubleVector2 size, String color) {
        super(position, size);
        rectangle = GuiConstructor.createRectangle(Paint.valueOf(color), position, size);
        root.getChildren().add(rectangle);
    }

    @Override
    public void setSize(DoubleVector2 size) {
        super.setSize(size);
        rectangle.setWidth(size.x);
        rectangle.setHeight(size.y);
    }

    public void setColor(String color) {
        rectangle.setFill(Paint.valueOf(color));
    }
}
