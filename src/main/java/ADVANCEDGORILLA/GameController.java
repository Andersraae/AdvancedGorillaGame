package ADVANCEDGORILLA;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;

public class GameController {
    private static final int CANVAS_X = 600;
    private static Player player1 = new Player(0, 0, "p1");
    private static Player player2 = new Player(CANVAS_X - 1, 0, "p2");
    private static Projectile proj = new Projectile(0,0);
    private static final double g = 9.81;
    private static final int STEPS = 20;
    private boolean hasTurnP1 = true;

    public Label player1point;
    public Label player2point;
    @FXML
    private Circle projectile;
    @FXML
    private TextField angle;
    @FXML
    private TextField velocity;

    //Anders
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
    public void simulateProjectile(Player shootingPlayer, Player targetPlayer, double ANGLE_IN_DEGREES, double VELOCITY){
        double angle = Math.toRadians(ANGLE_IN_DEGREES);
        double xVelocity = VELOCITY * Math.cos(angle);
        double yVelocity = VELOCITY * Math.sin(angle);
        double totalTime = - 2.0 * yVelocity / -g;
        double timeIncrement = totalTime / STEPS;
        double xIncrement = xVelocity * timeIncrement;
        double x = shootingPlayer.getX();
        double y = shootingPlayer.getY();
        double t = 0.0;
        int stepCounter;

        System.out.println("step\tx \t y \t time \t length");
        System.out.println("0\t0.0\t\t0.0\t\t0.0");

        for (stepCounter= 1; stepCounter <= STEPS; stepCounter++) {
            t += timeIncrement;
            x += xIncrement;
            y = yVelocity * t + 0.5 * -g * t * t;
            proj.setX(x);
            proj.setY(y);
            projectile.setCenterX(x);
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
}
