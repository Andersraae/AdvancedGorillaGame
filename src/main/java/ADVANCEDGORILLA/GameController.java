package ADVANCEDGORILLA;
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


public class GameController {
    private static final int CANVAS_X = 600;
    public static Player player1 = new Player(0, 0, "p1");
    public static Player player2 = new Player(CANVAS_X - 1, 0, "p2");
    private static Projectile proj = new Projectile(0,0);
    private static final double g = 9.81;
    private static final int totalSteps = 20;
    private boolean hasTurnP1 = true;

    public Label player1point;
    public Label player2point;
    public Scene root;
    public ImageView abe1;
    public ImageView BA;
    public ImageView abeKast;
    @FXML
    private Circle projectile;

    //Manuel kast
    @FXML
    private TextField angle;
    @FXML
    private TextField velocity;

    //Visuel kast
    public Polygon indicator;
    public Label visualangle;
    public Label visualvelocity;
    public double xdiff;
    public double ydiff;
    public double throwvelocity;
    public double throwangledeg;
    public double displayangle;
    private boolean alreadyExecuted;


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
    private void onMouseClick(MouseEvent event){
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

            //Christian
            //rotation af banan :)
            RotateTransition rotationBanan;
            rotationBanan = new RotateTransition();
            rotationBanan.setDuration(Duration.seconds(3));
            rotationBanan.setByAngle(360);
            rotationBanan.setCycleCount(1);
            rotationBanan.setAutoReverse(false);
            rotationBanan.setNode(BA);

            RotateTransition rotationBanan2;
            rotationBanan2 = new RotateTransition();
            rotationBanan2.setDuration(Duration.seconds(3));
            rotationBanan2.setByAngle(-360);
            rotationBanan2.setCycleCount(1);
            rotationBanan2.setAutoReverse(false);
            rotationBanan2.setNode(BA);

            if(hasTurnP1 == true) {
                rotationBanan.play();
        }else{rotationBanan2.play();}

        } catch (Exception e){
            System.out.println(e);
        }
    }

/* kør kun startpos en gang!
        if(!alreadyExecuted) {
        BA.setLayoutX(0);
        BA.setLayoutY(0);
        alreadyExecuted = true;
    }
    
 */
    //Andreas
    public void simulateProjectile(Player shootingPlayer, Player targetPlayer, double ANGLE_IN_DEGREES, double VELOCITY){
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

        System.out.println();

        if (playerIsHit(targetPlayer)){
            shootingPlayer.addPoint(1);
            System.out.println(targetPlayer.getName() + " is hit!");
            player1point.setText(Integer.toString(player1.getPoint()));
            player2point.setText(Integer.toString(player2.getPoint()));
        }
        //christian
        // sætter pos af billede til projectile Pos
        BA.setX(projectile.getLayoutX()-100);
        BA.setY(projectile.getLayoutY()-290);



        //status på point
        System.out.println(player1.getName() + ":" + player1.getPoint());
        System.out.println(player2.getName() + ":" + player2.getPoint());

        //skifte tur
        if (hasTurnP1){
            hasTurnP1 = false;
        } else {
            hasTurnP1 = true;
        }

        System.out.println(targetPlayer.getName() + " har tur!");
    }


    //Andreas
    public static String round(double a){
        return String.format("%.2f",a);
    }

    //Andreas
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

