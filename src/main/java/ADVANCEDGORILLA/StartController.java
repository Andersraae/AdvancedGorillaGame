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

    public static String namePlayer1,namePlayer2; //brugerindstastede spillernavne
    public static int PlayingTo; //spillet spillet indtil en spiller har nået denne værdi
    public static double gravity; // tyngdeacceleration
    @FXML
    private TextField TextNamePlayer1,TextNamePlayer2,TextPlayingTo,TextGravity; //textbokse til brugerindput

    //manuel kast
    @FXML
    private CheckBox CheckBoxManuelKast; // checkbox tjekkes hvis manuel kast skal anvendes
    public static boolean manuelKast; // true, når manuel kast er valgt

    //vind
    @FXML
    private ComboBox ComboBoxWind; // combobox for valg af vind
    public static int windDifficulty; //variabel for vindens sværhedsgrad

    //AI
    @FXML
    private ComboBox ComboBoxPlayer1AI, ComboBoxPlayer2AI; //combobokse for valg af AI
    public static int player1AI,player2AI; // variable med AI level; 0 = slået fra; 5 = max

    @FXML
    private TextField TextSizeX, TextSizeY; // tekstfelte hvor brugeren skriver størrelsen af spillets bane
    public static int sizeX,sizeY; // variable med brugerindtastet skærmstørrelse
    private int minX = 200, minY = 200; // minimum skærmstørrelse

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ComboBoxPlayer1AI.getItems().addAll("off",1,2,3,4,5); //setup valgmuligheder for AI
        ComboBoxPlayer2AI.getItems().addAll("off",1,2,3,4,5);
        ComboBoxWind.getItems().addAll(0,1,2,3,4,5); // setup valmuligheder for vind
    }

    //Kaldes når Start-knappen klikkes
    public void startGame(){
        try{
            setValues(); // evaluerer datainput

            //Hverken antal runder eller gravity må være negativ
            if (PlayingTo > 0 && gravity > 0 && sizeX >= minX && sizeY >= minY){
                GameApplication.setStage("game-view.fxml",sizeX,sizeY); // åbner scenen med størrelse (x,y)
            }

        } catch (Exception e){
            System.out.println(e);
        }
    }

    //Andreas
    //Tjekker de indtastede værdier og ser om de er efterladt tomme
    //Hvis et felt er tomt, vil de blive sat til standardværdier
    public void setupStageSize(){
        try{
            sizeX = Integer.parseInt(TextSizeX.getText()); //skærmstørrelse i x-retning
            sizeY = Integer.parseInt(TextSizeY.getText()); //skærmstørrelse i y-retning
        } catch (Exception e){
            if (TextSizeX.getText().length() == 0){
                sizeX = 600; //sætter x til 600, hvis input er tomt
            }

            if(TextSizeY.getText().length() == 0){
                sizeY = 400; //sætter y til 400, hvis input er tomt
            }
        }
    }

    //Andreas
    //Tjekker de indtastede værdier og ser om de er efterladt tomme
    //Hvis et felt er tomt, vil de blive sat til standardværdier
    public void setValues(){

        setupStageSize();

        //Sæt spillernavne
        if (TextNamePlayer1.getText().length() == 0){
            namePlayer1 = "Player 1"; //standard navn for spiller 1
        } else{
            namePlayer1 = TextNamePlayer1.getText(); // sæt navnet til input
        }

        if (TextNamePlayer2.getText().length() == 0){
            namePlayer2 = "Player 2";  //standard navn for spiller 2
        } else{
            namePlayer2 = TextNamePlayer2.getText();// sæt navnet til input
        }

        //Sæt antallet af point der spilles til
        if (TextPlayingTo.getText().length() == 0){
            PlayingTo = 3; // standard er først til 3
        } else{
            PlayingTo = Integer.parseInt(TextPlayingTo.getText()); // der spilles til det antal brugeren indstater
        }

        //Sæt gravity
        if (TextGravity.getText().length() == 0){
            gravity = 9.81; // standard tyngdeacceleration
        } else{
            gravity = Double.parseDouble(TextGravity.getText()); // g sættes til brugerindtaset værdi
        }

        //sæt AI
        if (ComboBoxPlayer1AI.getValue() == null || ComboBoxPlayer1AI.getValue().equals("off")){
            player1AI = 0; // AI er slået fra
        } else {
            player1AI = (int) ComboBoxPlayer1AI.getValue(); // AI er slået til hvis > 0
        }

        if (ComboBoxPlayer2AI.getValue() == null || ComboBoxPlayer2AI.getValue().equals("off")){
            player2AI = 0; // AI er slået fra
        } else {
            player2AI = (int) ComboBoxPlayer2AI.getValue(); // AI er slået til hvis > 0
        }

        manuelKast = CheckBoxManuelKast.isSelected(); //brug manuel kast hvis det er valgt

        //evaluer valg af sværhedsgrad af vind
        if (ComboBoxWind.getValue() == null){
            windDifficulty = 0; // vind er normalt slået fra
        } else {
            windDifficulty = (int) ComboBoxWind.getValue(); //vinden sættes til den vaglte sværhedsgrad
        }
    }
}
