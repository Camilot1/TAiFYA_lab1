package ru.samgtu.camilot.objects;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import ru.samgtu.camilot.*;
import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumDirection;
import ru.samgtu.camilot.enums.EnumTileType;
import ru.samgtu.camilot.gui.ColoredXYGuiObject;
import ru.samgtu.camilot.gui.Field;
import ru.samgtu.camilot.gui.Tile;
import ru.samgtu.camilot.ulits.vectors.DoubleVector2;
import ru.samgtu.camilot.ulits.vectors.IntVector2;

import java.util.ArrayList;
import java.util.List;

public class Bot extends ColoredXYGuiObject {

    private final Modeller modeller;

    private final Field field;
    private EnumDirection direction;
    private final Label labelDirection;

    private List<ScreenShot> screenShots;

    public Bot(Field field, EnumDirection direction, IntVector2 xyIndexes) {
        super(xyIndexes, Field.tileStartPosition, Field.tileSize, Field.tileXYGap, "#FFF506");
        this.field = field;
        this.modeller = new Modeller();

        screenShots = new ArrayList<>();

        root.getChildren().addAll(
                labelDirection = GuiConstructor.createLabel("", new DoubleVector2(), Field.tileSize, Pos.CENTER)
        );

        setDirection(direction);
    }

    public void start(List<Token> tokens) throws Exception {
        BooleanPackage booleanPackage = new BooleanPackage(EnumCalculateType.BOT, checkDirections());
        booleanPackage.setBot(this);
        booleanPackage.setGuiObject(null, null, Main.fieldTab.getLabelStatus());
        TokenPackage tokenPackage = new TokenPackage(EnumCalculateType.BOT, booleanPackage);
        tokenPackage.setBot(this);
        tokenPackage.setField(field);

        modeller.model(tokens, booleanPackage, tokenPackage, false);
    }

    public List<ScreenShot> getScreenShots() {
        return screenShots;
    }

    public void setScreenShots(List<ScreenShot> screenShots) {
        if (screenShots == null) this.screenShots = new ArrayList<>();
        else this.screenShots = screenShots;
    }

    public void makeScreenShot() {
        screenShots.add(new ScreenShot(getPosition(), getDirection()));
    }

    public void loadScreenShot(int index) {
        if (index < screenShots.size()) {
            setXYIndexes(screenShots.get(index).getPosition());
            setDirection(screenShots.get(index).getDirection());
        }
    }

    public IntVector2 getPosition() {
        return getXyIndexes();
    }

    public void setPosition(IntVector2 position) {
        setXYIndexes(position);
    }

    public EnumDirection getDirection() {
        return direction;
    }

    public void setDirection(EnumDirection direction) {
        this.direction = direction;
        labelDirection.setText(String.valueOf(direction.getDirectionChar()));
    }

    public void moveByDifference(IntVector2 position) {
        IntVector2 tempVector = getXyIndexes();
        tempVector.plus(position);
        setXYIndexes(tempVector);
    }

    public boolean[] checkDirections() {
        System.out.println("  Проверяю условия");
        Tile[][] tiles = field.getTiles();

        EnumDirection left = direction.getRotatedDirection(-1);
        EnumDirection right = direction.getRotatedDirection(1);

        IntVector2 leftDif = left.getPositionDifference();
        IntVector2 forwardDif = direction.getPositionDifference();
        IntVector2 rightDif = right.getPositionDifference();

        boolean[] xs = new boolean[5];
        IntVector2 pos = getPosition();

        xs[0] = tiles[pos.x + leftDif.x][pos.y + leftDif.y].getType() == EnumTileType.WALL;
        xs[1] = tiles[pos.x + forwardDif.x][pos.y + forwardDif.y].getType() == EnumTileType.WALL;
        xs[2] = tiles[pos.x + rightDif.x][pos.y + rightDif.y].getType() == EnumTileType.WALL;
        xs[3] = tiles[pos.x][pos.y].getType() != EnumTileType.END;
        xs[4] = tiles[pos.x][pos.y].getType() != EnumTileType.START;

        System.out.println("  Проверил условия");

        return xs;
    }

    public void executeCommand(Token token) throws Exception {
        System.out.println("  Начинаю выполнение команды " + token);
        switch (token.toString()) {
            case "Y1":
                direction = direction.getRotatedDirection(-1);
                System.out.println("Повернулся против часовой стрелки в положение " + direction.getDirectionChar());
                break;
            case "Y2":
                moveByDifference(direction.getPositionDifference());
                System.out.println("Передвинулся на " + direction.getPositionDifference() + " клеток на позицию " + getXyIndexes());
                break;
            case "Y3":
                direction = direction.getRotatedDirection(1);
                System.out.println("Повернулся по часовой часовой стрелке в положение " + direction.getDirectionChar());
                break;
            default: throw new Exception("Некорректная команда для бота: " + token);
        }
        System.out.println("  Выполнил команду " + token);
        makeScreenShot();
    }

}
