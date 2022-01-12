package ADVANCEDGORILLA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// TODO: vind, bygninger, køre hvert kast for-løkke

/*
dif:1 gns:11.322
dif:2 gns:9.583
dif:3 gns:7.897
dif:4 gns:5.717
dif:5 gns:2.041
 */
public class Computer {

    public static int guessCounter;
    public static ArrayList<Guess> computerMoves = new ArrayList<>();
    public static int currentGuessNumber = 0;
    public static double g = 9.81;

    //startup
    public static void setup(int difficulty) throws InterruptedException {
        computerMoves.clear();
        Player player1 = GameController.player1;
        Player player2 = GameController.player2;
        calculateMoves(player1,player2, difficulty);
    }

    //henter næste computertræk
    public static Guess nextComputerMove(){
        Guess move= computerMoves.get(currentGuessNumber);

        if (currentGuessNumber + 1 < computerMoves.size()){
            currentGuessNumber++;
        }
        return move;
    }

    //finder et tilfældigt korrekt gæt som computeren derefter gætter sig frem til
    //gættet starter med at generere en tilfældig vinkel og derefter beregner den hastighed der skal bruges
    //derefter bliver der gættet indtil computeren gætter de to tal
    public static void calculateMoves(Player shooter, Player target, int difficulty) throws InterruptedException {

        //constants and variables
        Random random = new Random();
        guessCounter = 1;

        //setup side
        int val = 1; //1=venstre, -1=højre
        if (shooter.equals(GameController.player2)){
            val = -1;
        }

        //find the lowest possible
        int angleInDegrees = random.nextInt(89)/2*2+1; //temporary
        double angleInRadians = Math.toRadians(angleInDegrees);

        //generate random correct guess
        double num = Math.abs(target.getX() - shooter.getX());
        double udtryk = 2*Math.sin(angleInRadians)*(Math.sin(angleInRadians)*Math.sin(angleInRadians)-1);
        double velocity = Math.abs((Math.sqrt(-udtryk*Math.cos(angleInRadians)*g*num)/udtryk));
        Guess correct = new Guess(angleInDegrees,velocity);

        //scale bounds with difficulty
        int totalAngles = (int) (89 - difficulty * 86.0 / 5);
        int totalVelocity = (int) (413 - difficulty * 410.0 / 5);

        //set lower and upper bounds
        int angleLower,angleUpper;
        double velcocityLower,velocityUpper;
        int tal = random.nextInt(totalAngles);
        angleLower = correct.getAngle() - tal ;
        angleUpper = correct.getAngle() + totalAngles - tal;
        if (angleLower < 1){
            angleUpper += Math.abs(angleLower) + 1;
            angleLower = 1;
        }

        tal = random.nextInt(totalVelocity);
        velcocityLower = (int) (correct.getVelocity() - tal);
        velocityUpper = (int) (correct.getVelocity() + totalVelocity - tal);
        if (velcocityLower < 1){
            velocityUpper += Math.abs(velcocityLower) + 1;
            velcocityLower = 1;
        }

        //guess random odd angle and velocity
        int guessedAngle = random.nextInt(angleUpper - angleLower) + angleLower;
        double guessedVelocity = random.nextInt((int) (velocityUpper - velcocityLower)) + velcocityLower;
        computerMoves.add(new Guess(guessedAngle,guessedVelocity));

        //print table
//        System.out.println("num\t\tvgu\tvlo\tvco\tvup\t\tagu\talo\taco\taup");
//        System.out.print(guessCounter + "\t\t" + guessedVelocity + "\t" + velcocityLower + "\t" + (int) correct.getVelocity()
//                + "\t" + velocityUpper + "\t\t");
//        System.out.println(guessedAngle + "\t" + angleLower + "\t" + correct.getAngle()
//                + "\t" + angleUpper );

        //while projectile doesn't hit player
        while(!playerIsHit(shooter,target,computerMoves.get(guessCounter-1))){

            //evaluate angle
            //if too high, lowerBound<correct<prevAnswer
            if (correct.getAngle() < guessedAngle){
                angleUpper = guessedAngle - 1;
            }

            //if too low, answer<correct<upperbound
            if (correct.getAngle() > guessedAngle){
                angleLower = guessedAngle + 1;
            }

            //evaluate velocity
            //if too high, lowerBound<correct<prevAnswer
            if (correct.getVelocity() < guessedVelocity){
                velocityUpper = guessedVelocity - 1;
            }

            //if too low, answer<correct<upperbound
            if (correct.getVelocity() > guessedVelocity){
                velcocityLower = guessedVelocity + 1;
            }

            //guess the numbers in the middle
            guessedVelocity = velocityUpper - (velocityUpper - velcocityLower) / 2;
            guessedAngle = angleUpper - (angleUpper - angleLower) / 2 * 2;
            computerMoves.add(new Guess(guessedAngle,guessedVelocity));

            //add one
            guessCounter++;

            //print table
//            System.out.print(guessCounter + "\t\t" + guessedVelocity + "\t" + velcocityLower + "\t" +(int) correct.getVelocity()
//                    + "\t" + velocityUpper + "\t\t");
//            System.out.println(guessedAngle + "\t" + angleLower + "\t" + correct.getAngle()
//                    + "\t" + angleUpper );
        }
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