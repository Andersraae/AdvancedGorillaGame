package ADVANCEDGORILLA;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class StartController{

    public static String namePlayer1,namePlayer2;
    public static int PlayingTo;
    public static double gravity;
    @FXML
    private TextField TextNamePlayer1,TextNamePlayer2,TextPlayingTo,TextGravity;

    public void startGame(){
        try{
            setValues();
            //Hverken antal runder eller gravity må være negativ
            if (PlayingTo > 0 && gravity > 0){
                GameApplication.setStage("game-view.fxml");
            }

        } catch (Exception e){
            System.out.println(e);
        }
    }

    //Andreas
    //Tjekker de indtastede værdier og ser om de er efterladt tomme
    //Hvis et felt er tomt, vil de blive sat til standardværdier
    public void setValues(){
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
