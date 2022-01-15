package ADVANCEDGORILLA;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

//*******************************************
//GameApplication
//Klassens opgave er at åbne og lukke vinduer
//
//Lavet af Andreas
//*******************************************

public class GameApplication extends Application{

    private static Stage currentStage; // den nuværende stage
    private static boolean firstStage = true; //true imens det første vindue er åbent

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        setStage("start-screen.fxml"); //sætter scenen til startscenen
    }

    //Kaldes når der skal skiftes scene
    //Sætter scenen til den fxml fil der har parameteren som filnavn
    public static void setStage(String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource(resource));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        closeStageIfNotFirst();
        currentStage = stage;
    }

    //Sørger for at den første scene ikke lukker med det samme
    public static void closeStageIfNotFirst(){
        if (firstStage == true){
            firstStage = false;
        } else {
            currentStage.close();
        }
    }
}
