package ADVANCEDGORILLA;
import java.util.ArrayList;
import java.util.Random;

// TODO: vind, bygninger, manuel kast knap

/*
dif:1 gns:11.322
dif:2 gns:9.583
dif:3 gns:7.897
dif:4 gns:5.717
dif:5 gns:2.041
 */


//Lavet af Andreas
public class Computer{

    //klassevariable
    private Player shooter;
    private Player target;
    private ArrayList<Guess> moves;
    private int currentGuessCounter;
    private int difficulty;
    private static double g = 9.81;
    private int val;

    //konstruktør
    public Computer(Player shooter, Player target){
        this.shooter = shooter;
        this.target = target;
        this.moves = new ArrayList<>();
        this.currentGuessCounter = 0;
        this.difficulty = 0;

        //setup side
        this.val = 1; //1=venstre, -1=højre
        if (this.shooter.equals(GameController.player2)) {
            this.val = -1;
        }
    }

    //kaldes hver gang spillet startes
    public void setup(int difficulty){

        //fjerner tidligere gæt
        this.moves.clear();

        //beregner alle gæt, hvis computeren er aktiveret
        if(difficulty > 0){
            this.difficulty = difficulty;
            calulateMoves();
        }
    }

    //retunerer det næste gæt
    public Guess getNextMove(){
        Guess move= this.moves.get(this.currentGuessCounter);
         if (this.currentGuessCounter + 1 < this.moves.size()){
            this.currentGuessCounter++;
         }
         return move;
    }

    //beregner alle de gæt der skal til for at ramme modspilleren
    //og føjer dem til listen moves
    public void calulateMoves() {
        //constants and variables
        Random random = new Random();
        int currentGuess = 1;

        //find the lowest possible
        int angleInDegrees = random.nextInt(89) / 2 * 2 + 1; //temporary
        double angleInRadians = Math.toRadians(angleInDegrees);

        //generate random correct guess
        double num = Math.abs(target.getX() - shooter.getX());
        double udtryk = 2 * Math.sin(angleInRadians) * (Math.sin(angleInRadians) * Math.sin(angleInRadians) - 1);
        double velocity = Math.abs((Math.sqrt(-udtryk * Math.cos(angleInRadians) * g * num) / udtryk));
        Guess correct = new Guess(angleInDegrees, velocity);

        //scale bounds with difficulty
        int totalAngles = (int) (89 - this.difficulty * 86.0 / 5);
        int totalVelocity = (int) (413 - this.difficulty * 410.0 / 5);

        //set lower and upper bounds
        int angleLower, angleUpper;
        double velcocityLower, velocityUpper;
        int tal = random.nextInt(totalAngles);
        angleLower = correct.getAngle() - tal;
        angleUpper = correct.getAngle() + totalAngles - tal;
        if (angleLower < 1) {
            angleUpper += Math.abs(angleLower) + 1;
            angleLower = 1;
        }

        tal = random.nextInt(totalVelocity);
        velcocityLower = (int) (correct.getVelocity() - tal);
        velocityUpper = (int) (correct.getVelocity() + totalVelocity - tal);
        if (velcocityLower < 1) {
            velocityUpper += Math.abs(velcocityLower) + 1;
            velcocityLower = 1;
        }

        //guess random odd angle and velocity
        int guessedAngle = random.nextInt(angleUpper - angleLower) + angleLower;
        double guessedVelocity = random.nextInt((int) (velocityUpper - velcocityLower)) + velcocityLower;
        this.moves.add(new Guess(guessedAngle, guessedVelocity));

        //while projectile doesn't hit player
        while (!playerIsHit(this.moves.get(currentGuess - 1))) {

            //evaluate angle
            //if too high, lowerBound<correct<prevAnswer
            if (correct.getAngle() < guessedAngle) {
                angleUpper = guessedAngle - 1;
            }

            //if too low, answer<correct<upperbound
            if (correct.getAngle() > guessedAngle) {
                angleLower = guessedAngle + 1;
            }

            //evaluate velocity
            //if too high, lowerBound<correct<prevAnswer
            if (correct.getVelocity() < guessedVelocity) {
                velocityUpper = guessedVelocity - 1;
            }

            //if too low, answer<correct<upperbound
            if (correct.getVelocity() > guessedVelocity) {
                velcocityLower = guessedVelocity + 1;
            }

            //guess the numbers in the middle
            guessedVelocity = velocityUpper - (velocityUpper - velcocityLower) / 2;
            guessedAngle = angleUpper - (angleUpper - angleLower) / 2 * 2;
            this.moves.add(new Guess(guessedAngle, guessedVelocity));

            //add one
            currentGuess++;
        }
    }

    //retunerer true hvis en spiller er ramt
    public boolean playerIsHit(Guess guess){
        Guess g = new Guess(guess.getAngle()*this.val,guess.getVelocity()*this.val);
        return playerIsHit(this.shooter,this.target,g);
    }

    //retunerer true hvis en spiller er ramt
    public static boolean playerIsHit(Player shooter,Player target,Guess guess){
        double v0 = guess.velocity;
        double a = Math.toRadians(guess.angle);
        int x = (int) (target.getX() - shooter.getX());
        int y = (int) (-g/(2*v0*v0*Math.cos(a)*Math.cos(a))*x*x+Math.tan(a)*x);
        double l = Math.sqrt(y*y);
        return l <= 12;
    }
}