package ru.samgtu.camilot.gui;

import com.sun.javafx.geom.Vec2d;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.Main;
import ru.samgtu.camilot.Modeller;
import ru.samgtu.camilot.enums.EnumDirection;
import ru.samgtu.camilot.enums.EnumTileType;
import ru.samgtu.camilot.objects.ScreenShot;
import ru.samgtu.camilot.objects.Token;

import java.util.List;

public class Bot {

    private final Field field;
    private EnumDirection direction;
    private Vec2d position;
    private final AnchorPane root;
    private final Label labelDirection;

    public Bot(Field field, EnumDirection direction, Vec2d position) {
        this.field = field;
        root = GuiConstructor.createAnchorPane(0, 0, Tile.size.x, Tile.size.y);

        root.getChildren().addAll(
                GuiConstructor.createRectangle("#FFF506", 0, 0, Tile.size.x, Tile.size.y),
                labelDirection = GuiConstructor.createLabel("", 0, 0, Tile.size.x, Tile.size.y, Pos.CENTER)
        );

        setDirection(direction);
        setPosition(position);
    }

    public void loadScreenShot(ScreenShot screenShot) {
        setPosition(screenShot.getBotPos());
        setDirection(screenShot.getBotDir());
    }

    public EnumDirection getDirection() {
        return direction;
    }

    public void nextStep(List<Token> list) {
        try {
            Modeller.modelBot(list, this);
        } catch (Exception e) {
            Main.getMainScene().updateStatus(e.getMessage());
        }
    }

    public void executeCommand(Token token) throws Exception {
        switch (token.toString()) {
            case "Y1":
                direction = direction.getRotatedDirection(-1);
                System.out.println("Повернулся против часовой стрелки в положение " + direction.getDirectionChar());
                break;
            case "Y2":
                moveByDifference(direction.getPositionDifference());
                System.out.println("Передвинулся на " + direction.getPositionDifference() + " клеток на позицию " + position);
                break;
            case "Y3":
                direction = direction.getRotatedDirection(1);
                System.out.println("Повернулся по часовой часовой стрелке в положение " + direction.getDirectionChar());
                break;
            default: throw new Exception("Некорректная команда для бота: " + token.toString());
        }
        field.makeScreenShot();
    }

    public boolean[] checkDirections(Tile[][] tiles) {
        EnumDirection left = direction.getRotatedDirection(-1);
        EnumDirection right = direction.getRotatedDirection(1);

        Vec2d leftDif = left.getPositionDifference();
        Vec2d forwardDif = direction.getPositionDifference();
        Vec2d rightDif = right.getPositionDifference();

        boolean[] xs = new boolean[5];
        int x, y;

        x = (int)position.x + (int)leftDif.x;
        y = (int)position.y + (int)leftDif.y;
        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) xs[0] = tiles[x][y].getType() != EnumTileType.WALL;
        else xs[0] = false;

        x = (int)position.x + (int)forwardDif.x;
        y = (int)position.y + (int)forwardDif.y;
        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) xs[1] = tiles[x][y].getType() != EnumTileType.WALL;
        else xs[1] = false;

        x = (int)position.x + (int)rightDif.x;
        y = (int)position.y + (int)rightDif.y;
        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) xs[2] = tiles[x][y].getType() != EnumTileType.WALL;
        else xs[2] = false;

        xs[3] = tiles[(int)position.x][(int)position.y].getType() == EnumTileType.END;
        xs[4] = tiles[(int)position.x][(int)position.y].getType() == EnumTileType.START;

        return xs;
    }

    public void setDirection(EnumDirection direction) {
        this.direction = direction;
        labelDirection.setText(String.valueOf(direction.getDirectionChar()));
    }

    public Vec2d getPosition() {
        return position;
    }

    public void moveByDifference(Vec2d position) {
        this.position.x += position.x;
        this.position.y += position.y;
        root.setLayoutX(Tile.startDif.x + (Tile.size.x + Tile.gap)*this.position.x);
        root.setLayoutY(Tile.startDif.y + (Tile.size.y + Tile.gap)*this.position.y);
    }

    public void setPosition(Vec2d position) {
        if (this.position == null) this.position = new Vec2d(position);
        else {
            this.position.x = position.x;
            this.position.y = position.y;
        }
        root.setLayoutX(Tile.startDif.x + (Tile.size.x + Tile.gap)*this.position.x);
        root.setLayoutY(Tile.startDif.y + (Tile.size.y + Tile.gap)*this.position.y);
    }

    public Field getField() {
        return field;
    }

    public AnchorPane getRoot() {
        return root;
    }
}
