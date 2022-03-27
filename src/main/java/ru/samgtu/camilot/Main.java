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

        primaryStage.setTitle("Программа");
        primaryStage.setWidth(1600);
        primaryStage.setHeight(800);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                lsaTab = new LSATab(),
                fieldTab = new FieldTab()
        );

        primaryStage.setScene(new Scene(tabPane));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
