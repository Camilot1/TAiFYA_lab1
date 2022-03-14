package ru.samgtu.camilot;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.samgtu.camilot.gui.MainScene;

public class Main extends Application {

    private static MainScene mainScene;
    private static ThreadModeller threadModeller;

    private static String status;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Программа");
        primaryStage.setWidth(1600);
        primaryStage.setHeight(800);

        primaryStage.setScene(mainScene.getScene());
        primaryStage.show();

    }

    public static void main(String[] args) {
        mainScene = new MainScene();
        threadModeller = new ThreadModeller(mainScene);
        mainScene.setThreadModeller(threadModeller);
        threadModeller.start();
        launch();
    }

    public static MainScene getMainScene() {
        return mainScene;
    }

    public static void updateStatus(String message) {
        status = message;
    }

    public static String getStatus() {
        return status;
    }

    public static ThreadModeller getThreadModeller() {
        return threadModeller;
    }
}
