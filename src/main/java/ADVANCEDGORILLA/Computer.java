package ADVANCEDGORILLA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// TODO: vind, bygninger, køre hvert kast for-løkke

/*
diff:1 gns:5.44115
diff:2 gns:5.02417
diff:3 gns:4.5096
diff:4 gns:3.69276
diff:5 gns:1.44226
 */
public class Computer {

    public static int guessCounter;
    public static ArrayList<Guess> computerMoves;
    public static int currentGuessNumber = 0;

    //startup
    public static void setup(int difficulty) throws InterruptedException {
        Player player1 = GameController.player1;
        Player player2 = GameController.player2;
        computerMoves = Computer.calculate(player1,player2,difficulty);
    }

    //henter næste computertræk
    public static Guess nextComputerMove(){
        Guess move= computerMoves.get(currentGuessNumber);

        if (currentGuessNumber + 1 < computerMoves.size()){
            currentGuessNumber++;
        }
        return move;
    }

    //beregner alle de træk der skal til for at computeren rammer modspilleren
    //retunerer alle træk i en liste
    private static double g = 9.81;
    private static int angleInDegrees = 20;

    public static ArrayList<Guess> calculate(Player shooter, Player target, int difficulty){
        //variables
        ArrayList<Guess> list = new ArrayList<>();
        Random random = new Random();
        guessCounter = 1;
        int side = 1;
        double angleInRadians = Math.toRadians(angleInDegrees);

        if (shooter.equals(GameController.player2)){
            side = -1;
        }

        //find correct answer
        int num = (int) Math.abs(target.getX() - shooter.getX());
        double udtryk = 2*Math.sin(angleInRadians)*(Math.sin(angleInRadians)*Math.sin(angleInRadians)-1);
        int correct = (int) Math.abs((Math.sqrt(-udtryk*Math.cos(angleInRadians)*g*num)/udtryk));

        //find upper and lower bound
        int totalNumbers = 150 - 30 * difficulty + 3;
        //System.out.println(difficulty);
        int rand = random.nextInt(totalNumbers);
        int lower = correct - rand;
        int upper = correct + totalNumbers - rand;
        if (lower < 1){
            upper += Math.abs(lower);
            lower = 1;
        }
        //System.out.println("num\tgue\tlow\tcor\tupp");

        //generate random velocity between upper and lower bound
        int guessedVelocity = random.nextInt(upper - lower) + lower; // t
        list.add(new Guess(angleInDegrees,guessedVelocity));

        //print table
        //System.out.println(guessCounter + "\t" + guessedVelocity + "\t" + lower + "\t" + correct + "\t" + upper );

        //while number is incorrect
        while(!playerIsHit(shooter,target, side*list.get(guessCounter-1).getAngle(),side*list.get(guessCounter-1).getVelocity())){

            //if too high, lowerBound<correct<prevAnswer
            if (correct < guessedVelocity){
                upper = guessedVelocity - 1;
            }

            //if too low, answer<correct<upperbound
            if (correct > guessedVelocity){
                lower = guessedVelocity + 1;
            }

            //guess the number in the middle
            guessedVelocity = upper - (upper - lower) / 2;
            list.add(new Guess(angleInDegrees,guessedVelocity));

            //add one
            guessCounter++;

            //print table
            // System.out.println(guessCounter + "\t" + guessedVelocity + "\t" + lower + "\t" + correct + "\t" + upper );
        }
        return list;
    }

    //tager 2 spillere, en vinkel og hastighed
    //beregner hvor projektilen rammer jorden og derefter beregner afstanden skal spilleren der sigtes efter
    //lige nu kigges der kun på slutpunktet og ikke på punkter der ligger
    public static boolean playerIsHit(Player shooter, Player target, double angleInDegrees, double velocity){
        double angle = Math.toRadians(angleInDegrees);
        double xVelocity = velocity * Math.cos(angle);
        double yVelocity = velocity * Math.sin(angle);
        double g = 9.81;
        double totalTime = -2.0 * yVelocity / -g;
        double dx = xVelocity * totalTime;
        double x = shooter.getX();
        double y = shooter.getY();

        x += dx;
        y = yVelocity * totalTime + 0.5 * -g * totalTime * totalTime;
        GameController.proj.setX(x);
        GameController.proj.setY(y);
        double l = target.distanceToProjectile(GameController.proj);
        return l <= GameController.CANVAS_X / 50;
    }
}