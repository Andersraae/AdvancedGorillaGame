package ADVANCEDGORILLA;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StartController extends Application {

    private String namePlayer1,namePlayer2;
    private int PlayingTo;
    private double gravity;
    @FXML
    private TextField TextNamePlayer1,TextNamePlayer2,TextPlayingTo,TextGravity;

    @Override
    public void start(Stage primaryStage) throws Exception {

    }


    public static void setValues(){
        if (TextNamePlayer1.getText().length() == 0){
            namePlayer1 = "Player 1";
        } else{
            namePlayer1 = TextNamePlayer1.getText();
        }

        if (TextNamePlayer2.getText().length() == 0){
            namePlayer2 = "Player 2";
        } else{
            namePlayer2 = TextNamePlayer2.getText();
        }

        if (TextPlayingTo.getText().length() == 0){
            PlayingTo = 3;
        } else{
            PlayingTo = Integer.parseInt(TextPlayingTo.getText());
        }
        if (TextGravity.getText().length() == 0){
            gravity = 9.81;
        } else{
            gravity = Double.parseDouble(TextGravity.getText());
        }
    }



}
