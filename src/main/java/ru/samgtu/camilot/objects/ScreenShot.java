package ru.samgtu.camilot.objects;

import ru.samgtu.camilot.enums.EnumDirection;
import ru.samgtu.camilot.ulits.vectors.IntVector2;

public class ScreenShot {

    private final IntVector2 position;
    private final EnumDirection direction;

    public ScreenShot(IntVector2 position, EnumDirection direction) {
        this.position = new IntVector2(position);
        this.direction = direction;
    }

    public IntVector2 getPosition() {
        return position;
    }

    public EnumDirection getDirection() {
        return direction;
    }
}
