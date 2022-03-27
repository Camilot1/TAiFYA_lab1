package ru.samgtu.camilot.gui;

import javafx.scene.layout.AnchorPane;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.ulits.vectors.DoubleVector2;
import ru.samgtu.camilot.ulits.vectors.IntVector2;

public class XYGuiObject {

    protected final AnchorPane root;

    private final IntVector2 xyIndexes;
    private final DoubleVector2 startPosition;
    private final DoubleVector2 size;
    private final DoubleVector2 xyGaps;

    public XYGuiObject(IntVector2 xyIndexes, DoubleVector2 startPosition, DoubleVector2 size, DoubleVector2 xyGaps) {
        this.xyIndexes = xyIndexes;
        this.startPosition = startPosition;
        this.size = size;
        this.xyGaps = xyGaps;
        root = GuiConstructor.createAnchorPane(startPosition, size);
        updatePosition();
    }

    public void updatePosition() {
        root.setLayoutX(startPosition.x + (size.x + xyGaps.x)*xyIndexes.x);
        root.setLayoutY(startPosition.y + (size.y + xyGaps.y)*xyIndexes.y);
    }

    public IntVector2 getXyIndexes() {
        return new IntVector2(xyIndexes);
    }

    public void setXYIndexes(IntVector2 xyIndexes) {
        this.xyIndexes.x = xyIndexes.x;
        this.xyIndexes.y = xyIndexes.y;

        updatePosition();
    }

    public DoubleVector2 getStartPosition() {
        return new DoubleVector2(startPosition);
    }

    public void setStartPosition(DoubleVector2 startPosition) {
        this.startPosition.x = startPosition.x;
        this.startPosition.y = startPosition.y;

        updatePosition();
    }

    public DoubleVector2 getSize() {
        return new DoubleVector2(size);
    }

    public void setSize(DoubleVector2 size) {
        this.size.x = size.x;
        this.size.y = size.y;
        updatePosition();
    }

    public DoubleVector2 getXYGaps() {
        return new DoubleVector2(xyGaps);
    }

    public void setXYGaps(DoubleVector2 xyGaps) {
        this.xyGaps.x = xyGaps.x;
        this.xyGaps.y = xyGaps.y;
        updatePosition();
    }

    public AnchorPane getRoot() {
        return root;
    }
}
