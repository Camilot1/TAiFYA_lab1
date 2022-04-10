package ru.samgtu.camilot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import ru.samgtu.camilot.tabs.FieldTab;
import ru.samgtu.camilot.tabs.LSATab;

public class Main extends Application {

    public static LSATab lsaTab;
    public static FieldTab fieldTab;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Программа для моделирования");
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                lsaTab = new LSATab(),
                fieldTab = new FieldTab()
        );

        primaryStage.setScene(new Scene(tabPane));
        primaryStage.setOnCloseRequest(e -> {
            lsaTab.getModeller().interrupt();
            try {
                fieldTab.getField().getBot().getModeller().interrupt();
            } catch (Exception ignored) {}
        });
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
