package ru.samgtu.camilot.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import ru.samgtu.camilot.GuiConstructor;
import ru.samgtu.camilot.enums.EnumDirection;
import ru.samgtu.camilot.enums.EnumTileType;
import ru.samgtu.camilot.objects.Bot;
import ru.samgtu.camilot.objects.ScreenShot;
import ru.samgtu.camilot.tabs.FieldTab;
import ru.samgtu.camilot.ulits.vectors.DoubleVector2;
import ru.samgtu.camilot.ulits.vectors.IntVector2;

import java.util.List;

public class Field {

    private final FieldTab tab;

    public static final DoubleVector2 tileSize = new DoubleVector2(32, 32);
    public static final DoubleVector2 tileStartPosition = new DoubleVector2(5, 5);
    public static final DoubleVector2 tileXYGap = new DoubleVector2(0, 0);

    private final ScrollPane scrollPane;
    private final AnchorPane scrollRoot;

    private Tile[][] tiles;
    private IntVector2 startXYIndexes;
    private IntVector2 endXYIndexes;
    private Bot bot;

    public boolean isPlaying = false;

    private static int currentScreenShot;

    public Field(FieldTab tab, DoubleVector2 position, DoubleVector2 size) {
        this.tab = tab;
        scrollPane = GuiConstructor.createScrollPane(scrollRoot = new AnchorPane(), position, size);
    }

    /**
     * Метод обновления размера поля в тайлах.
     * Если какие-либо тайлы уже существует, очищает и их отображение тоже
     * @param fieldSize Вектор с целочисленными размерами поля
     */
    private void updateFieldSize(IntVector2 fieldSize) {
        deleteField();
        tiles = new Tile[fieldSize.x][fieldSize.y];
        for (int x = 0; x < fieldSize.x; x++) {
            for (int y = 0; y < fieldSize.y; y++) {
                tiles[x][y] = new Tile(EnumTileType.EMPTY, new IntVector2(x, y), tileStartPosition, tileSize, tileXYGap);
                scrollRoot.getChildren().add(tiles[x][y].getRoot());
            }
        }
    }

    private void deleteField() {
        IntVector2 fieldSize = getFieldSize();

        for (int x = 0; x < fieldSize.x; x++) {
            for (int y = 0; y < fieldSize.y; y++) {
                scrollRoot.getChildren().remove(tiles[x][y].getRoot());
            }
        }
        tiles = null;
        startXYIndexes = null;
        endXYIndexes = null;
    }

    public IntVector2 getFieldSize() {
        if (tiles == null) return new IntVector2(0, 0);
        return new IntVector2(tiles.length, tiles[0].length);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Метод загрузки типов тайлов из списка строк данных
     * @param data построчный список данных
     */
    public void loadField(List<String> data) {
        IntVector2 dataSize = getFieldSizeFromData(data);
        if (!getFieldSize().isEqual(dataSize)) clearField();
        updateFieldSize(dataSize);

        for (int y = 0; y < data.size(); y++) {
            for (int x = 0; x < data.get(y).length(); x++) {
                EnumTileType type = EnumTileType.getEnumByChar(data.get(y).charAt(x));
                if (type == null) type = EnumTileType.EMPTY;
                else if (type == EnumTileType.START) startXYIndexes = new IntVector2(x, y);
                else if (type == EnumTileType.END) endXYIndexes = new IntVector2(x, y);
                tiles[x][y].setType(type);
            }
        }
        loadBot();
    }

    /**
     * Метод очистки всех тайлов от типов (установка типа EMPTY)
     */
    private void clearField() {
        if (tiles == null) return;
        for(Tile[] tilesArr: tiles) {
            for(Tile tile: tilesArr) {
                tile.setType(EnumTileType.EMPTY);
            }
        }
    }

    public void loadBot() {
        if (bot == null) {
            bot = new Bot(this, EnumDirection.RIGHT, startXYIndexes);
        } else {
            bot.setDirection(EnumDirection.RIGHT);
            bot.setPosition(startXYIndexes);
        }
        scrollRoot.getChildren().add(bot.getRoot());
    }

    public void firstScreenShot() {
        if (bot == null || isPlaying) return;
        List<ScreenShot> screenShots = bot.getScreenShots();
        if (screenShots == null || screenShots.size() == 0) return;
        currentScreenShot = 0;
        bot.loadScreenShot(currentScreenShot);
    }

    public void prevScreenShot() {
        if (bot == null || isPlaying) return;
        List<ScreenShot> screenShots = bot.getScreenShots();
        if (currentScreenShot > 0) currentScreenShot--;
        if (screenShots == null || screenShots.size() <= currentScreenShot) return;
        bot.loadScreenShot(currentScreenShot);
    }

    public void play() {
        if (bot == null || isPlaying) return;
        List<ScreenShot> screenShots = bot.getScreenShots();
        firstScreenShot();
        int[] counter = new int[1];
        Timeline timeline = new Timeline (new KeyFrame(Duration.millis(tab.getPlayFrameDelay()), ae -> {
            nextScreenShot(true);
            counter[0]--;
            if (counter[0] == 0) isPlaying = false;
            System.out.println(counter[0]);
        }));
        timeline.setCycleCount(screenShots.size()); //Ограничим число повторений
        counter[0] = screenShots.size();
        isPlaying = true;
        timeline.play();
    }

    public void nextScreenShot(boolean ignoreIsPlaying) {
        if (!ignoreIsPlaying) if (bot == null || isPlaying) return;
        List<ScreenShot> screenShots = bot.getScreenShots();
        if (screenShots == null) return;
        if (currentScreenShot + 1 < screenShots.size()) currentScreenShot++;
        if (screenShots.size() > 0) bot.loadScreenShot(currentScreenShot);
    }

    public void lastScreenShot() {
        if (bot == null || isPlaying) return;
        List<ScreenShot> screenShots = bot.getScreenShots();
        if (screenShots == null || screenShots.size() == 0) return;
        currentScreenShot = screenShots.size()-1;
        bot.loadScreenShot(currentScreenShot);
    }

    private IntVector2 getFieldSizeFromData(List<String> data) {
        IntVector2 dataSize = new IntVector2();
        if (data == null || data.size() == 0) return dataSize;
        dataSize.x = -1;
        dataSize.y = data.size();

        for (String s: data) if (dataSize.x < s.length()) dataSize.x = s.length();
        return dataSize;
    }

    public Bot getBot() {
        return bot;
    }

    public EnumTileType getTileType(IntVector2 position) {
        if (position.isSmaller(getFieldSize())) return tiles[position.x][position.y].getType();
        return null;
    }

    public IntVector2 getStartXYIndexes() {
        return new IntVector2(startXYIndexes);
    }

    public IntVector2 getEndXYIndexes() {
        return new IntVector2(endXYIndexes);
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}
