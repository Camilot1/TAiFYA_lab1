package ru.samgtu.camilot.objects;

import com.sun.javafx.geom.Vec2d;
import ru.samgtu.camilot.enums.EnumDirection;

public class ScreenShot {

    private Vec2d botPos;
    private EnumDirection botDir;

    public ScreenShot(Vec2d botPos, EnumDirection botDir) {
        this.botPos = new Vec2d(botPos);
        this.botDir = botDir;
    }

    public Vec2d getBotPos() {
        return botPos;
    }

    public EnumDirection getBotDir() {
        return botDir;
    }
}
