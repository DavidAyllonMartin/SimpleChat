package org.ielena.simplechat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ielena.simplechat.controllers.LoginController;

import java.io.IOException;

public class RunClient extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(RunClient.class.getResource("views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setStage(stage);
        stage.setTitle("Chat TCP");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}