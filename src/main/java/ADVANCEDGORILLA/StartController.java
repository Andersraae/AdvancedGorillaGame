package ADVANCEDGORILLA;

//**************************************************************
// StartController
// Klassen håndterer datainput fra spilleren på startskærmen
//
// Lavet af Andreas
//**************************************************************

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class StartController implements Initializable {

    public static String namePlayer1,namePlayer2;
    public static int PlayingTo;
    public static double gravity;
    @FXML
    private TextField TextNamePlayer1,TextNamePlayer2,TextPlayingTo,TextGravity;

    //manuel kast
    @FXML
    private CheckBox CheckBoxManuelKast;
    public static boolean manuelKast;

    //vind
    @FXML
    private ComboBox ComboBoxWind;
    public static int windDifficulty;

    //AI
    @FXML
    private ComboBox ComboBoxPlayer1AI, ComboBoxPlayer2AI;
    public static int player1AI,player2AI;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ComboBoxPlayer1AI.getItems().addAll("off",1,2,3,4,5);
        ComboBoxPlayer2AI.getItems().addAll("off",1,2,3,4,5);
        ComboBoxWind.getItems().addAll(0,1,2,3,4,5);
    }

    //Kaldes når Start-knappen klikkes
    public void startGame(){
        try{
            setValues(); // evaluerer datainput

            //Hverken antal runder eller gravity må være negativ
            if (PlayingTo > 0 && gravity > 0){
                GameApplication.setStage("game-view.fxml");
                System.out.println(windDifficulty);
            }

        } catch (Exception e){
            System.out.println(e);
        }
    }

    //Andreas
    //Tjekker de indtastede værdier og ser om de er efterladt tomme
    //Hvis et felt er tomt, vil de blive sat til standardværdier
    public void setValues(){

        //Sæt spillernavne
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

        //Sæt antallet af point der spilles til
        if (TextPlayingTo.getText().length() == 0){
            PlayingTo = 3;
        } else{
            PlayingTo = Integer.parseInt(TextPlayingTo.getText());
        }

        //Sæt gravity
        if (TextGravity.getText().length() == 0){
            gravity = 9.81;
        } else{
            gravity = Double.parseDouble(TextGravity.getText());
        }

        //sæt AI
        if (ComboBoxPlayer1AI.getValue() == null || ComboBoxPlayer1AI.getValue().equals("off")){
            player1AI = 0;
        } else {
            player1AI = (int) ComboBoxPlayer1AI.getValue();
        }

        if (ComboBoxPlayer2AI.getValue() == null || ComboBoxPlayer2AI.getValue().equals("off")){
            player2AI = 0;
        } else {
            player2AI = (int) ComboBoxPlayer2AI.getValue();
        }

        manuelKast = CheckBoxManuelKast.isSelected(); //brug manuel kast hvis det er valgt

        //evaluer valg af sværhedsgrad af vind
        if (ComboBoxWind.getValue() == null){
            windDifficulty = 0;
        } else {
            windDifficulty = (int) ComboBoxWind.getValue();
        }



    }
}
