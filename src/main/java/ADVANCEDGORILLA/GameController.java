package ADVANCEDGORILLA;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
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
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


public class GameController implements Initializable {

    //Variable der ikke skal ændres
    public static final int CANVAS_X = 600;
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

    //variable fra game-view ift point og navne
    @FXML
    public Label namePlayer1, namePlayer2,player1point, player2point;

    public boolean Executed = false;
    public Scene root;
    public ImageView abe1;
    public ImageView BA;
    public ImageView abeKast;
    @FXML
    private Circle projectile;

    //Manuel kast
    @FXML
    private TextField angle, velocity;

    //Visuel kast
    public Polygon indicator;
    public Label visualangle, visualvelocity;
    public double xdiff,ydiff,throwvelocity,throwangledeg,displayangle;

    //Vind
    public double winddirection, windforce; //TODO: Tilføj sværhedsgrad og skaler vinden op efter det

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reset();
        namePlayer1.setText(player1.getName());
        namePlayer2.setText(player2.getName());
        windforce = Wind.changeWindForce();
        winddirection = Wind.changeWindDirection();

        //Christian
        /* kør kun startpos en gang!
        if(!Executed) {
            BA.setLayoutX(0);
            BA.setLayoutY(360);
            Executed = true;
        }
        */

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

    public static void reset(){
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
    }

    //Anders
    //Til visuelt kast
    @FXML
    private void onMouseMove(MouseEvent event){
        xdiff = event.getX() - projectile.getLayoutX();
        ydiff = projectile.getLayoutY() - event.getY();
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

        indicator.getPoints().setAll(
          0.0,0.0,
          xdiff / 4 - 0.1 * xdiff , - ydiff / 4 - 0.1 * ydiff,
          xdiff / 4 + 0.1 * xdiff , - ydiff / 4 + 0.1 * ydiff
        );
    }

    //Anders
    //Til visuelt kast
    @FXML
    private void onMouseClick(MouseEvent event) throws IOException, InterruptedException {
        //Tur
        if(player1HasTurn){ //player 1 har tur
            animateProjectile(player1, player2, throwangledeg, throwvelocity);
        } else { //player 2 har tur
            animateProjectile(player2, player1, throwangledeg, throwvelocity);
        }

        //projektil
        indicator.setLayoutX(projectile.getLayoutX());
        indicator.setLayoutY(projectile.getLayoutY());

        System.out.println("xdiff: " + xdiff + " ydiff: " + ydiff + " power: " + throwvelocity + " angle: " + throwangledeg); //Test
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
                    simulateProjectile(player1, player2, guess.getAngle(), guess.getVelocity());
                } else {
                    numangle = Double.parseDouble(angle.getText());
                    numvelocity = Double.parseDouble(velocity.getText());
                    simulateProjectile(player1, player2, numangle, numvelocity);
                }
            } else { //player 2 har tur
                if(player2.isComputer()){
                    Guess guess = Computer.nextComputerMove();
                    simulateProjectile(player2, player1, -guess.getAngle(), -guess.getVelocity());
                } else {
                    numangle = Double.parseDouble(angle.getText());
                    numvelocity = Double.parseDouble(velocity.getText());
                    simulateProjectile(player2, player1, -numangle, -numvelocity);
                }
            }

            //Fjerner værdier fra sidste spiller
            angle.clear();
            velocity.clear();

        } catch (Exception e){
            System.out.println(e);
        }
    }

    //Andreas (Udregningen)
    //Christian (Animation)
    public void animateProjectile(Player shootingPlayer, Player targetPlayer, double ANGLE_IN_DEGREES, double VELOCITY) throws IOException, InterruptedException{
        //Christian
        //rotation af banan :)
        RotateTransition rotationBanan = new RotateTransition();
        rotationBanan.setCycleCount(rotationBanan.INDEFINITE);
        rotationBanan.setByAngle(360);
        rotationBanan.setInterpolator(Interpolator.LINEAR);
        rotationBanan.setAutoReverse(false);
        rotationBanan.setNode(BA);

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
                projectile.setLayoutX(x);
                projectile.setCenterY(y);


                //Christian
                //sætter pos af billede til projectile Pos
                BA.setX(x); //TODO: Fiks det her igen så det passer igen
                BA.setY(110 - y);

                double l = targetPlayer.distanceToProjectile(proj);
                System.out.println("x: " + round(x) + " y: " + round(y) + " realtime: " + round(realtime) + " afstand: " + round(l));

                if (y < 0){ //Stopper animation når bananen rammer jorden
                    throwanimation.stop();
                    rotationBanan.stop();
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

    }

    //Andreas
    public void simulateProjectile(Player shootingPlayer, Player targetPlayer, double ANGLE_IN_DEGREES, double VELOCITY) throws IOException, InterruptedException {
        double angle = Math.toRadians(ANGLE_IN_DEGREES);
        double xVelocity = VELOCITY * Math.cos(angle);
        double yVelocity = VELOCITY * Math.sin(angle);
        double totalTime = - 2.0 * yVelocity / -g;
        double timeIncrement = totalTime / totalSteps;
        double xIncrement = xVelocity * timeIncrement;
        double x = shootingPlayer.getX();
        double y = shootingPlayer.getY();
        double t = 0.0;
        int stepCounter;

        System.out.println("step\tx \t y \t time \t length");
        System.out.println("0\t0.0\t\t0.0\t\t0.0");

        for (stepCounter= 1; stepCounter <= totalSteps; stepCounter++) {
            t += timeIncrement;
            x += xIncrement;
            y = yVelocity * t + 0.5 * -g * t * t;
            proj.setX(x);
            proj.setY(y);
            projectile.setLayoutX(x);
            projectile.setCenterY(y);

            double l = targetPlayer.distanceToProjectile(proj);
            System.out.println(stepCounter + "\t" + round(x) + "\t" + round(y) + "\t" + round(t) + "\t" + round(l));
        }

        if (playerIsHit(targetPlayer)){
            shootingPlayer.addPoint(1);
            System.out.println(targetPlayer.getName() + " is hit!");
            player1point.setText(Integer.toString(player1.getPoint()));
            player2point.setText(Integer.toString(player2.getPoint()));
        }

        //Christian
        //sætter pos af billede til projectile Pos
        BA.setX(projectile.getLayoutX()-100);
        BA.setY(projectile.getLayoutY()-290);

        //status på point
        System.out.println();
        System.out.println("v:" + Math.abs(VELOCITY) + " a:" + Math.abs(ANGLE_IN_DEGREES));
        pointStatus(player1);
        pointStatus(player2);

        //tjekker om vinder er fundet
        if (winnerFound){
            System.out.println(winner.getName() + " har vundet!");
            GameApplication.setStage("gameover-screen.fxml");
        } else{
            //skifte tur
            if (player1HasTurn){
                player1HasTurn = false;
            } else {
                player1HasTurn = true;
            }
            System.out.println(targetPlayer.getName() + " har tur!");
        }
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
