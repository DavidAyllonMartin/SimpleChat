package org.ielena.simplechat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.ielena.simplechat.controllers.LoginController;

import java.io.IOException;
import java.util.Objects;

public class RunClient extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(RunClient.class.getResource("views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Image icon = new Image(Objects.requireNonNull(RunClient.class.getResourceAsStream("images/simple-chat-icon.png")));
        stage.getIcons().add(icon);
        LoginController loginController = fxmlLoader.getController();
        loginController.setStage(stage);
        stage.setTitle("Simple Chat");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}