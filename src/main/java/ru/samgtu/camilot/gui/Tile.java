package ru.samgtu.camilot.gui;

import com.sun.javafx.geom.Vec2d;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.enums.EnumTileType;

public class Tile {

    public static final Vec2d size = new Vec2d(16, 16);
    public static final Vec2d startDif = new Vec2d(5, 5);
    public static final double gap = 0;

    private EnumTileType type;
    private Rectangle tile;

    private Vec2d position;

    public Tile(EnumTileType type, Vec2d position) {
        this.type = type;
        this.position = position;
        tile = GuiConstructor.createRectangle(type.getColor(), startDif.x + (size.x + gap)*position.x, startDif.y + (size.y + gap)*position.y, size.x, size.y);
    }

    public Rectangle getRectangle() {
        return tile;
    }

    public void setType(EnumTileType type) {
        this.type = type;
        tile.setFill(Paint.valueOf(type.getColor()));
    }

    public EnumTileType getType() {
        return type;
    }

    public Vec2d getPosition() {
        return position;
    }

    public void setPosition(Vec2d position) {
        this.position = position;
    }
}
