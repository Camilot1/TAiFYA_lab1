package ru.samgtu.camilot.gui;

import ru.samgtu.camilot.enums.EnumTileType;
import ru.samgtu.camilot.ulits.vectors.DoubleVector2;
import ru.samgtu.camilot.ulits.vectors.IntVector2;

public class Tile extends ColoredXYGuiObject {

    private EnumTileType type;

    public Tile(EnumTileType type, IntVector2 xyIndexes, DoubleVector2 startPosition, DoubleVector2 size, DoubleVector2 xyGaps) {
        super(xyIndexes, startPosition, size, xyGaps, type.getColor());
        this.type = type;
    }

    public void setType(EnumTileType type) {
        this.type = type;
        setColor(type.getColor());
    }

    public EnumTileType getType() {
        return type;
    }

}
