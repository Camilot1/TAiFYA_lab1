package ru.samgtu.camilot.gui;

import javafx.scene.layout.AnchorPane;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.ulits.vectors.DoubleVector2;

public abstract class GuiObject {

    protected final AnchorPane root;

    private final DoubleVector2 position;
    private final DoubleVector2 size;

    public GuiObject(DoubleVector2 position, DoubleVector2 size) {
        this.position = position;
        this.size = size;
        root = GuiConstructor.createAnchorPane(position, size);
    }

    public DoubleVector2 getPosition() {
        return new DoubleVector2(position);
    }

    public void setPosition(DoubleVector2 position) {
        this.position.x = position.x;
        this.position.y = position.y;

        root.setLayoutX(position.x);
        root.setLayoutY(position.y);
    }

    public DoubleVector2 getSize() {
        return new DoubleVector2(size);
    }

    public void setSize(DoubleVector2 size) {
        this.size.x = size.x;
        this.size.y = size.y;

        root.setPrefSize(size.x, size.y);
    }

    public AnchorPane getRoot() {
        return root;
    }
}
