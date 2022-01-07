package ADVANCEDGORILLA;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class GameApplication extends Application{

    public static int PlayingTo;
    public static String namePlayer1;
    public static String namePlayer2;
    public static double gravity;
    private static Stage currentStage;
    private static boolean firstStage = true;

    public Label labelPlayer1;
    public Label labelPlayer2;

    @FXML
    private TextField TextPlayingTo;
    @FXML
    private TextField TextNamePlayer1;
    @FXML
    private TextField TextNamePlayer2;
    @FXML
    private TextField TextGravity;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        setStage("start-screen.fxml");
    }

    public void startGame(){
        try{
            StartController.setValues();

            if (PlayingTo > 0 && gravity != 0){
                setStage("game-view.fxml");
            }

        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void setStage(String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource(resource));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        if (firstStage == true){
            firstStage = false;
        } else {
            currentStage.close();
        }
        currentStage = stage;
    }






}
