package ADVANCEDGORILLA;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//difficulty
/*
        lvl: 1 gns: 26.60568
        lvl: 2 gns: 23.33402
        lvl: 3 gns: 19.96377
        lvl: 4 gns: 16.64994
        lvl: 5 gns: 13.36712
        lvl: 6 gns: 10.1231
        lvl: 7 gns: 7.26794
        lvl: 8 gns: 5.40815
        lvl: 9 gns: 2.74748
        lvl: 10 gns: 1.50015
        */

public class Computer {
    public static int guessCounter;
    public static int maxLevel = 5;
    public static ArrayList<Guess> computerMoves;
    public static int chosenDifficulty = 3; //1-5
    public static int currentGuessNumber = 0;

    //startup
    public static void setup() throws InterruptedException {
        Player player1 = GameController.player1;
        Player player2 = GameController.player2;
        computerMoves = Computer.calculate(player1,player2,chosenDifficulty);
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
    public static ArrayList<Guess> calculate(Player shooter, Player target, int difficulty) throws InterruptedException {

        //variables
        ArrayList<Guess> list = new ArrayList<>();
        int range = (int)Math.ceil(Math.pow(10,maxLevel-difficulty)) / 2 + 1;
        Random random = new Random();
        guessCounter = 1;
        int side = 1;

        if (shooter.equals(GameController.player2)){
            side = -1;
        }

        //find correct answer
        //target.setX(599);
        int num = (int) Math.abs(target.getX() - shooter.getX());

        int correct = (int) Math.sqrt(num/0.1019367992);

        //find upper and lower bound
        int lower = correct - range;
        int upper = correct + range;

        if (lower < 0){
            lower = 0;
        }

        if (upper > GameController.CANVAS_X){
            upper = GameController.CANVAS_X -1;
        }

        //System.out.println("num\tgue\tlow\tcor\tupp");

        //generate random velocity between upper and lower bound
        int guess = random.nextInt(upper - lower) + lower;
        list.add(new Guess(45,guess));

        //print table
        //System.out.println(guessCounter + "\t" + guess + "\t" + lower + "\t" + correct + "\t" + upper );

        //while number is incorrect
        while(!playerIsHit(shooter,target, side*list.get(guessCounter-1).getAngle(),side*list.get(guessCounter-1).getVelocity())){

            //TimeUnit.SECONDS.sleep(1);

            //if too high, lowerBound<correct<prevAnswer
            if (correct < guess){
                upper = guess - 1;
            }

            //if too low, answer<correct<upperbound
            if (correct > guess){
                lower = guess + 1;
            }

            //guess the number in the middle
            guess = upper - (upper - lower) / 2;
            list.add(new Guess(45,guess));

            //add one
            guessCounter++;

            //print table
           // System.out.println(guessCounter + "\t" + guess + "\t" + lower + "\t" + correct + "\t" + upper );
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

