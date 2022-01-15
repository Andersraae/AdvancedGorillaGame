package ADVANCEDGORILLA;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//*********************************************
// GameoverController
// Klassens opgave er at vise spillernes navne
// og score, efter spillet er overstået.
// Kan også genstarte spillet
//
// Lavet af Andreas
//********************************************

public class GameoverController implements Initializable {

    @FXML
    private Label labelPlayer1, labelPlayer2; //label til spillernes navne og score

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelPlayer1.setText(GameController.player1.getName() + ": \t" + GameController.player1.getPoint());
        labelPlayer2.setText(GameController.player2.getName() + ": \t" + GameController.player2.getPoint());
    }

    //lukker vinduet og åbner startskærmen, hvor spilleren kan starte det næste spil
    public void reset() throws IOException {
        GameApplication.setStage("start-screen.fxml");
    }
}
