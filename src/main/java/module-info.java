module ADVANCEDGORILLA.advancedgorillagame {
    requires javafx.controls;
    requires javafx.fxml;


    opens ADVANCEDGORILLA to javafx.fxml;
    exports ADVANCEDGORILLA;
}