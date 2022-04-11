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

    /**
     * Метод удаления поля.
     */
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

    /**
     * Метод получения текущего размера поля лабиринта
     * @return вектор с широной и высотой поля
     */
    public IntVector2 getFieldSize() {
        if (tiles == null) return new IntVector2();
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
     * Метод загрузки бота на поле лабиринта.
     */
    public void loadBot() {
        if (bot == null) {
            bot = new Bot(this, EnumDirection.RIGHT, startXYIndexes);
        } else {
            bot.setDirection(EnumDirection.RIGHT);
            bot.setPosition(startXYIndexes);
        }
        scrollRoot.getChildren().remove(bot.getRoot());
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
        if (screenShots.size() == 0) return;
        firstScreenShot();
        int[] counter = new int[1];
        Timeline timeline = new Timeline (new KeyFrame(Duration.millis(tab.getPlayFrameDelay()), ae -> {
            nextScreenShot(true);
            counter[0]--;
            if (counter[0] == 0) isPlaying = false;
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

    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}
