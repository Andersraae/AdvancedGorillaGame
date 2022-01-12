package ADVANCEDGORILLA;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.shape.Line;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


public class GameController implements Initializable {

    //Variable der ikke skal ændres
    public static final int CANVAS_X = 600;
    public static final int CANVAS_Y = 400;
    public static Projectile proj = new Projectile(0,0);
    private static int totalSteps = 20;
    private static Player winner = new Player(-1,-1,"null");
    private static boolean winnerFound = false;
    private static boolean player1HasTurn = true;

    //Variable fra startScreen
    private static int FirstTo = StartController.PlayingTo;
    public static Player player1 = new Player(0, 0, StartController.namePlayer1);
    public static Player player2 = new Player(CANVAS_X - 1, 0, StartController.namePlayer2);
    private static double g = StartController.gravity;
    public static boolean usemanualthrow = false; //Kommer senere

    //variable fra game-view ift point og navne
    @FXML
    public Label namePlayer1, namePlayer2,player1point, player2point;

    public boolean Executed = false;
    public ImageView abe1;
    public ImageView abe2;
    public ImageView BA;
    public ImageView abeKast;

    //Manuel kast
    public TextField angle, velocity;
    public Label anglelabel, velocitylabel;
    public Button throwbtn;

    //Visuel kast
    public Line indicatorp1, indicatorp2;
    public Label visualangle, visualvelocity;
    public double xdiff,ydiff,throwvelocity,throwangledeg,displayangle;

    //Vind
    public double winddirection, windforce; //TODO: Tilføj sværhedsgrad og skaler vinden op efter det

    //Terræn
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Rectangle player1Rect;
    @FXML
    private Rectangle player2Rect;
    public int blockHeight = 16;
    public int blockWidth = 72;
    public int maxHeight = 8;
    public int minHeight = 2;
    public Color[] buildingColors = {Color.DARKTURQUOISE, Color.INDIANRED, Color.LIGHTGREY};
    public Building[] buildings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reset();
        namePlayer1.setText(player1.getName());
        namePlayer2.setText(player2.getName());
        windforce = Wind.changeWindForce();
        winddirection = Wind.changeWindDirection();
        resetImage();

        generateTerrain();

        //Andreas
        //test - om en spiller er computer skal afgøres i startscreen
        player1.setComputer(false);
        player2.setComputer(false);

        //setup computer
        if(player1.isComputer() || player2.isComputer()){
            try {
                Computer.setup(3); // 1-5
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void reset(){
        //setup tur
        player1HasTurn = true;
        proj = new Projectile(0,0);
        winner = new Player(-1,-1,"null");
        winnerFound = false;
        FirstTo = StartController.PlayingTo;
        player1 = new Player(0, 0, StartController.namePlayer1);
        player2 = new Player(CANVAS_X - 1, 0, StartController.namePlayer2);
        g = StartController.gravity;
        Computer.currentGuessNumber = 0;

        if (usemanualthrow){
            throwbtn.setOpacity(1);
            anglelabel.setOpacity(1);
            velocitylabel.setOpacity(1);
            angle.setOpacity(1);
            velocity.setOpacity(1);
        } else {
            visualvelocity.setOpacity(1);
            visualangle.setOpacity(1);
        }
    }

    // Generer bygninger
    public void generateTerrain() {
        Random r = new Random();

        // Tegn bygninger
        int arrLength = CANVAS_X / blockWidth;
        if (CANVAS_X % blockWidth != 0) arrLength++;
        buildings = new Building[arrLength];

        for (int i = 0; (i * blockWidth) < CANVAS_X; i++) {
            int height = r.nextInt(maxHeight - minHeight) + minHeight;
            int color = r.nextInt(buildingColors.length);

            for (int j = 0; j <= height; j++) {
                Rectangle block = new Rectangle();
                block.setLayoutX(i * blockWidth);
                block.setLayoutY(CANVAS_Y - j * blockHeight);
                block.setWidth(blockWidth);
                block.setHeight(blockHeight);
                block.setFill(buildingColors[color]);
                System.out.println("block");
                anchorPane.getChildren().add(block);

                // Tegn vinduer
                for (int k = 0; k < 6; k++) {
                    Rectangle window = new Rectangle();
                    window.setLayoutX(i * blockWidth + (blockWidth / 6) * k + (blockWidth / 6) / 3);
                    window.setLayoutY(CANVAS_Y - j * blockHeight + (blockHeight / 4));
                    window.setWidth(blockWidth / 6 / 3);
                    window.setHeight(blockHeight / 2);
                    if (r.nextInt(2) == 1) {
                        window.setFill(Color.BEIGE);
                    } else {
                        window.setFill(Color.SLATEGRAY);
                    }
                    anchorPane.getChildren().add(window);
                }
            }
            Building building = new Building(i * blockWidth, blockWidth, height * blockHeight);
            buildings[i] = building;
        }
        // TODO: sæt spilleres og projektils position


        abe1.setLayoutX(buildings[1].getX() + buildings[1].getWidth() / 2 - abe1.getFitWidth() / 2);
        abe1.setLayoutY(CANVAS_Y - (buildings[1].getHeight() + abe1.getFitHeight()));
        abe2.setLayoutX(buildings[7].getX() + buildings[7].getWidth() / 2 - abe2.getFitWidth() / 2);
        abe2.setLayoutY(CANVAS_Y - (buildings[7].getHeight() + abe2.getFitHeight()));

        player1.setX(abe1.getLayoutX() + abe1.getFitWidth() / 2);
        player1.setY(abe1.getLayoutY() + abe1.getFitHeight() / 2);
        player2.setX(abe2.getLayoutX() + abe2.getFitWidth() / 2);
        player2.setY(abe2.getLayoutY() + abe2.getFitHeight() / 2);

        proj.setX(player1.getX());
        proj.setY(player1.getY());
    }

    //Anders
    //Til visuelt kast
    @FXML
    private void onMouseMove(MouseEvent event){
        if (!usemanualthrow){
            xdiff = event.getX() - BA.getLayoutX();
            ydiff = BA.getLayoutY() - event.getY();
            throwvelocity = Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2));
            throwangledeg = Math.toDegrees(Math.acos(xdiff/throwvelocity));
            displayangle = throwangledeg;

            if (ydiff < 0){ //Hvis man peger musen under bananens position på y aksen
                displayangle = - throwangledeg;
            }

            if (!player1HasTurn){
                displayangle = 180 - throwangledeg;
            }

            throwvelocity /= 4; //Gør det nemmere at styre hastigheden

            visualangle.setText("Vinkel: " + round(displayangle));
            visualvelocity.setText("Hastighed: " + round(throwvelocity));


            if (player1HasTurn){
                indicatorp1.setEndX(xdiff/4);
                indicatorp1.setEndY(-ydiff/4);
            } else {
                indicatorp2.setEndX(xdiff/4);
                indicatorp2.setEndY(-ydiff/4);
            }
        }
    }

    //Anders
    //Til visuelt kast
    @FXML
    private void onMouseClick(MouseEvent event) throws IOException, InterruptedException {
        if (!usemanualthrow){
            //Tur
            if(player1HasTurn){ //player 1 har tur
                animateProjectile(player1, player2, throwangledeg, throwvelocity);
            } else { //player 2 har tur
                animateProjectile(player2, player1, throwangledeg, throwvelocity);
            }

            System.out.println("xdiff: " + xdiff + " ydiff: " + ydiff + " power: " + throwvelocity + " angle: " + throwangledeg); //Test
        }
    }

    //Anders
    //Til kast knappen
    public void kast() throws IOException, InterruptedException {
        namePlayer1.setText(player1.getName());
        namePlayer2.setText(player2.getName());
        try {

            double numangle, numvelocity;

            //Tur
            if(player1HasTurn){ //player 1 har tur
                if(player1.isComputer()){
                    Guess guess = Computer.nextComputerMove();
                    animateProjectile(player1, player2, guess.getAngle(), guess.getVelocity());
                } else {
                    numangle = Double.parseDouble(angle.getText());
                    numvelocity = Double.parseDouble(velocity.getText());
                    animateProjectile(player1, player2, numangle, numvelocity);
                }
            } else { //player 2 har tur
                if(player2.isComputer()){
                    Guess guess = Computer.nextComputerMove();
                    animateProjectile(player2, player1, -guess.getAngle(), -guess.getVelocity());
                } else {
                    numangle = Double.parseDouble(angle.getText());
                    numvelocity = Double.parseDouble(velocity.getText());
                    animateProjectile(player2, player1, -numangle, -numvelocity);
                }
            }

            //Fjerner værdier fra sidste spiller
            angle.clear();
            velocity.clear();

        } catch (Exception e){
            System.out.println(e);
        }
        resetImage();
    }

    //Andreas (Udregningen)
    //Christian (Animation)
    //Anders (Omskrevet udregning til animation og implementeret eksisterende animationer)
    public void animateProjectile(Player shootingPlayer, Player targetPlayer, double ANGLE_IN_DEGREES, double VELOCITY) throws IOException, InterruptedException{
        //Christian
        //rotation af banan :)
        RotateTransition rotationBanan = new RotateTransition();
        rotationBanan.setCycleCount(rotationBanan.INDEFINITE);
        rotationBanan.setByAngle(360);
        rotationBanan.setInterpolator(Interpolator.LINEAR);
        rotationBanan.setAutoReverse(false);
        rotationBanan.setNode(BA);

        //Anders (Omskrivning) Andreas (Udregning)
        //Kurve animation
        //TODO: Pas metoden til, så bananen ikke bare starter i 0,0
        Timeline throwanimation = new Timeline();
        throwanimation.setCycleCount(Timeline.INDEFINITE);
        int updatemillis = 10;
        KeyFrame bananakeyrframe = new KeyFrame(Duration.millis(updatemillis), new EventHandler<ActionEvent>() {
            double angle = Math.toRadians(ANGLE_IN_DEGREES);
            double xVelocity = VELOCITY * Math.cos(angle);
            double yVelocity = VELOCITY * Math.sin(angle);
            double totalTime = - updatemillis * 3.17 * yVelocity / -g; //Tilpasset godt og vel til funktionen med de 3.17
            double timeIncrement = totalTime / 1000;
            double xIncrement = xVelocity * timeIncrement;
            double x = shootingPlayer.getX();
            double y = shootingPlayer.getY();
            double realtime;

            @Override
            public void handle(ActionEvent event) {
                realtime += rotationBanan.getCurrentTime().toSeconds();

                x += xIncrement;
                y = yVelocity * realtime + 0.5 * -g * realtime * realtime;
                proj.setX(x);
                proj.setY(y);

                //Christian
                //sætter pos af billede til projectile Pos
                BA.setLayoutX(x); //TODO: Fiks det her igen så det passer igen
                BA.setLayoutY(400 - y);

                double l = targetPlayer.distanceToProjectile(proj);
                System.out.println("x: " + round(x) + " y: " + round(y) + " realtime: " + round(realtime) + " afstand: " + round(l));

                if (y < 0){ //Stopper animation når bananen rammer jorden
                    throwanimation.stop();
                    rotationBanan.stop();
                    resetImage();
                }

            }
        });
        throwanimation.getKeyFrames().add(bananakeyrframe);
        throwanimation.play();

        if (player1HasTurn) {
            rotationBanan.setRate(1);
            rotationBanan.play();
        } else {
            rotationBanan.setRate(-1);
            rotationBanan.play();
        }

        if (playerIsHit(targetPlayer)){
            shootingPlayer.addPoint(1);
            System.out.println(targetPlayer.getName() + " is hit!");
            player1point.setText(Integer.toString(player1.getPoint()));
            player2point.setText(Integer.toString(player2.getPoint()));
        }

        //status på point
        pointStatus(player1);
        pointStatus(player2);

        //Skifter tur og tjekker for vinder
        turnStatus();
    }

    //Christian
    public void resetImage (){
        if(player1HasTurn){
            BA.setLayoutX(100);
            BA.setLayoutY(400-60);
        }else{
            BA.setLayoutX(500);
            BA.setLayoutY(400-60);
        }
        resetIndicators();
    }

    //Andreas
    //Kaldes efter et kast er sket
    //Tager en Player og printer point til konsollen, samt tjekker om spilleren har vundet
    public void pointStatus(Player player){
        System.out.println(player.getName() + ":" + player.getPoint());

        if (player.getPoint() == FirstTo){
            winner = player;
            winnerFound = true;
        }
    }

    //Andreas
    //Kaldes når koordinater skal printes til konsollen
    //Tager en double og laver den om til 2 decimaler og retunerer den som streng
    public static String round(double a){
        return String.format("%.2f",a);
    }

    //Andreas
    //Kaldes efter et kast er sket
    //Tager en Player og retunerer true når en spiller er ramt
    public static boolean playerIsHit(Player player){
        double len = player.distanceToProjectile(proj);
        return len <= CANVAS_X/50;
    }

    //Andreas
    public void turnStatus() throws IOException {
        //tjekker om vinder er fundet
        if (winnerFound) {
            System.out.println(winner.getName() + " har vundet!");
            GameApplication.setStage("gameover-screen.fxml");
        } else {
            //skifte tur
            if (player1HasTurn){
                player1HasTurn = false;
            } else {
                player1HasTurn = true;
            }
        }
    }

    //Anders
    //Stiller indicatorene til at være
    public void resetIndicators(){
        indicatorp1.setEndY(0);
        indicatorp1.setEndX(0);
        indicatorp2.setEndY(0);
        indicatorp2.setEndX(0);

        indicatorp1.setLayoutX(BA.getLayoutX() + 20);
        indicatorp1.setLayoutY(BA.getLayoutY() + 25);
        indicatorp2.setLayoutX(BA.getLayoutX() + 20);
        indicatorp2.setLayoutY(BA.getLayoutY() + 25);
    }

/*
    final Image[] deathAnimationImages = new Image[] {};

final ImageView character = new ImageView("Kast.png");

        Duration frameDuration = Duration.seconds(1d / deathAnimationImages.length);
        Timeline deathAnimation = new Timeline(new KeyFrame(frameDuration, new EventHandler<ActionEvent>() {
private int index = 0;

@Override
public void handle(ActionEvent event) {
        character.setImage(deathAnimationImages[index]);
        index++;
        }
        }));
        deathAnimation.setCycleCount(deathAnimationImages.length);
        deathAnimation.play();
*/
}
