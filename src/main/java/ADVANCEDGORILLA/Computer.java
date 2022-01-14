package ADVANCEDGORILLA;
import java.util.ArrayList;
import java.util.Random;

// TODO: vind

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
    private static double g = GameController.g;
    private int val;
    public static double dy;
    private int lowAngle;
    private static int highestAngle = 89;

    //konstruktør
    public Computer(Player shooter, Player target){
        this.shooter = shooter;
        this.target = target;
        this.moves = new ArrayList<>();
        this.currentGuessCounter = 0;
        this.difficulty = 0;
        this.lowAngle = 1;

        //setup side
        this.val = 1; //1=venstre, -1=højre
        if (this.shooter.equals(GameController.player2)) {
            this.val = -1;
        }
    }

    //kaldes hver gang spillet startes
    public void setup(int difficulty) throws InterruptedException {

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

    //computer har brugt alle sine gæt og laver nye gæt
    //kaldes når computeren rammer en bygning med sit sidste gæt
    public void calculateNewMoves(){
        if(this.currentGuessCounter + 1 >= this.moves.size()){
            this.lowAngle = 45;
            this.moves.clear();
            calulateMoves();
            this.currentGuessCounter = 0;
        }
    }

    //beregner alle de gæt der skal til for at ramme modspilleren
    //og føjer dem til listen moves
    public void calulateMoves(){
        //constants and variables
        Random random = new Random();
        int currentGuess = 1;

        //generate random correct guess
        Guess correct;
        int angleInDegrees = random.nextInt(highestAngle - this.lowAngle) / 2 * 2 + this.lowAngle; //temporary
        double a = Math.toRadians(angleInDegrees);
        double dx = Math.abs(target.getX() - shooter.getX());
        dy = shooter.getY() - target.getY();
        double udtryk = 2*(dx*Math.pow(Math.sin(a),3)-dy*Math.pow(Math.cos(a),3)-dx*Math.sin(a));
        double v0 = Math.abs((Math.sqrt(-udtryk*Math.cos(a)*g)*dx/udtryk));
        correct = new Guess(angleInDegrees, v0);

        //scale bounds with difficulty
        int totalAngles = (int) (89 - this.difficulty * 86.0 / 5);
        int totalVelocity = (int) (413 - this.difficulty * 410.0 / 5);

        //set lower and upper bounds
        int angleLower, angleUpper;
        double velcocityLower, velocityUpper;
        int tal = random.nextInt(totalAngles);
        angleLower = correct.getAngle() - tal;
        angleUpper = correct.getAngle() + totalAngles - tal;
        if (angleLower < this.lowAngle) {
            angleUpper += Math.abs(angleLower) + 1;
            angleLower = this.lowAngle;
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
        double l = Math.sqrt(y*y) - Math.abs(dy);
        return l <= 1;
    }
}