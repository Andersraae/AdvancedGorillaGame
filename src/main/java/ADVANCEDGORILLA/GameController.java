package ADVANCEDGORILLA;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class GameController implements Initializable {

    //Variable der ikke skal ændres
    private static final int CANVAS_X = 600;
    private static Projectile proj = new Projectile(0,0);
    private static int totalSteps = 20;
    private static boolean hasTurnP1 = true;
    private static Player winner = new Player(-1,-1,"null");
    private static boolean winnerFound = false;

    //Variable fra startScreen
    private static int FirstTo = StartController.PlayingTo;
    public static Player player1 = new Player(0, 0, StartController.namePlayer1);
    public static Player player2 = new Player(CANVAS_X - 1, 0, StartController.namePlayer2);
    private static double g = StartController.gravity;

    //variable fra game-view ift point og navne
    @FXML
    public Label namePlayer1, namePlayer2,player1point, player2point;


    public Scene root;
    @FXML
    private Circle projectile;

    //Manuel kast
    @FXML
    private TextField angle, velocity;

    //Visuel kast
    public Polygon indicator;
    public Label visualangle, visualvelocity;
    public double xdiff,ydiff,throwvelocity,throwangledeg,displayangle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reset();
        namePlayer1.setText(player1.getName());
        namePlayer2.setText(player2.getName());
    }

    public static void reset(){
        hasTurnP1 = true;
        proj = new Projectile(0,0);
        winner = new Player(-1,-1,"null");
        winnerFound = false;
        FirstTo = StartController.PlayingTo;
        player1 = new Player(0, 0, StartController.namePlayer1);
        player2 = new Player(CANVAS_X - 1, 0, StartController.namePlayer2);
        g = StartController.gravity;
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

        if (!hasTurnP1){
            displayangle = 180 - throwangledeg;
        }

        throwvelocity /= 4; //Gør det nemmere at styre hastigheden
        
        visualangle.setText("Vinkel: " + round(displayangle));
        visualvelocity.setText("Hastighed: " + round(throwvelocity));

        indicator.getPoints().setAll(
          0.0,0.0,
          xdiff / 4 - 8 , - ydiff / 4 - 8,
          xdiff / 4 + 8 , - ydiff / 4 + 8
        );

    }

    //Anders
    //Til visuelt kast
    @FXML
    private void onMouseClick(MouseEvent event) throws IOException, InterruptedException {
        //Print til test
        System.out.println("click");

        System.out.println(event.getX());
        System.out.println(event.getY());

        System.out.println(projectile.getLayoutX());
        System.out.println(projectile.getLayoutY());

        //Kode
        if(hasTurnP1){ //player 1 har tur
            simulateProjectile(player1, player2, throwangledeg, throwvelocity);
        } else { //player 2 har tur
            simulateProjectile(player2, player1, throwangledeg, -throwvelocity);
        }
        indicator.setLayoutX(projectile.getLayoutX());
        indicator.setLayoutY(projectile.getLayoutY());

        System.out.println("xdiff: " + xdiff + " ydiff: " + ydiff + " power: " + throwvelocity + " angle: " + throwangledeg); //Test

    }

    //Anders
    //Til kast knappen
    public void kast(){
        try {
            double numangle = Double.parseDouble(angle.getText());
            double numvelocity = Double.parseDouble(velocity.getText());

            //player 1 har tur
            if(hasTurnP1){
                simulateProjectile(player1, player2, numangle, numvelocity);
            } else { //player 2 har tur
                simulateProjectile(player2, player1, -numangle, -numvelocity);
            }

            //Fjerner værdier fra sidste spiller
            angle.clear();
            velocity.clear();

        } catch (Exception e){
            System.out.println(e);
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

        //status på point
        pointStatus(player1);
        pointStatus(player2);

        //tjekker om vinder er fundet
        if (winnerFound){
            System.out.println(winner.getName() + " har vundet!");
            GameApplication.setStage("gameover-screen.fxml");
        } else{
            //skifte tur
            if (hasTurnP1){
                hasTurnP1 = false;
            } else {
                hasTurnP1 = true;
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


}
