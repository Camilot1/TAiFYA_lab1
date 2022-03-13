package ru.samgtu.camilot;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.samgtu.camilot.gui.MainScene;

public class Main extends Application {

    private static MainScene mainScene;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Программа");
        primaryStage.setWidth(1600);
        primaryStage.setHeight(800);

        mainScene = new MainScene();
        primaryStage.setScene(mainScene.getScene());
        primaryStage.show();

    }

    public static void main(String[] args) {
        boolean[][] arr = Modeller.getBinaryMatrix(3);
        launch();
    }

    public static MainScene getMainScene() {
        return mainScene;
    }
}
