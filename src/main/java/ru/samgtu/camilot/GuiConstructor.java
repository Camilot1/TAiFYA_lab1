package ru.samgtu.camilot;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GuiConstructor {

    public static AnchorPane createAnchorPane(double x, double y, double width, double height) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setLayoutX(x);
        anchorPane.setLayoutY(y);
        anchorPane.setPrefWidth(width);
        anchorPane.setPrefHeight(height);
        return anchorPane;
    }

    public static ScrollPane createScrollPane(AnchorPane scrollRoot, double x, double y, double width, double height) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setLayoutX(x);
        scrollPane.setLayoutY(y);
        scrollPane.setPrefViewportWidth(width);
        scrollPane.setPrefViewportHeight(height);
        scrollPane.setContent(scrollRoot);
        return scrollPane;
    }

    public static ScrollPane createScrollPane(double x, double y, double width, double height) {
        ScrollPane scrollRoot = new ScrollPane();
        scrollRoot.setLayoutX(x);
        scrollRoot.setLayoutY(y);
        scrollRoot.setPrefViewportWidth(width);
        scrollRoot.setPrefViewportHeight(height);
        return scrollRoot;
    }

    public static Label createLabel(String text, double x, double y, double prefWidth) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setPrefWidth(prefWidth);
        return label;
    }

    public static Label createLabel(String text, double x, double y, double prefWidth, Pos pos) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setPrefWidth(prefWidth);
        label.setAlignment(pos);
        return label;
    }

    public static Label createLabel(String text, double x, double y, double prefWidth, double prefHeight, Pos pos) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setPrefWidth(prefWidth);
        label.setPrefHeight(prefHeight);
        label.setAlignment(pos);
        return label;
    }


    public static Button createButton(double x, double y, double prefWidth) {
        Button button = new Button();
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefWidth(prefWidth);
        return button;
    }

    public static Button createButton(EventHandler<ActionEvent> e, double x, double y, double prefWidth) {
        Button button = new Button();
        button.setOnAction(e);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefWidth(prefWidth);
        return button;
    }

    public static Button createButton(EventHandler<ActionEvent> e, String text, double x, double y, double prefWidth) {
        Button button = new Button(text);
        button.setOnAction(e);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefWidth(prefWidth);
        return button;
    }

    public static Button createButton(String text, double x, double y, double prefWidth) {
        Button button = new Button(text);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefWidth(prefWidth);
        return button;
    }

    public static CheckBox createCheckBox(String text, double x, double y) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setLayoutX(x);
        checkBox.setLayoutY(y);
        return checkBox;
    }

    public static CheckBox createCheckBox(String text, String userData, double x, double y) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setUserData(userData);
        checkBox.setLayoutX(x);
        checkBox.setLayoutY(y);
        return checkBox;
    }

    public static TextField createTextField(String text, double x, double y, double prefWidth) {
        TextField textField = new TextField(text);
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setPrefWidth(prefWidth);
        return textField;
    }

    public static TextField createTextField(String text, boolean isEditable, double x, double y, double prefWidth) {
        TextField textField = new TextField(text);
        textField.setEditable(isEditable);
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setPrefWidth(prefWidth);
        return textField;
    }

    public static TextField createTextField(boolean isEditable, double x, double y, double prefWidth) {
        TextField textField = new TextField();
        textField.setEditable(isEditable);
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setPrefWidth(prefWidth);
        return textField;
    }

    public static TextField createTextField(double x, double y, double prefWidth) {
        TextField textField = new TextField("");
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setPrefWidth(prefWidth);
        return textField;
    }

    public static TextField createTextField(EventHandler<? super javafx.scene.input.KeyEvent> e, double x, double y, double prefWidth) {
        TextField textField = new TextField("");
        textField.setOnKeyReleased(e);
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setPrefWidth(prefWidth);
        return textField;
    }

    public static TextArea createTextArea(double x, double y, double prefWidth, double prefHeight) {
        TextArea textArea = new TextArea();
        textArea.setLayoutX(x);
        textArea.setLayoutY(y);
        textArea.setPrefSize(prefWidth, prefHeight);
        return textArea;
    }
    public static TextArea createTextArea(boolean isEditable, double x, double y, double prefWidth, double prefHeight) {
        TextArea textArea = new TextArea();
        textArea.setLayoutX(x);
        textArea.setLayoutY(y);
        textArea.setPrefSize(prefWidth, prefHeight);
        textArea.setEditable(isEditable);
        return textArea;
    }

    public static TextArea createTextArea(double x, double y, double prefWidth, double prefHeight, boolean wrapTextProperty) {
        TextArea textArea = new TextArea();
        textArea.wrapTextProperty().setValue(wrapTextProperty);
        textArea.setLayoutX(x);
        textArea.setLayoutY(y);
        textArea.setPrefSize(prefWidth, prefHeight);
        return textArea;
    }

    public static Rectangle createRectangle(String color, double x, double y, double width, double height) {
        Rectangle rectangle = new Rectangle();
        rectangle.setLayoutX(x);
        rectangle.setLayoutY(y);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        if (color != null) rectangle.setFill(Paint.valueOf(color));
        return rectangle;
    }

    public static Rectangle createRectangle(Paint color, double x, double y, double width, double height) {
        Rectangle rectangle = new Rectangle();
        rectangle.setLayoutX(x);
        rectangle.setLayoutY(y);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        if (color != null) rectangle.setFill(color);
        return rectangle;
    }

    public static MenuButton createMenuButton(String text, List<String> list, double x, double y, double prefWidth) {
        MenuButton menuButton = new MenuButton(text);

        for (String item : list) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(e -> menuButton.setText(menuItem.getText()));
            menuButton.getItems().add(menuItem);
        }

        menuButton.setLayoutX(x);
        menuButton.setLayoutY(y);
        menuButton.setPrefWidth(prefWidth);
        return menuButton;
    }
    public static MenuButton createMenuButton(List<String> list, double x, double y, double prefWidth) {
        MenuButton menuButton = new MenuButton();

        for (String item : list) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(e -> menuButton.setText(menuItem.getText()));
            menuButton.getItems().add(menuItem);
        }

        menuButton.setLayoutX(x);
        menuButton.setLayoutY(y);
        menuButton.setPrefWidth(prefWidth);
        return menuButton;
    }

    public static MenuButton createMenuButton(String[] strings, double x, double y, double prefWidth) {
        MenuButton menuButton = new MenuButton();

        for (String item : strings) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(e -> menuButton.setText(menuItem.getText()));
            menuButton.getItems().add(menuItem);
        }

        menuButton.setLayoutX(x);
        menuButton.setLayoutY(y);
        menuButton.setPrefWidth(prefWidth);
        return menuButton;
    }

    public static MenuButton createMenuButton(Collection<String> list, double x, double y, double prefWidth) {
        MenuButton menuButton = new MenuButton();

        for (String item : list) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(e -> menuButton.setText(menuItem.getText()));
            menuButton.getItems().add(menuItem);
        }

        menuButton.setLayoutX(x);
        menuButton.setLayoutY(y);
        menuButton.setPrefWidth(prefWidth);
        return menuButton;
    }

    public static MenuButton createMenuButton(Map<String, List<String>> map, double x, double y, double prefWidth) {
        MenuButton menuButton = new MenuButton();

        for (String key : map.keySet()) {
            for (String item : map.get(key)) {
                MenuItem menuItem = new MenuItem(item);
                menuItem.setOnAction(e -> menuButton.setText(menuItem.getText()));
                menuButton.getItems().add(menuItem);
            }
            menuButton.getItems().add(new SeparatorMenuItem());
        }

        menuButton.setLayoutX(x);
        menuButton.setLayoutY(y);
        menuButton.setPrefWidth(prefWidth);
        return menuButton;
    }

    public static MenuButton createMenuButton(double x, double y, double prefWidth) {
        MenuButton menuButton = new MenuButton();
        menuButton.setLayoutX(x);
        menuButton.setLayoutY(y);
        menuButton.setPrefWidth(prefWidth);
        return menuButton;
    }

    public static MenuButton createMenuButton(String text, double x, double y, double prefWidth) {
        MenuButton menuButton = new MenuButton(text);
        menuButton.setLayoutX(x);
        menuButton.setLayoutY(y);
        menuButton.setPrefWidth(prefWidth);
        return menuButton;
    }

    public static void modifyMenuItemList(MenuButton menuButton, List<String> items) {
        menuButton.getItems().clear();
        for (String item: items) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(e -> menuButton.setText(menuItem.getText()));
            menuButton.getItems().add(menuItem);
        }
    }
}
